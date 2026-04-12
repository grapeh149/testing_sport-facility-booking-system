package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;
import group6.it.ou.sportfacilitybooking.dto.FacilityCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.FacilityStatus;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.entity.FacilityApprovalLog;
import group6.it.ou.sportfacilitybooking.mapper.FacilityMapper;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityApprovalLogRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {

    private static final FacilityStatus SOFT_DELETED_STATUS = FacilityStatus.SUSPENDED;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FacilityApprovalLogRepository approvalLogRepository;
    
    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    public Page<FacilityDTO> searchFacilitiesWithPagination(String search, Pageable pageable) {
        Page<Facility> facilities = facilityRepository.findByStatusAndNameContainingIgnoreCase(
            FacilityStatus.APPROVED, search, pageable);
        return facilities.map(facilityMapper::toDTO);
    }
    
    public List<FacilityDTO> searchFacilities(String keyword, String district, String city) {
        List<Facility> facilities = facilityRepository.findByStatus(FacilityStatus.APPROVED);
        
        if (keyword != null && !keyword.isEmpty()) {
            facilities = facilities.stream()
                .filter(f -> f.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                           (f.getDescription() != null && f.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
        }
        
        if (district != null && !district.isEmpty()) {
            facilities = facilities.stream()
                .filter(f -> f.getDistrict().equalsIgnoreCase(district))
                .collect(Collectors.toList());
        }
        
        if (city != null && !city.isEmpty()) {
            facilities = facilities.stream()
                .filter(f -> f.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
        }
        
        return facilities.stream()
            .map(facilityMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public FacilityDTO getFacilityDetails(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));

        if (facility.getStatus() == SOFT_DELETED_STATUS) {
            throw new RuntimeException("Facility not found");
        }

        return facilityMapper.toDTO(facility);
    }
    
    public FacilityDTO createFacility(FacilityCreateRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Facility facility = facilityMapper.toEntity(request);
        facility.setOwner(owner);
        facility.setStatus(FacilityStatus.PENDING);
        facility.setCreatedAt(LocalDateTime.now());
        facility.setUpdatedAt(LocalDateTime.now());
        
        facilityRepository.save(facility);
        
        // Send notification to all admins
        sendFacilityCreationNotificationToAdmins(facility);
        
        return facilityMapper.toDTO(facility);
    }
    
    private void sendFacilityCreationNotificationToAdmins(Facility facility) {
        try {
            // Get all admin users
            List<User> admins = userRepository.findByRole(UserRole.ADMIN);
            
            // Create notification for each admin
            for (User admin : admins) {
                Notification notification = new Notification();
                notification.setUser(admin);
                notification.setType(NotificationType.FACILITY_CREATED);
                notification.setTitle("Co so moi can duyet");
                notification.setMessage(String.format("Chu san '%s' vua tao co so '%s' can duyet", 
                    facility.getOwner().getFullName(), facility.getName()));
                notification.setRefId(facility.getId());
                notification.setRefType("FACILITY");
                notification.setIsRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                
                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            // Log error but don't throw - notification is non-critical
            System.err.println("Error sending facility creation notification: " + e.getMessage());
        }
    }

    public List<FacilityDTO> getFacilitiesByOwner(Long ownerId) {
        userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return facilityRepository.findByOwnerIdAndStatusNot(ownerId, SOFT_DELETED_STATUS).stream()
            .map(facilityMapper::toDTO)
            .collect(Collectors.toList());
    }

    public Page<FacilityDTO> getFacilitiesForAdmin(Pageable pageable, FacilityStatus status) {
        Page<Facility> facilities = status == null
            ? facilityRepository.findByStatusNot(SOFT_DELETED_STATUS, pageable)
            : facilityRepository.findByStatus(status, pageable);

        List<FacilityDTO> content = facilities.getContent().stream()
            .map(this::toAdminFacilityDTO)
            .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, facilities.getTotalElements());
    }
    
    public FacilityDTO updateFacility(Long id, FacilityCreateRequest request) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        // Only update fields that are provided (not null/empty)
        if (request.getName() != null && !request.getName().isEmpty()) {
            facility.setName(request.getName());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            facility.setAddress(request.getAddress());
        }
        if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
            facility.setDistrict(request.getDistrict());
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            facility.setCity(request.getCity());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            facility.setPhone(request.getPhone());
        }
        if (request.getDescription() != null) {
            facility.setDescription(request.getDescription());
        }
        if (request.getCommissionRate() != null) {
            facility.setCommissionRate(request.getCommissionRate());
        }
        if (request.getCancelBeforeHours() != null) {
            facility.setCancelBeforeHours(request.getCancelBeforeHours());
        }
        if (request.getAutoConfirm() != null) {
            facility.setAutoConfirm(request.getAutoConfirm());
        }
        
        // Update cover image URL if provided
        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().isEmpty()) {
            facility.setCoverImageUrl(request.getCoverImageUrl());
        }
        
        // Update open and close times if provided
        if (request.getOpenTime() != null && !request.getOpenTime().isEmpty()) {
            facility.setOpenTime(LocalTime.parse(request.getOpenTime()));
        }
        if (request.getCloseTime() != null && !request.getCloseTime().isEmpty()) {
            facility.setCloseTime(LocalTime.parse(request.getCloseTime()));
        }
        
        facility.setUpdatedAt(LocalDateTime.now());
        
        facilityRepository.save(facility);
        return facilityMapper.toDTO(facility);
    }

    public FacilityDTO updateFacilityCoverImage(Long id, String coverImageUrl) {
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            throw new RuntimeException("coverImageUrl không được để trống");
        }

        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));

        facility.setCoverImageUrl(coverImageUrl);
        facility.setUpdatedAt(LocalDateTime.now());

        facilityRepository.save(facility);
        return facilityMapper.toDTO(facility);
    }

    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));

        if (facility.getStatus() == SOFT_DELETED_STATUS) {
            return;
        }

        facility.setStatus(SOFT_DELETED_STATUS);
        facility.setUpdatedAt(LocalDateTime.now());
        facilityRepository.save(facility);
    }
    
    public void approveFacility(Long facilityId, Long adminId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        facility.setStatus(FacilityStatus.APPROVED);
        facility.setUpdatedAt(LocalDateTime.now());
        facilityRepository.save(facility);
        
        FacilityApprovalLog log = new FacilityApprovalLog();
        log.setFacility(facility);
        log.setAdmin(admin);
        log.setAction("APPROVED");
        log.setCreatedAt(LocalDateTime.now());
        approvalLogRepository.save(log);

        createOwnerNotification(
            facility,
            NotificationType.FACILITY_APPROVED,
            "Cơ sở được duyệt",
            "Cơ sở " + facility.getName() + " đã được admin phê duyệt."
        );
    }
    
    public void rejectFacility(Long facilityId, Long adminId, String reason) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));

        FacilityStatus previousStatus = facility.getStatus();
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        facility.setStatus(FacilityStatus.REJECTED);
        facility.setUpdatedAt(LocalDateTime.now());
        facilityRepository.save(facility);
        
        FacilityApprovalLog log = new FacilityApprovalLog();
        log.setFacility(facility);
        log.setAdmin(admin);
        log.setAction("REJECTED");
        log.setReason(reason);
        log.setCreatedAt(LocalDateTime.now());
        approvalLogRepository.save(log);

        boolean cancelledByAdmin = previousStatus == FacilityStatus.APPROVED;
        createOwnerNotification(
            facility,
            cancelledByAdmin ? NotificationType.FACILITY_CANCELLED_BY_ADMIN : NotificationType.FACILITY_REJECTED,
            cancelledByAdmin ? "Cơ sở bị hủy" : "Cơ sở bị từ chối",
            cancelledByAdmin
                ? "Cơ sở " + facility.getName() + " đã bị admin hủy. Lý do: " + (reason == null || reason.isBlank() ? "Không có" : reason)
                : "Cơ sở " + facility.getName() + " bị từ chối duyệt. Lý do: " + (reason == null || reason.isBlank() ? "Không có" : reason)
        );
    }

    private void createOwnerNotification(Facility facility, NotificationType type, String title, String message) {
        if (facility == null || facility.getOwner() == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(facility.getOwner());
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRefId(facility.getId());
        notification.setRefType("FACILITY");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private FacilityDTO toAdminFacilityDTO(Facility facility) {
        FacilityDTO dto = facilityMapper.toDTO(facility);
        String rejectReason = approvalLogRepository
            .findTopByFacilityIdAndActionOrderByCreatedAtDesc(facility.getId(), "REJECTED")
            .map(FacilityApprovalLog::getReason)
            .orElse(null);
        dto.setRejectReason(rejectReason);
        return dto;
    }
}

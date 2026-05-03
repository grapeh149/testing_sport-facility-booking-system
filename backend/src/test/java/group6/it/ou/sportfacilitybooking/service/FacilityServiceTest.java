package group6.it.ou.sportfacilitybooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import group6.it.ou.sportfacilitybooking.dto.FacilityCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;
import group6.it.ou.sportfacilitybooking.entity.*;
import group6.it.ou.sportfacilitybooking.mapper.FacilityMapper;
import group6.it.ou.sportfacilitybooking.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityService Unit Tests")
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FacilityMapper facilityMapper;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private FacilityApprovalLogRepository approvalLogRepository;

    @InjectMocks
    private FacilityService facilityService;

    private User owner;
    private Facility facility;
    private FacilityCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // Setup owner
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@test.com");
        owner.setFullName("Facility Owner");
        owner.setRole(UserRole.OWNER);

        // Setup facility
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");
        facility.setAddress("123 Test Street");
        facility.setDistrict("Test District");
        facility.setCity("Test City");
        facility.setPhone("0123456789");
        facility.setCommissionRate(BigDecimal.valueOf(10));
        facility.setStatus(FacilityStatus.APPROVED);
        facility.setOwner(owner);
        facility.setCreatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = new FacilityCreateRequest();
        createRequest.setName("Test Facility");
        createRequest.setAddress("123 Test Street");
        createRequest.setDistrict("Test District");
        createRequest.setCity("Test City");
        createRequest.setPhone("0123456789");
        createRequest.setOpenTime("07:00");
        createRequest.setCloseTime("23:00");
        createRequest.setCommissionRate(BigDecimal.valueOf(10));
        createRequest.setCancelBeforeHours(2);
    }

    @Test
    @DisplayName("Should create facility successfully")
    void testCreateFacilitySuccess() {
        Facility createdFacility = new Facility();
        createdFacility.setId(1L);
        createdFacility.setName("Test Facility");
        createdFacility.setOwner(owner);
        createdFacility.setStatus(FacilityStatus.PENDING);

        FacilityDTO dto = new FacilityDTO();
        dto.setId(1L);
        dto.setName("Test Facility");

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(facilityMapper.toEntity(any(FacilityCreateRequest.class))).thenReturn(createdFacility);
        when(facilityRepository.save(any(Facility.class))).thenReturn(createdFacility);
        when(facilityMapper.toDTO(createdFacility)).thenReturn(dto);
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(new User())); // simulate 1 admin

        FacilityDTO result = facilityService.createFacility(createRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Facility", result.getName());
        verify(facilityRepository, times(1)).save(any(Facility.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should handle missing admins when creating facility")
    void testCreateFacility_NoAdmins() {
        Facility createdFacility = new Facility();
        createdFacility.setId(1L);
        createdFacility.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(facilityMapper.toEntity(any(FacilityCreateRequest.class))).thenReturn(createdFacility);
        when(facilityRepository.save(any(Facility.class))).thenReturn(createdFacility);
        when(facilityMapper.toDTO(createdFacility)).thenReturn(new FacilityDTO());
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(Collections.emptyList());

        facilityService.createFacility(createRequest, 1L);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when owner not found")
    void testCreateFacilityWithInvalidOwner() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> facilityService.createFacility(createRequest, 999L));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should search facilities with pagination")
    void testSearchFacilitiesWithPagination() {
        Page<Facility> page = new PageImpl<>(List.of(facility));
        when(facilityRepository.findByStatusAndNameContainingIgnoreCase(
                eq(FacilityStatus.APPROVED), anyString(), any(Pageable.class))).thenReturn(page);
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        Page<FacilityDTO> result = facilityService.searchFacilitiesWithPagination("test", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should search facilities with filters")
    void testSearchFacilities() {
        when(facilityRepository.findByStatus(FacilityStatus.APPROVED)).thenReturn(List.of(facility));
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        List<FacilityDTO> result = facilityService.searchFacilities("test", "Test District", "Test City");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get facility details")
    void testGetFacilityDetails() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        FacilityDTO result = facilityService.getFacilityDetails(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception for suspended facility details")
    void testGetFacilityDetails_Suspended() {
        facility.setStatus(FacilityStatus.SUSPENDED);
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> facilityService.getFacilityDetails(1L));
        assertEquals("Facility not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get facilities by owner")
    void testGetFacilitiesByOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(facilityRepository.findByOwnerIdAndStatusNot(1L, FacilityStatus.SUSPENDED)).thenReturn(List.of(facility));
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        List<FacilityDTO> result = facilityService.getFacilitiesByOwner(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get facilities for admin")
    void testGetFacilitiesForAdmin() {
        Page<Facility> page = new PageImpl<>(List.of(facility));
        when(facilityRepository.findByStatusNot(FacilityStatus.SUSPENDED, PageRequest.of(0, 10))).thenReturn(page);
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        Page<FacilityDTO> result = facilityService.getFacilitiesForAdmin(PageRequest.of(0, 10), null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should update facility successfully")
    void testUpdateFacility() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);
        when(facilityMapper.toDTO(any(Facility.class))).thenReturn(new FacilityDTO());

        createRequest.setDescription("New Desc");
        createRequest.setCoverImageUrl("http://new.url");
        createRequest.setAutoConfirm(true);
        FacilityDTO result = facilityService.updateFacility(1L, createRequest);

        assertNotNull(result);
        assertEquals("New Desc", facility.getDescription());
        assertEquals("http://new.url", facility.getCoverImageUrl());
        assertTrue(facility.getAutoConfirm());
    }

    @Test
    @DisplayName("Should update facility cover image")
    void testUpdateFacilityCoverImage() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);
        when(facilityMapper.toDTO(facility)).thenReturn(new FacilityDTO());

        facilityService.updateFacilityCoverImage(1L, "http://image.url");

        assertEquals("http://image.url", facility.getCoverImageUrl());
    }

    @Test
    @DisplayName("Should soft delete facility")
    void testDeleteFacility() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        facilityService.deleteFacility(1L);

        assertEquals(FacilityStatus.SUSPENDED, facility.getStatus());
        verify(facilityRepository, times(1)).save(facility);
    }

    @Test
    @DisplayName("Should skip deleting already deleted facility")
    void testDeleteFacility_AlreadyDeleted() {
        facility.setStatus(FacilityStatus.SUSPENDED);
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        facilityService.deleteFacility(1L);

        verify(facilityRepository, never()).save(facility);
    }

    @Test
    @DisplayName("Should approve facility successfully")
    void testApproveFacility() {
        User admin = new User();
        admin.setId(2L);
        facility.setStatus(FacilityStatus.PENDING);

        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        facilityService.approveFacility(1L, 2L);

        assertEquals(FacilityStatus.APPROVED, facility.getStatus());
        verify(facilityRepository, times(1)).save(facility);
        verify(approvalLogRepository, times(1)).save(any(FacilityApprovalLog.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should reject facility successfully")
    void testRejectFacility() {
        User admin = new User();
        admin.setId(2L);
        facility.setStatus(FacilityStatus.PENDING);

        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        facilityService.rejectFacility(1L, 2L, "Missing info");

        assertEquals(FacilityStatus.REJECTED, facility.getStatus());
        verify(facilityRepository, times(1)).save(facility);
        verify(approvalLogRepository, times(1)).save(any(FacilityApprovalLog.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}

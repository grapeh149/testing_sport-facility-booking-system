package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.mapper.BookingMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.TimeSlotRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CourtRepository courtRepository;
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingMapper bookingMapper;
    
    public BookingDTO createBooking(BookingCreateRequest request, Long customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Court court = courtRepository.findById(request.getCourtId())
            .orElseThrow(() -> new RuntimeException("Court not found"));
        
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
            .orElseThrow(() -> new RuntimeException("TimeSlot not found"));
        
        // Validate booking date and time
        LocalDate today = LocalDate.now();
        if (request.getBookingDate().isBefore(today)) {
            throw new RuntimeException("Cannot book past dates. Selected date is in the past");
        }
        
        // For today bookings, validate that the timeslot hasn't started yet
        if (request.getBookingDate().isEqual(today)) {
            LocalTime now = LocalTime.now();
            LocalTime startTime = timeSlot.getStartTime();
            if (startTime != null && (startTime.isBefore(now) || startTime.equals(now))) {
                throw new RuntimeException("Cannot book past or ongoing time slot");
            }
        }

        // ✅ NEW: Validate payment time - current datetime must be before booking date + start time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(request.getBookingDate(), timeSlot.getStartTime());
        
        System.out.println("🔍 [BookingService] Payment time validation at backend:");
        System.out.println("   Current: " + now);
        System.out.println("   Booking DateTime: " + bookingDateTime);
        System.out.println("   Valid: " + now.isBefore(bookingDateTime));
        
        if (!now.isBefore(bookingDateTime)) {
            throw new RuntimeException("Thời gian hiện tại đã vượt quá thời gian đặt sân. Vui lòng chọn thời gian khác");
        }
        
        // ⚠️ CRITICAL: Check for double bookings
        long conflictCount = bookingRepository.countConflictingBookings(
            request.getCourtId(), 
            request.getBookingDate(),
            request.getTimeSlotId()
        );
        
        if (conflictCount > 0) {
            throw new RuntimeException("This time slot is already booked");
        }
        
        // Generate booking code
        String bookingCode = generateBookingCode();
        
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setCourt(court);
        
        // Get owner_id directly from database - avoid N+1 query and lazy load issues
        Long ownerId = courtRepository.findOwnerIdByCourtId(court.getId())
            .orElseThrow(() -> new RuntimeException("Facility owner not found for this court"));
        booking.setOwnerId(ownerId);
        
        booking.setTimeSlot(timeSlot);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(timeSlot.getStartTime());
        booking.setEndTime(timeSlot.getEndTime());
        booking.setTotalPrice(timeSlot.getPrice());
        booking.setDepositAmount(booking.getTotalPrice().multiply(timeSlot.getDepositRate()).divide(new BigDecimal(100)));
        
        // Fetch facility to get commission rate (single fetch)
        BigDecimal commissionRate = court.getFacility().getCommissionRate();
        booking.setCommissionAmount(booking.getTotalPrice().multiply(commissionRate).divide(new BigDecimal(100)));
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        bookingRepository.save(booking);
        
        // Create notification for customer
        createNotification(customer, NotificationType.BOOKING_CREATED, 
            "Đơn đặt sân mới", "Đơn đặt sân " + bookingCode, booking.getId(), "BOOKING");
        
        return bookingMapper.toDTO(booking);
    }
    
    public BookingDTO confirmBooking(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getCourt().getFacility().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Only facility owner can confirm booking");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        
        // Create notification
        createNotification(booking.getCustomer(), NotificationType.BOOKING_CONFIRMED,
            "Đơn đặt sân đã xác nhận", "Đơn " + booking.getBookingCode(), booking.getId(), "BOOKING");
        
        return bookingMapper.toDTO(booking);
    }
    
    public BookingDTO cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        
        // Create notification
        createNotification(booking.getCustomer(), NotificationType.BOOKING_CANCELLED,
            "Đơn đặt sân đã hủy", "Đơn " + booking.getBookingCode(), booking.getId(), "BOOKING");
        
        return bookingMapper.toDTO(booking);
    }
    
    public BookingDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        BookingDTO dto = bookingMapper.toDTO(booking);
        boolean hasReview = reviewRepository.findByBookingId(booking.getId()).isPresent();
        dto.setHasReview(hasReview);
        return dto;
    }
    
    public BookingDTO getBookingDetails(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        return bookingMapper.toDTO(booking);
    }

    public List<BookingDTO> getCourtBookings(Long courtId) {
        LocalDate today = LocalDate.now();

        return bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(courtId)
            .stream()
            .filter(booking -> !booking.getBookingDate().isBefore(today))
            .map(bookingMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public Page<BookingDTO> getCustomerBookingHistory(Long customerId, Pageable pageable) {
        return bookingRepository.findByCustomerId(customerId, pageable)
            .map(booking -> {
                BookingDTO dto = bookingMapper.toDTO(booking);
                boolean hasReview = reviewRepository.findByBookingId(booking.getId()).isPresent();
                dto.setHasReview(hasReview);
                return dto;
            });
    }
    
    // Get pending bookings waiting for owner's approval
    public Page<BookingDTO> getOwnerPendingBookings(Long ownerId, Pageable pageable) {
        return bookingRepository.findByOwner_IdAndStatus(ownerId, BookingStatus.PENDING_CONFIRM, pageable)
            .map(bookingMapper::toDTO);
    }
    
    // Get all bookings for owner
    public Page<BookingDTO> getOwnerAllBookings(Long ownerId, Pageable pageable) {
        return bookingRepository.findByOwnerId(ownerId, pageable)
            .map(booking -> {
                BookingDTO dto = bookingMapper.toDTO(booking);
                boolean hasReview = reviewRepository.findByBookingId(booking.getId()).isPresent();
                dto.setHasReview(hasReview);
                return dto;
            });
    }
    
    // Get bookings by owner and status
    public Page<BookingDTO> getOwnerBookingsByStatus(Long ownerId, BookingStatus status, Pageable pageable) {
        return bookingRepository.findByOwner_IdAndStatus(ownerId, status, pageable)
            .map(bookingMapper::toDTO);
    }
    
    // Get all pending approvals (quick count)
    public java.util.List<BookingDTO> getOwnerPendingApprovalsQuick(Long ownerId) {
        return bookingRepository.findPendingApprovalsForOwner(ownerId)
            .stream()
            .map(bookingMapper::toDTO)
            .toList();
    }
    
    @Transactional
    public void autoConfirmPendingBookingsForNextDay() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<Booking> bookingsToConfirm = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING_CONFIRM, startOfToday);
        for (Booking booking : bookingsToConfirm) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setUpdatedAt(LocalDateTime.now());
            createNotification(booking.getCustomer(), NotificationType.BOOKING_CONFIRMED,
                "Đơn đặt sân tự động xác nhận", "Đơn " + booking.getBookingCode() + " đã được hệ thống tự động xác nhận", booking.getId(), "BOOKING");
        }
        bookingRepository.saveAll(bookingsToConfirm);
        System.out.println("Auto-confirmed " + bookingsToConfirm.size() + " bookings.");
    }
    
    private String generateBookingCode() {
        return "SB-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
    
    private void createNotification(User user, NotificationType type, String title, String message, Long refId, String refType) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRefId(refId);
        notification.setRefType(refType);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}

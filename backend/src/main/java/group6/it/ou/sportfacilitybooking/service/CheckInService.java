package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.entity.CheckIn;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.CheckInMapper;
import group6.it.ou.sportfacilitybooking.repository.CheckInRepository;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    public CheckInDTO checkIn(Long bookingId, Long checkedByUserId, CheckInRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User checkedByUser = userRepository.findById(checkedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if checkedByUser is the owner of the facility
        Long facilityOwnerId = booking.getOwnerId();
        if (facilityOwnerId != null && !facilityOwnerId.equals(checkedByUserId)) {
            throw new RuntimeException("Chỉ chủ sân mới có thể check-in. Bạn không phải là chủ của sân này.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Can only check in CONFIRMED bookings");
        }

        if (booking.getBookingDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Không thể check-in trước ngày đặt sân");
        }

        // Check if already checked in
        if (checkInRepository.findByBookingId(bookingId).isPresent()) {
            throw new RuntimeException("Booking already checked in");
        }

        CheckIn checkIn = checkInMapper.toEntity(request);
        checkIn.setBooking(booking);
        checkIn.setCheckedByUser(checkedByUser);
        checkIn.setCheckedInAt(LocalDateTime.now());

        checkInRepository.save(checkIn);

        // Update booking status
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        Notification notification = new Notification();
        notification.setUser(booking.getCustomer());
        notification.setType(NotificationType.CHECK_IN);
        notification.setTitle("Đã check-in");
        notification.setMessage("Bạn đã được check-in cho đơn " + booking.getBookingCode() + ".");
        notification.setRefId(booking.getId());
        notification.setRefType("BOOKING");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return checkInMapper.toDTO(checkIn);
    }

    public CheckInDTO getCheckInRecord(Long bookingId) {
        CheckIn checkIn = checkInRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("CheckIn record not found"));
        return checkInMapper.toDTO(checkIn);
    }
}

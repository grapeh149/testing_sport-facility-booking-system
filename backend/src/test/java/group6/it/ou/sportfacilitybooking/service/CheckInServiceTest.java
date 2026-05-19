package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.CheckIn;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.CheckInMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.CheckInRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.service.CheckInService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInServiceTest {
    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CheckInMapper checkInMapper;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CheckInService checkInService;

    private static final Long BOOKING_ID = 1L;
    private static final Long CHECKED_BY_USER_ID = 2L;

    @Test
    void checkIn_bookingNotFound_throwsBookingNotFound() {
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("Booking not found", ex.getMessage());
        verifyNoInteractions(userRepository, checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_userNotFound_throwsUserNotFound() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now());
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("User not found", ex.getMessage());
        verifyNoInteractions(checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_notFacilityOwner_throwsNotOwner() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now());
        booking.setOwnerId(99L); // khác CHECKED_BY_USER_ID = 2L
        User user = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("Chỉ chủ sân mới có thể check-in. Bạn không phải là chủ của sân này.", ex.getMessage());
        verifyNoInteractions(checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_pendingPaymentBooking_throwsOnlyConfirmedAllowed() {
        Booking booking = booking(BookingStatus.PENDING_PAYMENT, LocalDate.now());
        User user = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("Can only check in CONFIRMED bookings", ex.getMessage());
        verifyNoInteractions(checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_pendingConfirmBooking_throwsOnlyConfirmedAllowed() {
        Booking booking = booking(BookingStatus.PENDING_CONFIRM, LocalDate.now());
        User user = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("Can only check in CONFIRMED bookings", ex.getMessage());
        verifyNoInteractions(checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_futureBookingDate_throwsCannotCheckInBeforeBookingDate() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now().plusDays(1));
        User user = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertTrue(ex.getMessage().contains("check-in"));
        verifyNoInteractions(checkInRepository, checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_alreadyCheckedIn_throwsBookingAlreadyCheckedIn() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now());
        User user = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(user));
        when(checkInRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(new CheckIn()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertEquals("Booking already checked in", ex.getMessage());
        verify(checkInRepository, never()).save(any());
        verifyNoInteractions(checkInMapper, notificationRepository);
    }

    @Test
    void checkIn_confirmedTodayAndNoExistingCheckIn_savesCheckInUpdatesBookingCreatesNotificationAndReturnsDto() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now());
        User checkedByUser = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();
        CheckIn checkIn = new CheckIn();
        CheckInDTO expectedDto = dto();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(checkedByUser));
        when(checkInRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());
        when(checkInMapper.toEntity(request)).thenReturn(checkIn);
        when(checkInMapper.toDTO(checkIn)).thenReturn(expectedDto);

        CheckInDTO actual = checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request);

        assertSame(expectedDto, actual);
        assertSame(booking, checkIn.getBooking());
        assertSame(checkedByUser, checkIn.getCheckedByUser());
        assertNotNull(checkIn.getCheckedInAt());

        verify(checkInRepository).save(checkIn);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.CHECKED_IN, bookingCaptor.getValue().getStatus());
        assertNotNull(bookingCaptor.getValue().getUpdatedAt());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertSame(booking.getCustomer(), notification.getUser());
        assertEquals(NotificationType.CHECK_IN, notification.getType());
        assertEquals(BOOKING_ID, notification.getRefId());
        assertEquals("BOOKING", notification.getRefType());
        assertFalse(notification.getIsRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void checkIn_confirmedPastDateAndNoExistingCheckIn_doesNotThrow() {
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now().minusDays(1));
        User checkedByUser = user(CHECKED_BY_USER_ID);
        CheckInRequest request = request();
        CheckIn checkIn = new CheckIn();
        CheckInDTO expectedDto = dto();

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userRepository.findById(CHECKED_BY_USER_ID)).thenReturn(Optional.of(checkedByUser));
        when(checkInRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());
        when(checkInMapper.toEntity(request)).thenReturn(checkIn);
        when(checkInMapper.toDTO(checkIn)).thenReturn(expectedDto);

        CheckInDTO actual = assertDoesNotThrow(
                () -> checkInService.checkIn(BOOKING_ID, CHECKED_BY_USER_ID, request));

        assertSame(expectedDto, actual);
        verify(checkInRepository).save(checkIn);
        verify(bookingRepository).save(booking);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getCheckInRecord_recordNotFound_throwsCheckInRecordNotFound() {
        when(checkInRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkInService.getCheckInRecord(BOOKING_ID));

        assertEquals("CheckIn record not found", ex.getMessage());
        verifyNoInteractions(checkInMapper);
    }

    @Test
    void getCheckInRecord_recordExists_returnsDto() {
        CheckIn checkIn = new CheckIn();
        CheckInDTO expectedDto = dto();

        when(checkInRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(checkIn));
        when(checkInMapper.toDTO(checkIn)).thenReturn(expectedDto);

        CheckInDTO actual = checkInService.getCheckInRecord(BOOKING_ID);

        assertSame(expectedDto, actual);
    }

    private CheckInRequest request() {
        CheckInRequest request = new CheckInRequest(BOOKING_ID);
        request.setNote("Arrived on time");
        return request;
    }

    private Booking booking(BookingStatus status, LocalDate bookingDate) {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setBookingCode("BK001");
        booking.setCustomer(user(10L));
        booking.setStatus(status);
        booking.setBookingDate(bookingDate);
        return booking;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private CheckInDTO dto() {
        CheckInDTO dto = new CheckInDTO();
        dto.setId(100L);
        dto.setBookingId(BOOKING_ID);
        dto.setCheckedByUserId(CHECKED_BY_USER_ID);
        dto.setNote("Arrived on time");
        return dto;
    }
}

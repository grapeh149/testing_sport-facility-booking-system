package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.BookingMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.ReviewRepository;
import group6.it.ou.sportfacilitybooking.repository.TimeSlotRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateBookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private static final Long CUSTOMER_ID = 1L;
    private static final Long COURT_ID = 2L;
    private static final Long TIME_SLOT_ID = 3L;
    private static final Long OWNER_ID = 4L;
    private static final Long BOOKING_ID = 5L;

    @Test
    void createBooking_customerNotFound_throwsCustomerNotFound() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertEquals("Customer not found", ex.getMessage());
        verifyNoInteractions(courtRepository, timeSlotRepository, bookingRepository, notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_courtNotFound_throwsCourtNotFound() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(user(CUSTOMER_ID)));
        when(courtRepository.findById(COURT_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertEquals("Court not found", ex.getMessage());
        verifyNoInteractions(timeSlotRepository, bookingRepository, notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_timeSlotNotFound_throwsTimeSlotNotFound() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(user(CUSTOMER_ID)));
        when(courtRepository.findById(COURT_ID)).thenReturn(Optional.of(court(OWNER_ID)));
        when(timeSlotRepository.findById(TIME_SLOT_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertEquals("TimeSlot not found", ex.getMessage());
        verifyNoInteractions(bookingRepository, notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_pastBookingDate_throwsCannotBookPastDates() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().minusDays(1));
        arrangeFoundEntities(timeSlot(LocalTime.now().plusHours(1)));

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertTrue(ex.getMessage().contains("Cannot book past dates"));
        verify(bookingRepository, never()).countConflictingBookings(any(), any(), any());
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_futureBookingDateNoConflict_returnsBookingDTO() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        BookingDTO expectedDto = dto();
        arrangeSuccessfulCreateBooking(request, timeSlot(LocalTime.of(9, 0)), expectedDto);

        // ACT
        BookingDTO actual = bookingService.createBooking(request, CUSTOMER_ID);

        // ASSERT
        assertSame(expectedDto, actual);
        verifyCreatedBookingAndNotification();
    }

    @Test // TC 6 kỳ vong phải là Throw
    void createBooking_todayAndNullStartTime_throwsNullPointerException() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now());
        arrangeFoundEntities(timeSlot(null));

        // ACT
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertNotNull(ex);
        verify(bookingRepository, never()).countConflictingBookings(any(), any(), any());
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_todayAndStartTimeBeforeNow_throwsPastOrOngoing() {
        // ARRANGE
        LocalDate today = LocalDate.now();
        LocalTime fixedNow = LocalTime.of(12, 0);
        BookingCreateRequest request = request(today);
        arrangeFoundEntities(timeSlot(LocalTime.of(11, 0)));

        try (MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {
            localTimeMock.when(LocalTime::now).thenReturn(fixedNow);

            // ACT
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> bookingService.createBooking(request, CUSTOMER_ID));

            // ASSERT
            assertEquals("Cannot book past or ongoing time slot", ex.getMessage());
            verify(bookingRepository, never()).countConflictingBookings(any(), any(), any());
            verifyNoInteractions(notificationRepository, bookingMapper);
        }
    }

    @Test
    void createBooking_todayAndStartTimeEqualsNow_throwsPastOrOngoing() {
        // ARRANGE
        LocalDate today = LocalDate.now();
        LocalTime fixedNow = LocalTime.of(12, 0);
        BookingCreateRequest request = request(today);
        arrangeFoundEntities(timeSlot(fixedNow));

        try (MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {
            localTimeMock.when(LocalTime::now).thenReturn(fixedNow);

            // ACT
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> bookingService.createBooking(request, CUSTOMER_ID));

            // ASSERT
            assertEquals("Cannot book past or ongoing time slot", ex.getMessage());
            verify(bookingRepository, never()).countConflictingBookings(any(), any(), any());
            verifyNoInteractions(notificationRepository, bookingMapper);
        }
    }

    @Test
    void createBooking_todayAndStartTimeAfterNow_returnsBookingDTO() {
        // ARRANGE
        LocalDate today = LocalDate.now();
        LocalTime fixedNow = LocalTime.of(12, 0);
        LocalDateTime fixedDateTimeNow = LocalDateTime.of(today, fixedNow);
        BookingCreateRequest request = request(today);
        BookingDTO expectedDto = dto();
        arrangeSuccessfulCreateBooking(request, timeSlot(LocalTime.of(13, 0)), expectedDto);

        try (MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS);
             MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localTimeMock.when(LocalTime::now).thenReturn(fixedNow);
            localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedDateTimeNow);

            // ACT
            BookingDTO actual = bookingService.createBooking(request, CUSTOMER_ID);

            // ASSERT
            assertSame(expectedDto, actual);
            verifyCreatedBookingAndNotification();
        }
    }

    @Test
    void createBooking_currentDateTimeAfterBookingDateTime_throwsPaymentTimeExceeded() {
        // ARRANGE
        LocalDate bookingDate = LocalDate.now().plusDays(1);
        LocalDateTime fixedDateTimeNow = LocalDateTime.of(bookingDate, LocalTime.of(10, 0));
        BookingCreateRequest request = request(bookingDate);
        arrangeFoundEntities(timeSlot(LocalTime.of(10, 0)));

        try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedDateTimeNow);

            // ACT
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> bookingService.createBooking(request, CUSTOMER_ID));

            // ASSERT
            assertNotNull(ex.getMessage());
            verify(bookingRepository, never()).countConflictingBookings(any(), any(), any());
            verifyNoInteractions(notificationRepository, bookingMapper);
        }
    }

    @Test
    void createBooking_currentDateTimeBeforeBookingDateTime_continuesAndReturnsBookingDTO() {
        // ARRANGE
        LocalDate bookingDate = LocalDate.now().plusDays(1);
        LocalDateTime fixedDateTimeNow = LocalDateTime.of(bookingDate.minusDays(1), LocalTime.of(10, 0));
        BookingCreateRequest request = request(bookingDate);
        BookingDTO expectedDto = dto();

        arrangeSuccessfulCreateBooking(request, timeSlot(LocalTime.of(10, 0)), expectedDto);

        try (MockedStatic<LocalDateTime> localDateTimeMock =
                     Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedDateTimeNow);

            // ACT
            BookingDTO actual = bookingService.createBooking(request, CUSTOMER_ID);

            // ASSERT
            assertSame(expectedDto, actual);
            verify(bookingRepository).countConflictingBookings(COURT_ID, bookingDate, TIME_SLOT_ID);
            verifyCreatedBookingAndNotification();
        }
    }

    @Test
    void createBooking_conflictingBookingExists_throwsAlreadyBooked() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        arrangeFoundEntities(timeSlot(LocalTime.of(9, 0)));
        when(bookingRepository.countConflictingBookings(COURT_ID, request.getBookingDate(), TIME_SLOT_ID)).thenReturn(1L);

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertEquals("This time slot is already booked", ex.getMessage());
        verify(courtRepository, never()).findOwnerIdByCourtId(any());
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_noConflictAndOwnerNotFound_throwsFacilityOwnerNotFound() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        arrangeFoundEntities(timeSlot(LocalTime.of(9, 0)));
        when(bookingRepository.countConflictingBookings(COURT_ID, request.getBookingDate(), TIME_SLOT_ID)).thenReturn(0L);
        when(courtRepository.findOwnerIdByCourtId(COURT_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(request, CUSTOMER_ID));

        // ASSERT
        assertEquals("Facility owner not found for this court", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void createBooking_noConflict_continuesToFindOwnerAndReturnsBookingDTO() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        BookingDTO expectedDto = dto();

        arrangeFoundEntities(timeSlot(LocalTime.of(9, 0)));
        when(bookingRepository.countConflictingBookings(COURT_ID, request.getBookingDate(), TIME_SLOT_ID))
                .thenReturn(0L);
        when(courtRepository.findOwnerIdByCourtId(COURT_ID)).thenReturn(Optional.of(OWNER_ID));
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(expectedDto);

        // ACT
        BookingDTO actual = bookingService.createBooking(request, CUSTOMER_ID);

        // ASSERT
        assertSame(expectedDto, actual);
        verify(bookingRepository).countConflictingBookings(COURT_ID, request.getBookingDate(), TIME_SLOT_ID);
        verify(courtRepository).findOwnerIdByCourtId(COURT_ID);
        verifyCreatedBookingAndNotification();
    }

    @Test
    void createBooking_allValid_savesBookingCreatesNotificationAndReturnsDTO() {
        // ARRANGE
        BookingCreateRequest request = request(LocalDate.now().plusDays(1));
        BookingDTO expectedDto = dto();
        arrangeSuccessfulCreateBooking(request, timeSlot(LocalTime.of(9, 0)), expectedDto);

        // ACT
        BookingDTO actual = bookingService.createBooking(request, CUSTOMER_ID);

        // ASSERT
        assertSame(expectedDto, actual);
        verifyCreatedBookingAndNotification();
    }

    @Test
    void confirmBooking_bookingNotFound_throwsBookingNotFound() {
        // ARRANGE
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.confirmBooking(BOOKING_ID, OWNER_ID));

        // ASSERT
        assertEquals("Booking not found", ex.getMessage());
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void confirmBooking_ownerIdDoesNotMatch_throwsOnlyFacilityOwnerCanConfirmBooking() {
        // ARRANGE
        Booking booking = booking(OWNER_ID);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.confirmBooking(BOOKING_ID, 999L));

        // ASSERT
        assertEquals("Only facility owner can confirm booking", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void confirmBooking_ownerIdMatches_setsConfirmedCreatesNotificationAndReturnsDTO() {
        // ARRANGE
        Booking booking = booking(OWNER_ID);
        BookingDTO expectedDto = dto();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(expectedDto);

        // ACT
        BookingDTO actual = bookingService.confirmBooking(BOOKING_ID, OWNER_ID);

        // ASSERT
        assertSame(expectedDto, actual);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertNotNull(booking.getUpdatedAt());
        verify(bookingRepository).save(booking);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertSame(booking.getCustomer(), notification.getUser());
        assertEquals(NotificationType.BOOKING_CONFIRMED, notification.getType());
        assertEquals(BOOKING_ID, notification.getRefId());
        assertEquals("BOOKING", notification.getRefType());
        assertFalse(notification.getIsRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void cancelBooking_bookingNotFound_throwsBookingNotFound() {
        // ARRANGE
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        // ACT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking(BOOKING_ID, "Busy"));

        // ASSERT
        assertEquals("Booking not found", ex.getMessage());
        verifyNoInteractions(notificationRepository, bookingMapper);
    }

    @Test
    void cancelBooking_reasonProvided_setsCancelledReasonCreatesNotificationAndReturnsDTO() {
        // ARRANGE
        Booking booking = booking(OWNER_ID);
        BookingDTO expectedDto = dto();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(expectedDto);

        // ACT
        BookingDTO actual = bookingService.cancelBooking(BOOKING_ID, "Busy");

        // ASSERT
        assertSame(expectedDto, actual);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals("Busy", booking.getCancelReason());
        assertNotNull(booking.getCancelledAt());
        assertNotNull(booking.getUpdatedAt());
        verify(bookingRepository).save(booking);
        verifyCancelNotificationCreated(booking);
    }

    @Test
    void cancelBooking_nullReason_acceptsNullReasonAndReturnsDTO() {
        // ARRANGE
        Booking booking = booking(OWNER_ID);
        BookingDTO expectedDto = dto();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(expectedDto);

        // ACT
        BookingDTO actual = bookingService.cancelBooking(BOOKING_ID, null);

        // ASSERT
        assertSame(expectedDto, actual);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertNull(booking.getCancelReason());
        assertNotNull(booking.getCancelledAt());
        assertNotNull(booking.getUpdatedAt());
        verify(bookingRepository).save(booking);
        verifyCancelNotificationCreated(booking);
    }

    private void arrangeSuccessfulCreateBooking(BookingCreateRequest request, TimeSlot timeSlot, BookingDTO expectedDto) {
        arrangeFoundEntities(timeSlot);
        when(bookingRepository.countConflictingBookings(COURT_ID, request.getBookingDate(), TIME_SLOT_ID)).thenReturn(0L);
        when(courtRepository.findOwnerIdByCourtId(COURT_ID)).thenReturn(Optional.of(OWNER_ID));
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(expectedDto);
    }

    private void arrangeFoundEntities(TimeSlot timeSlot) {
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(user(CUSTOMER_ID)));
        when(courtRepository.findById(COURT_ID)).thenReturn(Optional.of(court(OWNER_ID)));
        when(timeSlotRepository.findById(TIME_SLOT_ID)).thenReturn(Optional.of(timeSlot));
    }

    private void verifyCreatedBookingAndNotification() {
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertTrue(savedBooking.getBookingCode().startsWith("SB-"));
        assertEquals(CUSTOMER_ID, savedBooking.getCustomer().getId());
        assertEquals(COURT_ID, savedBooking.getCourt().getId());
        assertEquals(OWNER_ID, savedBooking.getOwnerId());
        assertEquals(TIME_SLOT_ID, savedBooking.getTimeSlot().getId());
        assertEquals(BookingStatus.PENDING_PAYMENT, savedBooking.getStatus());
        assertEquals(new BigDecimal("100000"), savedBooking.getTotalPrice());
        assertEquals(new BigDecimal("30000"), savedBooking.getDepositAmount());
        assertEquals(new BigDecimal("5000"), savedBooking.getCommissionAmount());
        assertNotNull(savedBooking.getCreatedAt());
        assertNotNull(savedBooking.getUpdatedAt());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();

        assertEquals(CUSTOMER_ID, notification.getUser().getId());
        assertEquals(NotificationType.BOOKING_CREATED, notification.getType());
        assertEquals("BOOKING", notification.getRefType());
        assertFalse(notification.getIsRead());
        assertNotNull(notification.getCreatedAt());
    }

    private void verifyCancelNotificationCreated(Booking booking) {
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();

        assertSame(booking.getCustomer(), notification.getUser());
        assertEquals(NotificationType.BOOKING_CANCELLED, notification.getType());
        assertEquals(BOOKING_ID, notification.getRefId());
        assertEquals("BOOKING", notification.getRefType());
        assertFalse(notification.getIsRead());
        assertNotNull(notification.getCreatedAt());
    }

    private BookingCreateRequest request(LocalDate bookingDate) {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setCourtId(COURT_ID);
        request.setTimeSlotId(TIME_SLOT_ID);
        request.setBookingDate(bookingDate);
        return request;
    }

    private Booking booking(Long ownerId) {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setBookingCode("BK001");
        booking.setCustomer(user(CUSTOMER_ID));
        booking.setCourt(court(ownerId));
        booking.setStatus(BookingStatus.PENDING_CONFIRM);
        return booking;
    }

    private Court court(Long ownerId) {
        Court court = new Court();
        court.setId(COURT_ID);
        court.setName("Court 1");
        court.setFacility(facility(ownerId));
        return court;
    }

    private Facility facility(Long ownerId) {
        Facility facility = new Facility();
        facility.setOwner(user(ownerId));
        facility.setName("Facility 1");
        facility.setCommissionRate(new BigDecimal("5"));
        return facility;
    }

    private TimeSlot timeSlot(LocalTime startTime) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(TIME_SLOT_ID);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(startTime != null ? startTime.plusHours(1) : null);
        timeSlot.setPrice(new BigDecimal("100000"));
        timeSlot.setDepositRate(new BigDecimal("30"));
        return timeSlot;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setFullName("User " + id);
        return user;
    }

    private BookingDTO dto() {
        BookingDTO dto = new BookingDTO();
        dto.setId(BOOKING_ID);
        dto.setBookingCode("BK001");
        dto.setStatus("PENDING_PAYMENT");
        return dto;
    }
}
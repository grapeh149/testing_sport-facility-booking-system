package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.Review;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private CourtRepository courtRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private static final Long BOOKING_ID = 1L;
    private static final Long CUSTOMER_ID = 2L;
    private static final Long COURT_ID   = 3L;
    private static final Long OWNER_ID   = 4L;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    // ─── getBookingById ───────────────────────────────────────────────────────
    // Coverage: C1 — D1 (findById found/not found), D2 (hasReview T/F)

    // TC1 — D1=T: bookingId không tồn tại → throw
    @Test
    void getBookingById_notFound_throwsBookingNotFound() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById(BOOKING_ID));

        assertEquals("Booking not found", ex.getMessage());
        verifyNoInteractions(reviewRepository, bookingMapper);
    }

    // TC2 — D1=F, D2=T: booking tồn tại, có review → hasReview = true
    @Test
    void getBookingById_foundWithReview_returnsDtoWithHasReviewTrue() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(new Review()));

        BookingDTO result = bookingService.getBookingById(BOOKING_ID);

        assertTrue(result.getHasReview());
    }

    // TC3 — D1=F, D2=F: booking tồn tại, không có review → hasReview = false
    @Test
    void getBookingById_foundWithoutReview_returnsDtoWithHasReviewFalse() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());

        BookingDTO result = bookingService.getBookingById(BOOKING_ID);

        assertFalse(result.getHasReview());
    }

    // ─── getBookingDetails ────────────────────────────────────────────────────
    // Coverage: C1 — D1 (findByBookingCode found/not found)

    // TC4 — D1=T: bookingCode không tồn tại → throw
    @Test
    void getBookingDetails_notFound_throwsBookingNotFound() {
        when(bookingRepository.findByBookingCode("INVALID")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingDetails("INVALID"));

        assertEquals("Booking not found", ex.getMessage());
        verifyNoInteractions(bookingMapper);
    }

    // TC5 — D1=F: bookingCode hợp lệ → trả về DTO
    @Test
    void getBookingDetails_found_returnsDTO() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO expectedDto = new BookingDTO();
        when(bookingRepository.findByBookingCode("SB-001")).thenReturn(Optional.of(booking));
        when(bookingMapper.toDTO(booking)).thenReturn(expectedDto);

        BookingDTO result = bookingService.getBookingDetails("SB-001");

        assertSame(expectedDto, result);
    }

    // ─── getCourtBookings ─────────────────────────────────────────────────────
    // Coverage: C1 + Boundary — filter: !bookingDate.isBefore(today)

    // TC6: không có booking nào → list rỗng
    @Test
    void getCourtBookings_noBookings_returnsEmptyList() {
        when(bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(COURT_ID))
                .thenReturn(List.of());

        List<BookingDTO> result = bookingService.getCourtBookings(COURT_ID);

        assertTrue(result.isEmpty());
        verifyNoInteractions(bookingMapper);
    }

    // TC7 — D1=T: bookingDate = ngày mai (tương lai) → được giữ lại
    @Test
    void getCourtBookings_futureBooking_isIncluded() {
        Booking futureBooking = bookingWithDate(LocalDate.now().plusDays(1));
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(COURT_ID))
                .thenReturn(List.of(futureBooking));
        when(bookingMapper.toDTO(futureBooking)).thenReturn(dto);

        List<BookingDTO> result = bookingService.getCourtBookings(COURT_ID);

        assertEquals(1, result.size());
    }

    // TC8 — Boundary: bookingDate = hôm nay → được giữ lại (không bị lọc)
    @Test
    void getCourtBookings_todayBooking_isIncluded() {
        Booking todayBooking = bookingWithDate(LocalDate.now());
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(COURT_ID))
                .thenReturn(List.of(todayBooking));
        when(bookingMapper.toDTO(todayBooking)).thenReturn(dto);

        List<BookingDTO> result = bookingService.getCourtBookings(COURT_ID);

        assertEquals(1, result.size());
    }

    // TC9 — D1=F: bookingDate = hôm qua (quá khứ) → bị lọc ra
    @Test
    void getCourtBookings_pastBooking_isFiltered() {
        Booking pastBooking = bookingWithDate(LocalDate.now().minusDays(1));
        when(bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(COURT_ID))
                .thenReturn(List.of(pastBooking));

        List<BookingDTO> result = bookingService.getCourtBookings(COURT_ID);

        assertTrue(result.isEmpty());
        verifyNoInteractions(bookingMapper);
    }

    // TC10 — D1=T và D1=F: 1 quá khứ + 1 tương lai → chỉ trả booking tương lai
    @Test
    void getCourtBookings_mixedDates_onlyFutureReturned() {
        Booking pastBooking   = bookingWithDate(LocalDate.now().minusDays(1));
        Booking futureBooking = bookingWithDate(LocalDate.now().plusDays(1));
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByCourtIdOrderByBookingDateAscStartTimeAsc(COURT_ID))
                .thenReturn(List.of(pastBooking, futureBooking));
        when(bookingMapper.toDTO(futureBooking)).thenReturn(dto);

        List<BookingDTO> result = bookingService.getCourtBookings(COURT_ID);

        assertEquals(1, result.size());
        verify(bookingMapper, never()).toDTO(pastBooking);
        verify(bookingMapper).toDTO(futureBooking);
    }

    // ─── getCustomerBookingHistory ─────────────────────────────────────────────
    // Coverage: C1 — D1 (hasReview T/F bên trong Page.map)

    // TC11: không có booking → Page rỗng
    @Test
    void getCustomerBookingHistory_noBookings_returnsEmptyPage() {
        when(bookingRepository.findByCustomerId(CUSTOMER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of()));

        Page<BookingDTO> result = bookingService.getCustomerBookingHistory(CUSTOMER_ID, PAGEABLE);

        assertTrue(result.isEmpty());
        verifyNoInteractions(reviewRepository, bookingMapper);
    }

    // TC12 — D1=T: booking có review → hasReview = true
    @Test
    void getCustomerBookingHistory_bookingHasReview_hasReviewTrue() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByCustomerId(CUSTOMER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(new Review()));

        Page<BookingDTO> result = bookingService.getCustomerBookingHistory(CUSTOMER_ID, PAGEABLE);

        assertTrue(result.getContent().get(0).getHasReview());
    }

    // TC13 — D1=F: booking không có review → hasReview = false
    @Test
    void getCustomerBookingHistory_bookingNoReview_hasReviewFalse() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByCustomerId(CUSTOMER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());

        Page<BookingDTO> result = bookingService.getCustomerBookingHistory(CUSTOMER_ID, PAGEABLE);

        assertFalse(result.getContent().get(0).getHasReview());
    }

    // TC14 — D1=T và D1=F: 2 bookings, 1 có review 1 không → hasReview đúng từng item
    @Test
    void getCustomerBookingHistory_mixedReviews_hasReviewSetCorrectlyPerItem() {
        Booking b1 = booking(1L);
        Booking b2 = booking(2L);
        BookingDTO dto1 = new BookingDTO();
        BookingDTO dto2 = new BookingDTO();
        when(bookingRepository.findByCustomerId(CUSTOMER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(b1, b2)));
        when(bookingMapper.toDTO(b1)).thenReturn(dto1);
        when(bookingMapper.toDTO(b2)).thenReturn(dto2);
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.of(new Review()));
        when(reviewRepository.findByBookingId(2L)).thenReturn(Optional.empty());

        Page<BookingDTO> result = bookingService.getCustomerBookingHistory(CUSTOMER_ID, PAGEABLE);

        assertTrue(result.getContent().get(0).getHasReview());
        assertFalse(result.getContent().get(1).getHasReview());
    }

    // ─── getOwnerPendingBookings ───────────────────────────────────────────────
    // Coverage: C0=C1 (không có nhánh phụ)

    // TC15: owner có booking PENDING_CONFIRM → trả về Page có data
    @Test
    void getOwnerPendingBookings_hasPendingBookings_returnsPage() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByOwner_IdAndStatus(OWNER_ID, BookingStatus.PENDING_CONFIRM, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);

        Page<BookingDTO> result = bookingService.getOwnerPendingBookings(OWNER_ID, PAGEABLE);

        assertEquals(1, result.getTotalElements());
        verify(bookingMapper).toDTO(booking);
    }

    // TC16: owner không có booking pending → Page rỗng
    @Test
    void getOwnerPendingBookings_noPendingBookings_returnsEmptyPage() {
        when(bookingRepository.findByOwner_IdAndStatus(OWNER_ID, BookingStatus.PENDING_CONFIRM, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of()));

        Page<BookingDTO> result = bookingService.getOwnerPendingBookings(OWNER_ID, PAGEABLE);

        assertTrue(result.isEmpty());
        verifyNoInteractions(bookingMapper);
    }

    // ─── getOwnerAllBookings ──────────────────────────────────────────────────
    // Coverage: C1 — D1 (hasReview T/F bên trong Page.map)

    // TC17: owner không có booking → Page rỗng
    @Test
    void getOwnerAllBookings_noBookings_returnsEmptyPage() {
        when(bookingRepository.findByOwnerId(OWNER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of()));

        Page<BookingDTO> result = bookingService.getOwnerAllBookings(OWNER_ID, PAGEABLE);

        assertTrue(result.isEmpty());
        verifyNoInteractions(reviewRepository, bookingMapper);
    }

    // TC18 — D1=T: booking có review → hasReview = true
    @Test
    void getOwnerAllBookings_bookingHasReview_hasReviewTrue() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByOwnerId(OWNER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(new Review()));

        Page<BookingDTO> result = bookingService.getOwnerAllBookings(OWNER_ID, PAGEABLE);

        assertTrue(result.getContent().get(0).getHasReview());
    }

    // TC19 — D1=F: booking không có review → hasReview = false
    @Test
    void getOwnerAllBookings_bookingNoReview_hasReviewFalse() {
        Booking booking = booking(BOOKING_ID);
        BookingDTO dto = new BookingDTO();
        when(bookingRepository.findByOwnerId(OWNER_ID, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDTO(booking)).thenReturn(dto);
        when(reviewRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.empty());

        Page<BookingDTO> result = bookingService.getOwnerAllBookings(OWNER_ID, PAGEABLE);

        assertFalse(result.getContent().get(0).getHasReview());
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Booking booking(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setBookingCode("BK-" + id);
        booking.setCustomer(user(CUSTOMER_ID));
        booking.setCourt(court());
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setStatus(BookingStatus.PENDING_CONFIRM);
        return booking;
    }

    private Booking bookingWithDate(LocalDate date) {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setBookingCode("BK-001");
        booking.setCustomer(user(CUSTOMER_ID));
        booking.setCourt(court());
        booking.setBookingDate(date);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setFullName("User " + id);
        return user;
    }

    private Court court() {
        Court court = new Court();
        court.setId(COURT_ID);
        court.setName("Court 1");
        court.setFacility(facility());
        return court;
    }

    private Facility facility() {
        Facility facility = new Facility();
        facility.setOwner(user(OWNER_ID));
        facility.setName("Facility 1");
        facility.setCommissionRate(new BigDecimal("5"));
        return facility;
    }
}

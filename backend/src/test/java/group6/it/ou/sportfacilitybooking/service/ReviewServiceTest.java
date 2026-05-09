package group6.it.ou.sportfacilitybooking.service;

<<<<<<< HEAD
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
=======
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.Review;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.ReviewMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import group6.it.ou.sportfacilitybooking.repository.ReviewRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.service.ReviewService;
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

<<<<<<< HEAD
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.*;
import group6.it.ou.sportfacilitybooking.mapper.ReviewMapper;
import group6.it.ou.sportfacilitybooking.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
=======
import java.util.List;
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
<<<<<<< HEAD
@DisplayName("ReviewService Unit Tests")
=======
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

<<<<<<< HEAD
    private User customer;
    private Facility facility;
    private Court court;
    private Booking confirmedBooking;
    private ReviewCreateRequest reviewRequest;

    @BeforeEach
    void setUp() {
        // Setup customer
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@test.com");
        customer.setFullName("Test Customer");
        customer.setRole(UserRole.CUSTOMER);

        // Setup facility
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");
        facility.setAvgRating(BigDecimal.ZERO);

        // Setup court
        court = new Court();
        court.setId(1L);
        court.setFacility(facility);

        // Setup booking đã CHECKED_IN (hợp lệ để review)
        confirmedBooking = new Booking();
        confirmedBooking.setId(1L);
        confirmedBooking.setBookingCode("BOOK001");
        confirmedBooking.setCustomer(customer);
        confirmedBooking.setCourt(court);
        confirmedBooking.setStatus(BookingStatus.CHECKED_IN);
        confirmedBooking.setBookingDate(LocalDate.now().minusDays(1));

        // Review request hợp lệ
        reviewRequest = new ReviewCreateRequest();
        reviewRequest.setBookingId(1L);
        reviewRequest.setRating((byte) 5);
        reviewRequest.setComment("Sân rất tốt!");
    }

    // ========== R-01: testCreateReviewSuccess ==========
    @Test
    @DisplayName("R-01: Tạo đánh giá thành công sau khi đã check-in")
    void testCreateReviewSuccess() {
        // Arrange
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setRating((byte) 5);
        savedReview.setComment("Sân rất tốt!");
        savedReview.setBooking(confirmedBooking);
        savedReview.setCustomer(customer);
        savedReview.setIsVisible(true);

        ReviewDTO expectedDTO = new ReviewDTO();
        expectedDTO.setId(1L);
        expectedDTO.setRating((byte) 5);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(confirmedBooking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(reviewMapper.toEntity(any(ReviewCreateRequest.class))).thenReturn(savedReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(reviewRepository.findByFacilityIdAndIsVisible(1L, true))
            .thenReturn(java.util.List.of(savedReview));
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);
        when(reviewMapper.toDTO(any(Review.class))).thenReturn(expectedDTO);

        // Act
        ReviewDTO result = reviewService.createReview(1L, 1L, reviewRequest);

        // Assert
        assertNotNull(result);
        assertEquals((byte) 5, result.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(facilityRepository, times(1)).save(any(Facility.class));
    }

    // ========== R-02: testCreateReview_BookingNotCompleted ==========
    @Test
    @DisplayName("R-02: Không thể đánh giá khi booking chưa ở trạng thái CHECKED_IN")
    void testCreateReview_BookingNotCompleted() {
        // Arrange: booking chỉ CONFIRMED, chưa CHECK_IN
        confirmedBooking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(confirmedBooking));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> reviewService.createReview(1L, 1L, reviewRequest));
        assertTrue(exception.getMessage().contains("Chỉ có thể đánh giá khi đã check-in"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // ========== R-03: testCreateReview_Duplicate ==========
    @Test
    @DisplayName("R-03: Không thể đánh giá lần 2 cho cùng một booking")
    void testCreateReview_Duplicate() {
        // Arrange: booking đã có review rồi
        Review existingReview = new Review();
        existingReview.setId(99L);
        existingReview.setBooking(confirmedBooking);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(confirmedBooking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.of(existingReview));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> reviewService.createReview(1L, 1L, reviewRequest));
        assertTrue(exception.getMessage().contains("Bạn đã đánh giá cho đặt sân này rồi"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // ========== R-04: testCreateReview_WrongCustomer ==========
    @Test
    @DisplayName("R-04: Không thể đánh giá khi customerId không phải chủ booking")
    void testCreateReview_WrongCustomer() {
        // Arrange: customer ID khác với người đặt sân
        Long wrongCustomerId = 999L;
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(confirmedBooking));
        // confirmedBooking.getCustomer().getId() = 1L, nhưng ta dùng 999L

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> reviewService.createReview(1L, wrongCustomerId, reviewRequest));
        assertTrue(exception.getMessage().contains("Chỉ khách hàng mới có thể đánh giá"));
        verify(reviewRepository, never()).save(any(Review.class));
=======
    @Test
    void getAllReviews_shouldOnlyReturnVisibleReviews() {
        Review visible = new Review();
        visible.setIsVisible(true);
        Review hidden = new Review();
        hidden.setIsVisible(false);

        when(reviewRepository.findAll()).thenReturn(List.of(visible, hidden));
        when(reviewMapper.toDTO(visible)).thenReturn(new ReviewDTO());

        List<ReviewDTO> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
    }

    @Test
    void createReview_shouldSaveReviewWhenBookingCheckedIn() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CHECKED_IN);
        User customer = new User();
        customer.setId(2L);
        booking.setCustomer(customer);
        Court court = new Court();
        Facility facility = new Facility();
        facility.setId(10L);
        court.setFacility(facility);
        booking.setCourt(court);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));

        Review reviewEntity = new Review();
        reviewEntity.setRating((byte) 5);
        reviewEntity.setComment("Great facility");
        when(reviewMapper.toEntity(any(ReviewCreateRequest.class))).thenReturn(reviewEntity);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(facilityRepository.findById(10L)).thenReturn(Optional.of(facility));
        when(reviewRepository.findByFacilityIdAndIsVisible(10L, true)).thenReturn(List.of(reviewEntity));
        when(reviewMapper.toDTO(reviewEntity)).thenReturn(new ReviewDTO());

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookingId(1L);
        request.setRating((byte) 5);
        request.setComment("Excellent");

        ReviewDTO result = reviewService.createReview(1L, 2L, request);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
        verify(facilityRepository).save(facility);
    }

    @Test
    void replyToReview_shouldUpdateReviewOwnerReply() {
        Review review = new Review();
        review.setId(5L);

        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(new ReviewDTO());

        ReviewDTO result = reviewService.replyToReview(5L, "Thank you");

        assertNotNull(result);
        assertEquals("Thank you", review.getOwnerReply());
        verify(reviewRepository).save(review);
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
    }
}

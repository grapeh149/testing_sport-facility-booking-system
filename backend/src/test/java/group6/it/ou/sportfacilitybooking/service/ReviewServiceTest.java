package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
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
        reviewEntity.setRating((int) 5);
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
    }
}

package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Review;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.mapper.ReviewMapper;
import group6.it.ou.sportfacilitybooking.repository.ReviewRepository;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
            .filter(r -> r.getIsVisible())
            .map(reviewMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public ReviewDTO createReview(Long bookingId, Long customerId, ReviewCreateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Chỉ có thể đánh giá khi đã check-in");
        }
        
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Chỉ khách hàng mới có thể đánh giá");
        }
        
        // Check if already reviewed
        if (reviewRepository.findByBookingId(bookingId).isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá cho đặt sân này rồi");
        }
        
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Review review = reviewMapper.toEntity(request);
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setFacility(booking.getCourt().getFacility());
        review.setIsVisible(true);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        reviewRepository.save(review);
        
        // Update facility rating
        recalculateFacilityRating(booking.getCourt().getFacility().getId());
        
        return reviewMapper.toDTO(review);
    }
    
    public Page<ReviewDTO> getReviewsByFacility(Long facilityId, Pageable pageable) {
        return reviewRepository.findByFacilityIdAndIsVisible(facilityId, true, pageable)
            .map(reviewMapper::toDTO);
    }
    
    public ReviewDTO replyToReview(Long reviewId, String reply) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setOwnerReply(reply);
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        
        return reviewMapper.toDTO(review);
    }
    
    private void recalculateFacilityRating(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        List<Review> reviews = reviewRepository.findByFacilityIdAndIsVisible(facilityId, true);
        
        if (reviews.isEmpty()) {
            facility.setAvgRating(BigDecimal.ZERO);
        } else {
            double average = reviews.stream()
                .mapToLong(r -> r.getRating().longValue())
                .average()
                .orElse(0.0);
            facility.setAvgRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        }
        
        facilityRepository.save(facility);
    }
}

package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByBookingId(Long bookingId);
    Page<Review> findByFacilityIdAndIsVisible(Long facilityId, Boolean isVisible, Pageable pageable);
    List<Review> findByFacilityIdAndIsVisible(Long facilityId, Boolean isVisible);
}

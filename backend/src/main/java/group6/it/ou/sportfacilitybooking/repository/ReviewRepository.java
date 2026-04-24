package group6.it.ou.sportfacilitybooking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.it.ou.sportfacilitybooking.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser_Id(Long userId);
    List<Review> findByFacility_Id(Long facilityId);
    List<Review> findByBooking_Id(Long bookingId);
}

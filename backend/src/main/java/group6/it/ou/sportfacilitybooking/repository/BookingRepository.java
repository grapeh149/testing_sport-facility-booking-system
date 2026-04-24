package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    List<Booking> findByCourt_Facility_IdAndBookingDateBetween(Long facilityId, LocalDate startDate, LocalDate endDate);
    boolean existsByCourt_Facility_Id(Long facilityId);

    @Query("SELECT COALESCE(COUNT(b), 0) FROM Booking b WHERE " +
            "b.court.id = :courtId AND b.bookingDate = :bookingDate AND " +
            "b.timeSlot.id = :timeSlotId AND " +
            "b.status IN ('CONFIRMED', 'PENDING_CONFIRM', 'CHECKED_IN')")
    long countConflictingBookings(@Param("courtId") Long courtId,
                                  @Param("bookingDate") LocalDate bookingDate,
                                  @Param("timeSlotId") Long timeSlotId);

    // Get pending bookings for owner approval
    @Query("SELECT b FROM Booking b WHERE b.ownerId = :ownerId AND b.status = :status ORDER BY b.createdAt DESC")
    Page<Booking> findByOwner_IdAndStatus(@Param("ownerId") Long ownerId,
                                          @Param("status") BookingStatus status,
                                          Pageable pageable);

    // Get all bookings for specific owner
    @Query("SELECT b FROM Booking b WHERE b.ownerId = :ownerId ORDER BY b.createdAt DESC")
    Page<Booking> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    // Get booking for owner with any status
    @Query("SELECT b FROM Booking b WHERE b.ownerId = :ownerId AND b.bookingCode = :bookingCode")
    Optional<Booking> findByOwnerIdAndBookingCode(@Param("ownerId") Long ownerId, @Param("bookingCode") String bookingCode);

    // Get all pending approvals for owner
    @Query("SELECT b FROM Booking b WHERE b.ownerId = :ownerId AND b.status = 'PENDING_CONFIRM' ORDER BY b.createdAt DESC")
    List<Booking> findPendingApprovalsForOwner(@Param("ownerId") Long ownerId);

    // Find booking with eager load of timeSlot - use for payment validation
    @Query("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.timeSlot WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithTimeSlot(@Param("bookingId") Long bookingId);
}

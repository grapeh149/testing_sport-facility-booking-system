package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByFacilityId(Long facilityId);
    List<Court> findByFacilityIdAndIsActive(Long facilityId, Boolean isActive);
    List<Court> findByIsActive(Boolean isActive);
    
    // Get owner_id directly from database by court_id - avoid N+1 query and lazy load issues
    @Query(nativeQuery = true, value = "SELECT f.owner_id FROM courts c INNER JOIN facilities f ON c.facility_id = f.id WHERE c.id = :courtId")
    Optional<Long> findOwnerIdByCourtId(@Param("courtId") Long courtId);
    
    // Search courts with filters: address, sport type, price range
    @Query("SELECT c FROM Court c " +
           "JOIN c.facility f " +
           "JOIN c.sportType st " +
           "LEFT JOIN TimeSlot ts ON ts.court = c " +
           "WHERE (f.address LIKE %:address% OR :address IS NULL OR :address = '') " +
           "AND (:sportTypeId IS NULL OR st.id = :sportTypeId) " +
           "AND c.isActive = true " +
           "AND f.status = 'APPROVED'")
    List<Court> searchCourts(
        @Param("address") String address,
        @Param("sportTypeId") Long sportTypeId
    );
}

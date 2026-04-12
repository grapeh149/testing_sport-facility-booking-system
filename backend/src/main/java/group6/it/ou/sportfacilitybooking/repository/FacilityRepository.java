package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.FacilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByStatus(FacilityStatus status);
    List<Facility> findByOwnerId(Long ownerId);
    List<Facility> findByOwnerIdAndStatusNot(Long ownerId, FacilityStatus status);
    List<Facility> findByDistrictContainingAndStatus(String district, FacilityStatus status);
    List<Facility> findByCityContainingAndStatus(String city, FacilityStatus status);
    Page<Facility> findByStatus(FacilityStatus status, Pageable pageable);
    Page<Facility> findByStatusNot(FacilityStatus status, Pageable pageable);
    Page<Facility> findByStatusAndNameContainingIgnoreCase(FacilityStatus status, String name, Pageable pageable);
    
    @Query("SELECT f FROM Facility f WHERE f.status = 'APPROVED' AND (f.district LIKE %:keyword% OR f.name LIKE %:keyword%)")
    Page<Facility> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

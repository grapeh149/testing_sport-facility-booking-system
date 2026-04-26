package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilityImageRepository extends JpaRepository<FacilityImage, Long> {
    List<FacilityImage> findByFacilityId(Long facilityId);
    List<FacilityImage> findByFacilityIdOrderBySortOrder(Long facilityId);
    long deleteByFacilityId(Long facilityId);
}

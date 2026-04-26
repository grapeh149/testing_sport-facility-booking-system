package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.FacilityApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityApprovalLogRepository extends JpaRepository<FacilityApprovalLog, Long> {
    List<FacilityApprovalLog> findByFacilityId(Long facilityId);
    Optional<FacilityApprovalLog> findTopByFacilityIdAndActionOrderByCreatedAtDesc(Long facilityId, String action);
    long deleteByFacilityId(Long facilityId);
}

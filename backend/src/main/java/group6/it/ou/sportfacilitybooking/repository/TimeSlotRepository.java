package group6.it.ou.sportfacilitybooking.repository;


import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByCourtId(Long courtId);
    List<TimeSlot> findByCourtIdAndIsActive(Long courtId, Boolean isActive);

    @Query("SELECT t FROM TimeSlot t WHERE t.court.id = :courtId AND " +
            "((t.startTime < :endTime AND t.endTime > :startTime) OR " +
            "(t.dayOfWeek IS NULL OR t.dayOfWeek = :dayOfWeek))")
    List<TimeSlot> findOverlappingSlots(@Param("courtId") Long courtId,
                                        @Param("startTime") LocalTime startTime,
                                        @Param("endTime") LocalTime endTime,
                                        @Param("dayOfWeek") Byte dayOfWeek);
}
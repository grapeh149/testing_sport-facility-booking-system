package group6.it.ou.sportfacilitybooking.repository;


import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
}

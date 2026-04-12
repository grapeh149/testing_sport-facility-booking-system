package group6.it.ou.sportfacilitybooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.it.ou.sportfacilitybooking.entity.Facility;

@Repository
public interface FalicilyRepository extends JpaRepository<Facility, Integer> {
}

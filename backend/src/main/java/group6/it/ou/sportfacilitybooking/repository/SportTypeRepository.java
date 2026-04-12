package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Integer> {
    List<SportType> findByIsActive(Boolean isActive);
}

package group6.it.ou.sportfacilitybooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.it.ou.sportfacilitybooking.entity.SanTheThao;

@Repository
public interface SanTheThaoRepository extends JpaRepository<SanTheThao, Integer> {
	List<SanTheThao> findByMaChiNhanh(Integer maChiNhanh);
}

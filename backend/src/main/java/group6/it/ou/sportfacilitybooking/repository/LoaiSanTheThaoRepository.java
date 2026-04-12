package group6.it.ou.sportfacilitybooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.it.ou.sportfacilitybooking.entity.LoaiSanTheThao;

@Repository
public interface LoaiSanTheThaoRepository extends JpaRepository<LoaiSanTheThao, String> {
}

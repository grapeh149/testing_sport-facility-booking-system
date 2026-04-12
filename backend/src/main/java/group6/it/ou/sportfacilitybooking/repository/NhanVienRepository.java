package group6.it.ou.sportfacilitybooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.it.ou.sportfacilitybooking.entity.PaymentStatus;

@Repository
public interface NhanVienRepository extends JpaRepository<PaymentStatus, Integer> {
}
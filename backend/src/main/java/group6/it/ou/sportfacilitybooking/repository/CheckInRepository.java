package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<Booking, Integer> {
    // TC42: lấy bản ghi cọc theo maDatSan (1-1)
    Optional<Booking> findByThongTinDatSanMaDatSan(Integer maDatSan);
}

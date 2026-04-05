package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.ThongTinDatSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ThongTinDatSanRepository extends JpaRepository<ThongTinDatSan, Integer> {

    // TC11/TC12: kiểm tra trùng slot (maSan + maGio + bookingDate) với status PENDING/CONFIRMED
    boolean existsBySanTheThaoMaSanAndTimeSlotMaGioAndBookingDateAndStatusIn(
            Integer maSan, Integer maGio, LocalDate bookingDate,
            List<ThongTinDatSan.BookingStatus> statuses);

    // TC18: kiểm tra cùng khách hàng đã có booking cùng maGio + bookingDate (dù sân khác)
    boolean existsByKhachHangMaKHAndTimeSlotMaGioAndBookingDateAndStatusIn(
            Integer maKH, Integer maGio, LocalDate bookingDate,
            List<ThongTinDatSan.BookingStatus> statuses);

    // READ: lọc theo khách hàng
    List<ThongTinDatSan> findByKhachHangMaKH(Integer maKH);

    // READ: lọc theo sân
    List<ThongTinDatSan> findBySanTheThaoMaSan(Integer maSan);

    List<ThongTinDatSan> findBySanTheThaoMaChiNhanh(Integer maChiNhanh);

    // Dùng cho calendar
    List<ThongTinDatSan> findBySanTheThaoMaSanAndBookingDateBetween(
            Integer maSan,
            LocalDate from,
            LocalDate to
    );


}
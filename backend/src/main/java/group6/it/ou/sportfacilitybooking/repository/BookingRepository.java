package group6.it.ou.sportfacilitybooking.repository;

import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingStatus, Integer> {

    // TC11/TC12: kiểm tra trùng slot (maSan + maGio + bookingDate) với status PENDING/CONFIRMED
    boolean existsBySanTheThaoMaSanAndTimeSlotMaGioAndBookingDateAndStatusIn(
            Integer maSan, Integer maGio, LocalDate bookingDate,
            List<BookingStatus.BookingStatus> statuses);

    // TC18: kiểm tra cùng khách hàng đã có booking cùng maGio + bookingDate (dù sân khác)
    boolean existsByKhachHangMaKHAndTimeSlotMaGioAndBookingDateAndStatusIn(
            Integer maKH, Integer maGio, LocalDate bookingDate,
            List<BookingStatus.BookingStatus> statuses);

    // READ: lọc theo khách hàng
    List<BookingStatus> findByKhachHangMaKH(Integer maKH);

    // READ: lọc theo sân
    List<BookingStatus> findBySanTheThaoMaSan(Integer maSan);

    List<BookingStatus> findBySanTheThaoMaChiNhanh(Integer maChiNhanh);

    // Dùng cho calendar
    List<BookingStatus> findBySanTheThaoMaSanAndBookingDateBetween(
            Integer maSan,
            LocalDate from,
            LocalDate to
    );


}
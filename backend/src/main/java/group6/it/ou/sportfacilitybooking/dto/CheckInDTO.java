package group6.it.ou.sportfacilitybooking.dto;




import group6.it.ou.sportfacilitybooking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dùng cho danh sách — hiển thị gọn:
 *   GET /api/bookings/my               (API-21) — Customer xem lịch của mình
 *   GET /api/facilities/{id}/bookings  (API-27) — Owner xem booking của cơ sở
 *
 * Không chứa thông tin cọc chi tiết, không chứa thông tin khách hàng đầy đủ.
 */
public class CheckInDTO {

    private Integer maDatSan;
    private String tenSan;          // tên sân để hiển thị list
    private String tenKhachHang;    // họ + tên (Owner cần biết ai đặt)
    private LocalDate bookingDate;
    private LocalTime gioBatDau;    // lấy từ TimeSlot
    private LocalTime gioKetThuc;   // lấy từ TimeSlot
    private BigDecimal totalPrice;
    private BookingStatus.BookingStatus status;

    // ---- NoArgs ----
    public CheckInDTO() {}

    // ---- AllArgs ----
    public CheckInDTO(Integer maDatSan, String tenSan, String tenKhachHang,
                                  LocalDate bookingDate, LocalTime gioBatDau,
                                  LocalTime gioKetThuc, BigDecimal totalPrice,
                                  BookingStatus.BookingStatus status) {
        this.maDatSan = maDatSan;
        this.tenSan = tenSan;
        this.tenKhachHang = tenKhachHang;
        this.bookingDate = bookingDate;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // ---- Getters ----
    public Integer getMaDatSan() { return maDatSan; }
    public String getTenSan() { return tenSan; }
    public String getTenKhachHang() { return tenKhachHang; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BookingStatus.BookingStatus getStatus() { return status; }

    // ---- Setters ----
    public void setMaDatSan(Integer maDatSan) { this.maDatSan = maDatSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(BookingStatus.BookingStatus status) { this.status = status; }
}

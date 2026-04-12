package group6.it.ou.sportfacilitybooking.dto;

import group6.it.ou.sportfacilitybooking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dùng cho xem chi tiết:
 *   GET /api/bookings/{id}  (API-22)
 *
 * Trả về ĐẦY ĐỦ: thông tin sân + khách hàng + khung giờ + cọc.
 */
public class CheckInRequest {
    // --- Thông tin booking ---
    private Integer maDatSan;
    private LocalDate bookingDate;
    private BigDecimal totalPrice;
    private BookingStatus.BookingStatus status;

    // --- Thông tin sân ---
    private Integer maSan;
    private String tenSan;

    // --- Thông tin khung giờ ---
    private Integer maGio;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    // --- Thông tin khách hàng ---
    private Integer maKH;
    private String tenKhachHang;
    private String sdtKhachHang;

    // --- Thông tin đặt cọc (nhúng DepositResponse) ---
    private BookingDTO deposit;

    // ---- NoArgs ----
    public CheckInRequest() {}

    // ---- AllArgs ----
    public CheckInRequest(Integer maDatSan, LocalDate bookingDate, BigDecimal totalPrice,
                                 BookingStatus.BookingStatus status, Integer maSan, String tenSan,
                                 Integer maGio, LocalTime gioBatDau, LocalTime gioKetThuc,
                                 Integer maKH, String tenKhachHang, String sdtKhachHang,
                                 BookingDTO deposit) {
        this.maDatSan = maDatSan;
        this.bookingDate = bookingDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.maSan = maSan;
        this.tenSan = tenSan;
        this.maGio = maGio;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.maKH = maKH;
        this.tenKhachHang = tenKhachHang;
        this.sdtKhachHang = sdtKhachHang;
        this.deposit = deposit;
    }

    // ---- Getters ----
    public Integer getMaDatSan() { return maDatSan; }
    public LocalDate getBookingDate() { return bookingDate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BookingStatus.BookingStatus getStatus() { return status; }
    public Integer getMaSan() { return maSan; }
    public String getTenSan() { return tenSan; }
    public Integer getMaGio() { return maGio; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public Integer getMaKH() { return maKH; }
    public String getTenKhachHang() { return tenKhachHang; }
    public String getSdtKhachHang() { return sdtKhachHang; }
    public BookingDTO getDeposit() { return deposit; }

    // ---- Setters ----
    public void setMaDatSan(Integer maDatSan) { this.maDatSan = maDatSan; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(BookingStatus.BookingStatus status) { this.status = status; }
    public void setMaSan(Integer maSan) { this.maSan = maSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }
    public void setMaGio(Integer maGio) { this.maGio = maGio; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }
    public void setDeposit(BookingDTO deposit) { this.deposit = deposit; }
}

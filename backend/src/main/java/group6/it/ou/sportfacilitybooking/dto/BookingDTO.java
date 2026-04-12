package group6.it.ou.sportfacilitybooking.dto;

import group6.it.ou.sportfacilitybooking.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * KHÔNG trả về standalone.
 * Được NHÚNG trong BookingDetailResponse (API-22).
 *
 * Ví dụ JSON trả về (nằm trong bookingDetail):
 * {
 *   "deposit": {
 *     "maDatCoc": 5,
 *     "tienCoc": 100000,
 *     "phuongThucThanhToan": "MOMO",
 *     "tinhTrangThanhToan": "PAID",
 *     "ngayThanhToan": "2026-03-25T10:00:00"
 *   }
 * }
 */
public class BookingDTO {
    private Integer maDatCoc;
    private BigDecimal tienCoc;
    private String phuongThucThanhToan;
    private Booking.PaymentStatus tinhTrangThanhToan;
    private LocalDateTime ngayThanhToan;

    // ---- NoArgs ----
    public BookingDTO() {}

    // ---- AllArgs ----
    public BookingDTO(Integer maDatCoc, BigDecimal tienCoc, String phuongThucThanhToan,
                     Booking.PaymentStatus tinhTrangThanhToan, LocalDateTime ngayThanhToan) {
        this.maDatCoc = maDatCoc;
        this.tienCoc = tienCoc;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.tinhTrangThanhToan = tinhTrangThanhToan;
        this.ngayThanhToan = ngayThanhToan;
    }

    // ---- Getters ----
    public Integer getMaDatCoc() { return maDatCoc; }
    public BigDecimal getTienCoc() { return tienCoc; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public Booking.PaymentStatus getTinhTrangThanhToan() { return tinhTrangThanhToan; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }

    // ---- Setters ----
    public void setMaDatCoc(Integer maDatCoc) { this.maDatCoc = maDatCoc; }
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
    public void setTinhTrangThanhToan(Booking.PaymentStatus tinhTrangThanhToan) {
        this.tinhTrangThanhToan = tinhTrangThanhToan;
    }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }
}

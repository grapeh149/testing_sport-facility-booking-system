package group6.it.ou.sportfacilitybooking.dto.DatSan;

import group6.it.ou.sportfacilitybooking.entity.ThongTinDatCoc;

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
public class DatCocDTO {
    private Integer maDatCoc;
    private BigDecimal tienCoc;
    private String phuongThucThanhToan;
    private ThongTinDatCoc.PaymentStatus tinhTrangThanhToan;
    private LocalDateTime ngayThanhToan;

    // ---- NoArgs ----
    public DatCocDTO() {}

    // ---- AllArgs ----
    public DatCocDTO(Integer maDatCoc, BigDecimal tienCoc, String phuongThucThanhToan,
                     ThongTinDatCoc.PaymentStatus tinhTrangThanhToan, LocalDateTime ngayThanhToan) {
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
    public ThongTinDatCoc.PaymentStatus getTinhTrangThanhToan() { return tinhTrangThanhToan; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }

    // ---- Setters ----
    public void setMaDatCoc(Integer maDatCoc) { this.maDatCoc = maDatCoc; }
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
    public void setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus tinhTrangThanhToan) {
        this.tinhTrangThanhToan = tinhTrangThanhToan;
    }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }
}

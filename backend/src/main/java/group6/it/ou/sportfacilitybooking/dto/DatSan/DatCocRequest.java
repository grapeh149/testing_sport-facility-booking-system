package group6.it.ou.sportfacilitybooking.dto.DatSan;


import java.math.BigDecimal;
/**
 * KHÔNG dùng standalone.
 * Được NHÚNG vào BookingRequest vì khách đặt sân + nộp cọc trong 1 lần (API-20).
 *
 * Ví dụ JSON gửi lên:
 * {
 *   "maSan": 1,
 *   "maGio": 2,
 *   "bookingDate": "2026-03-25",
 *   "deposit": {
 *     "tienCoc": 100000,
 *     "phuongThucThanhToan": "MOMO"
 *   }
 * }
 */

public class DatCocRequest {
    // * Bắt buộc
    private BigDecimal tienCoc;

    // * Bắt buộc — ví dụ: "MOMO", "BANKING", "CASH"
    private String phuongThucThanhToan;

    // ---- NoArgs ----
    public DatCocRequest() {}

    // ---- AllArgs ----
    public DatCocRequest(BigDecimal tienCoc, String phuongThucThanhToan) {
        this.tienCoc = tienCoc;
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // ---- Getters ----
    public BigDecimal getTienCoc() { return tienCoc; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }

    // ---- Setters ----
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}

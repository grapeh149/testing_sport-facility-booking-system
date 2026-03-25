package group6.it.ou.sportfacilitybooking.dto.DatSan;

import java.time.LocalDate;

/**
 * Dùng cho:
 *   POST /api/bookings  (API-20)
 *
 * Khách hàng gửi lên: chọn sân + chọn giờ + chọn ngày + thông tin cọc
 *
 * JSON mẫu:
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
public class DatSanRequest {

    // * Bắt buộc
    private Integer maSan;

    // * Bắt buộc
    private Integer maGio;

    // * Bắt buộc
    private LocalDate bookingDate;

    // * Bắt buộc — thông tin đặt cọc nhúng vào đây
    private DatCocRequest deposit;

    // ---- NoArgs ----
    public DatSanRequest() {}

    // ---- AllArgs ----
    public DatSanRequest(Integer maSan, Integer maGio,
                          LocalDate bookingDate, DatCocRequest deposit) {
        this.maSan = maSan;
        this.maGio = maGio;
        this.bookingDate = bookingDate;
        this.deposit = deposit;
    }

    // ---- Getters ----
    public Integer getMaSan() { return maSan; }
    public Integer getMaGio() { return maGio; }
    public LocalDate getBookingDate() { return bookingDate; }
    public DatCocRequest getDeposit() { return deposit; }

    // ---- Setters ----
    public void setMaSan(Integer maSan) { this.maSan = maSan; }
    public void setMaGio(Integer maGio) { this.maGio = maGio; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public void setDeposit(DatCocRequest deposit) { this.deposit = deposit; }
}

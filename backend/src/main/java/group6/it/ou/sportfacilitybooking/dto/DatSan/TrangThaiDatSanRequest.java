package group6.it.ou.sportfacilitybooking.dto.DatSan;


import group6.it.ou.sportfacilitybooking.entity.ThongTinDatSan;

/**
 * Dùng cho các PATCH thay đổi trạng thái:
 *   PATCH /api/bookings/{id}/cancel   (API-23)
 *   PATCH /api/bookings/{id}/confirm  (API-24)
 *   PATCH /api/bookings/{id}/reject   (API-25)
 *   POST  /api/bookings/{id}/checkin  (API-26)
 *
 * Trả về gọn: chỉ xác nhận maDatSan + status mới + message.
 */
public class TrangThaiDatSanRequest {
    private Integer maDatSan;
    private ThongTinDatSan.BookingStatus status;
    private String message;

    // ---- NoArgs ----
    public TrangThaiDatSanRequest() {}

    // ---- AllArgs ----
    public TrangThaiDatSanRequest(Integer maDatSan, ThongTinDatSan.BookingStatus status, String message) {
        this.maDatSan = maDatSan;
        this.status = status;
        this.message = message;
    }

    // ---- Getters ----
    public Integer getMaDatSan() { return maDatSan; }
    public ThongTinDatSan.BookingStatus getStatus() { return status; }
    public String getMessage() { return message; }

    // ---- Setters ----
    public void setMaDatSan(Integer maDatSan) { this.maDatSan = maDatSan; }
    public void setStatus(ThongTinDatSan.BookingStatus status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }

}

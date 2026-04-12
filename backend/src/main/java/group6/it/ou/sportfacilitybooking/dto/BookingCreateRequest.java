package group6.it.ou.sportfacilitybooking.dto;


import group6.it.ou.sportfacilitybooking.entity.BookingStatus;

/**
 * Dùng cho các PATCH thay đổi trạng thái:
 *   PATCH /api/bookings/{id}/cancel   (API-23)
 *   PATCH /api/bookings/{id}/confirm  (API-24)
 *   PATCH /api/bookings/{id}/reject   (API-25)
 *   POST  /api/bookings/{id}/checkin  (API-26)
 *
 * Trả về gọn: chỉ xác nhận maDatSan + status mới + message.
 */
public class BookingCreateRequest {
    private Integer maDatSan;
    private BookingStatus.BookingStatus status;
    private String message;

    // ---- NoArgs ----
    public BookingCreateRequest() {}

    // ---- AllArgs ----
    public BookingCreateRequest(Integer maDatSan, BookingStatus.BookingStatus status, String message) {
        this.maDatSan = maDatSan;
        this.status = status;
        this.message = message;
    }

    // ---- Getters ----
    public Integer getMaDatSan() { return maDatSan; }
    public BookingStatus.BookingStatus getStatus() { return status; }
    public String getMessage() { return message; }

    // ---- Setters ----
    public void setMaDatSan(Integer maDatSan) { this.maDatSan = maDatSan; }
    public void setStatus(BookingStatus.BookingStatus status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }

}

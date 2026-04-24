package group6.it.ou.sportfacilitybooking.dto;

import jakarta.validation.constraints.NotNull;

public class CheckInRequest {
    @NotNull(message = "ID đặt sân không được để trống")
    private Long bookingId;

    private String note;

    public CheckInRequest() {}

    public CheckInRequest(Long bookingId) {
        this.bookingId = bookingId;
    }

    // Getters & Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
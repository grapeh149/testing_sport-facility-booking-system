package group6.it.ou.sportfacilitybooking.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class BookingCreateRequest {
    @NotNull(message = "ID sân không được để trống")
    private Long courtId;

    @NotNull(message = "ID khung giờ không được để trống")
    private Long timeSlotId;

    @NotNull(message = "Ngày đặt không được để trống")
    private LocalDate bookingDate;

    public BookingCreateRequest() {}

    // Getters & Setters
    public Long getCourtId() { return courtId; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }

    public Long getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(Long timeSlotId) { this.timeSlotId = timeSlotId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
}

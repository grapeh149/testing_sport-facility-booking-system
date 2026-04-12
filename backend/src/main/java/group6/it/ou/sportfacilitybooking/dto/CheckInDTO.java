package group6.it.ou.sportfacilitybooking.dto;

import java.time.LocalDateTime;

public class CheckInDTO {
    private Long id;
    private Long bookingId;
    private Long checkedByUserId;
    private LocalDateTime checkedInAt;
    private String note;

    public CheckInDTO() {}

    public CheckInDTO(Long id, Long bookingId) {
        this.id = id;
        this.bookingId = bookingId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getCheckedByUserId() { return checkedByUserId; }
    public void setCheckedByUserId(Long checkedByUserId) { this.checkedByUserId = checkedByUserId; }

    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

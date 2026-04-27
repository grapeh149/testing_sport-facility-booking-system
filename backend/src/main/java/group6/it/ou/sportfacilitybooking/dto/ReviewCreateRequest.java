package group6.it.ou.sportfacilitybooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReviewCreateRequest {
    @NotNull(message = "ID đặt sân không được để trống")
    private Long bookingId;

    @NotNull(message = "Đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá phải từ 1-5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1-5 sao")
    private Byte rating;

    @NotEmpty(message = "Nhận xét không được để trống")
    private String comment;

    public ReviewCreateRequest() {}

    // Getters & Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Byte getRating() { return rating; }
    public void setRating(Byte rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}

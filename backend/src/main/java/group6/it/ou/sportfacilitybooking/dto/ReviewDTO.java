package group6.it.ou.sportfacilitybooking.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Integer maReview;
    private Integer maKH;
    private Integer maSan;
    private String comment;
    private LocalDateTime reviewDate;
    private Integer rating;

    public Integer getMaReview() { return maReview; }
    public void setMaReview(Integer maReview) { this.maReview = maReview; }

    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    public Integer getMaSan() { return maSan; }
    public void setMaSan(Integer maSan) { this.maSan = maSan; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}

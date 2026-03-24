package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaReview")
    private Integer maReview;

    @Column(name = "MaKH", nullable = false)
    private Integer maKH;

    @Column(name = "MaSan", nullable = false)
    private Integer maSan;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "Rating")
    private Integer rating;

    @Column(name = "ReviewDate")
    private LocalDateTime reviewDate;

//Getter&Setter

    public Integer getMaReview() { return maReview; }
    public void setMaReview(Integer maReview) { this.maReview = maReview; }

    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    public Integer getMaSan() { return maSan; }
    public void setMaSan(Integer maSan) { this.maSan = maSan; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
}

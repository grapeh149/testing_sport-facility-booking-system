package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maReview;
    private String comment;
    private LocalDateTime reviewDate;
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maSan")
    private SanTheThao sanTheThao;

    public Integer getMaReview() { return maReview; }
    public void setMaReview(Integer maReview) { this.maReview = maReview; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public SanTheThao getSanTheThao() { return sanTheThao; }
    public void setSanTheThao(SanTheThao sanTheThao) { this.sanTheThao = sanTheThao; }
}
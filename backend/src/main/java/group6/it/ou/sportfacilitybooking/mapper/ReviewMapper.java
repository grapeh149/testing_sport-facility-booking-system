package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.Review;

import java.time.LocalDateTime;

@Component
public class ReviewMapper {

    // Entity → DTO (trả ra ngoài)
    public ReviewDTO toDTO(Review entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setMaReview(entity.getMaReview());
        dto.setMaKH(entity.getMaKH());
        dto.setMaSan(entity.getMaSan());
        dto.setComment(entity.getComment());
        dto.setRating(entity.getRating());
        dto.setReviewDate(entity.getReviewDate());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public Review toEntity(ReviewDTO dto) {
        Review entity = new Review();
        entity.setMaReview(dto.getMaReview());
        entity.setMaKH(dto.getMaKH());
        entity.setMaSan(dto.getMaSan());
        entity.setComment(dto.getComment());
        entity.setRating(dto.getRating());
        entity.setReviewDate(dto.getReviewDate());
        return entity;
    }
}

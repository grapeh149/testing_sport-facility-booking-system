package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.Review;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setMaReview(entity.getMaReview());
        dto.setComment(entity.getComment());
        dto.setReviewDate(entity.getReviewDate());
        dto.setRating(entity.getRating());
        if (entity.getKhachHang() != null)
            dto.setMaKH(entity.getKhachHang().getMaKH());
        if (entity.getSanTheThao() != null)
            dto.setMaSan(entity.getSanTheThao().getMaSan());
        return dto;
    }

    public Review toEntity(ReviewDTO dto) {
        Review entity = new Review();
        entity.setMaReview(dto.getMaReview());
        entity.setComment(dto.getComment());
        entity.setReviewDate(dto.getReviewDate());
        entity.setRating(dto.getRating());
        return entity;
    }
}
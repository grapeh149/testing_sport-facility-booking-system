package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.Review;

@Component
public class ReviewMapper {
    public ReviewDTO toDTO(Review entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setComment(entity.getComment());
        dto.setRating(entity.getRating());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getBooking() != null)
            dto.setBookingId(entity.getBooking().getId());
        if (entity.getFacility() != null)
            dto.setFacilityId(entity.getFacility().getId());
        if (entity.getUser() != null)
            dto.setUserId(entity.getUser().getId());
        return dto;
    }

    public Review toEntity(ReviewDTO dto) {
        Review entity = new Review();
        entity.setId(dto.getId());
        entity.setComment(dto.getComment());
        entity.setRating(dto.getRating());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }
}

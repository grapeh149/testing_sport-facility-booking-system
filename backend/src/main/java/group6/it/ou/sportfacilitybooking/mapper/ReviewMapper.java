package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Review;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review entity) {
        if (entity == null) return null;
        
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBooking().getId());
        dto.setCustomerId(entity.getCustomer().getId());
        dto.setCustomerName(entity.getCustomer().getFullName());
        dto.setCustomerAvatarUrl(entity.getCustomer().getAvatarUrl());
        dto.setFacilityId(entity.getFacility().getId());
        if (entity.getBooking() != null && entity.getBooking().getCourt() != null) {
            dto.setCourtName(entity.getBooking().getCourt().getName());
        }
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setOwnerReply(entity.getOwnerReply());
        dto.setIsVisible(entity.getIsVisible());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Review toEntity(ReviewDTO dto) {
        if (dto == null) return null;
        
        Review entity = new Review();
        entity.setId(dto.getId());
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        entity.setOwnerReply(dto.getOwnerReply());
        entity.setIsVisible(dto.getIsVisible());
        return entity;
    }

    public Review toEntity(ReviewCreateRequest request) {
        if (request == null) return null;
        
        Review entity = new Review();
        entity.setRating(request.getRating());
        entity.setComment(request.getComment());
        return entity;
    }
}

package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;
import group6.it.ou.sportfacilitybooking.dto.FacilityCreateRequest;
import java.time.LocalTime;

@Component
public class FacilityMapper {

    public FacilityDTO toDTO(Facility entity) {
        if (entity == null) return null;
        
        FacilityDTO dto = new FacilityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setDistrict(entity.getDistrict());
        dto.setCity(entity.getCity());
        dto.setPhone(entity.getPhone());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus().toString());
        dto.setAvgRating(entity.getAvgRating());
        dto.setTotalReviews(entity.getTotalReviews());
        dto.setCoverImageUrl(entity.getCoverImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        
        // Add owner information
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getId());
            dto.setOwnerName(entity.getOwner().getFullName());
        }
        
        return dto;
    }

    public Facility toEntity(FacilityDTO dto) {
        if (dto == null) return null;
        
        Facility entity = new Facility();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setDistrict(dto.getDistrict());
        entity.setCity(dto.getCity());
        entity.setPhone(dto.getPhone());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public Facility toEntity(FacilityCreateRequest request) {
        if (request == null) return null;
        
        Facility entity = new Facility();
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setDistrict(request.getDistrict());
        entity.setCity(request.getCity());
        entity.setPhone(request.getPhone());
        entity.setDescription(request.getDescription());
        entity.setCommissionRate(request.getCommissionRate());
        entity.setCancelBeforeHours(request.getCancelBeforeHours());
        entity.setAutoConfirm(request.getAutoConfirm() != null ? request.getAutoConfirm() : false);
        
        // Parse time strings to LocalTime
        if (request.getOpenTime() != null && !request.getOpenTime().isEmpty()) {
            entity.setOpenTime(LocalTime.parse(request.getOpenTime()));
        }
        if (request.getCloseTime() != null && !request.getCloseTime().isEmpty()) {
            entity.setCloseTime(LocalTime.parse(request.getCloseTime()));
        }
        
        return entity;
    }
}

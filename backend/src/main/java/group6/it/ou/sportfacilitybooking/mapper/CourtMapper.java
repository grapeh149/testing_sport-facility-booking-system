package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;

@Component
public class CourtMapper {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private SportTypeMapper sportTypeMapper;

    public CourtDTO toDTO(Court entity) {
        if (entity == null) return null;
        
        CourtDTO dto = new CourtDTO();
        dto.setId(entity.getId());
        dto.setFacilityId(entity.getFacility().getId());
        dto.setSportTypeId(entity.getSportType().getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSurfaceType(entity.getSurfaceType());
        dto.setIsIndoor(entity.getIsIndoor());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        
        // Map nested facility and sportType
        if (entity.getFacility() != null) {
            dto.setFacility(facilityMapper.toDTO(entity.getFacility()));
        }
        if (entity.getSportType() != null) {
            dto.setSportType(sportTypeMapper.toDTO(entity.getSportType()));
        }
        
        return dto;
    }

    public Court toEntity(CourtDTO dto) {
        if (dto == null) return null;
        
        Court entity = new Court();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSurfaceType(dto.getSurfaceType());
        entity.setIsIndoor(dto.getIsIndoor());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }

    public Court toEntity(CourtCreateRequest request) {
        if (request == null) return null;
        
        Court entity = new Court();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSurfaceType(request.getSurfaceType());
        entity.setIsIndoor(request.getIsIndoor());
        entity.setIsActive(request.getIsActive());
        return entity;
    }
}

package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;

@Component
public class SportTypeMapper {

    public SportTypeDTO toDTO(SportType entity) {
        if (entity == null) return null;
        
        SportTypeDTO dto = new SportTypeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIconUrl(entity.getIconUrl());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public SportType toEntity(SportTypeDTO dto) {
        if (dto == null) return null;
        
        SportType entity = new SportType();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setIconUrl(dto.getIconUrl());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}

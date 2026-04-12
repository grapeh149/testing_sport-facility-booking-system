package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.entity.SportType;

@Component
public class SportTypeMapper {

    // Entity → DTO (trả ra ngoài)
    public SportTypeDTO toDTO(SportType entity) {
        SportTypeDTO dto = new SportTypeDTO();
        dto.setMaLoaiSan(entity.getMaLoaiSan());
        dto.setTenLoaiSan(entity.getTenLoaiSan());
        dto.setHinhAnh(entity.getHinhAnh());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public SportType toEntity(SportTypeDTO dto) {
        SportType entity = new SportType();
        entity.setMaLoaiSan(dto.getMaLoaiSan());
        entity.setTenLoaiSan(dto.getTenLoaiSan());
        entity.setHinhAnh(dto.getHinhAnh());
        return entity;
    }
}

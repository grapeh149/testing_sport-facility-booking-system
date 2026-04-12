package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.entity.Court;

@Component
public class CourtMapper {

    // Entity → DTO (trả ra ngoài)
    public CourtDTO toDTO(Court entity) {
        CourtDTO dto = new CourtDTO();
        dto.setMaSan(entity.getMaSan());
        dto.setMaLoaiSan(entity.getMaLoaiSan());
        dto.setMaChiNhanh(entity.getMaChiNhanh());
        dto.setSoSan(entity.getSoSan());
        dto.setGhiChu(entity.getGhiChu());
        dto.setHinhAnh(entity.getHinhAnh());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public Court toEntity(CourtDTO dto) {
        Court entity = new Court();
        entity.setMaSan(dto.getMaSan());
        entity.setMaLoaiSan(dto.getMaLoaiSan());
        entity.setMaChiNhanh(dto.getMaChiNhanh());
        entity.setSoSan(dto.getSoSan());
        entity.setGhiChu(dto.getGhiChu());
        entity.setHinhAnh(dto.getHinhAnh());
        return entity;
    }
}

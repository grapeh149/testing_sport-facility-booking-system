package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.ChiNhanhDTO;
import group6.it.ou.sportfacilitybooking.entity.ChiNhanh;

@Component
public class ChiNhanhMapper {

    // Entity → DTO (trả ra ngoài)
    public ChiNhanhDTO toDTO(ChiNhanh entity) {
        ChiNhanhDTO dto = new ChiNhanhDTO();
        dto.setMaChiNhanh(entity.getMaChiNhanh());
        dto.setTenChiNhanh(entity.getTenChiNhanh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setHinhAnh(entity.getHinhAnh());
        dto.setGhiChu(entity.getGhiChu());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public ChiNhanh toEntity(ChiNhanhDTO dto) {
        ChiNhanh entity = new ChiNhanh();
        entity.setMaChiNhanh(dto.getMaChiNhanh());
        entity.setTenChiNhanh(dto.getTenChiNhanh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setHinhAnh(dto.getHinhAnh());
        entity.setGhiChu(dto.getGhiChu());
        return entity;
    }
}

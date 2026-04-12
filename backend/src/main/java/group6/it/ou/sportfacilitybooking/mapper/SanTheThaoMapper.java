package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.SanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.entity.SanTheThao;

@Component
public class SanTheThaoMapper {

    // Entity → DTO (trả ra ngoài)
    public SanTheThaoDTO toDTO(SanTheThao entity) {
        SanTheThaoDTO dto = new SanTheThaoDTO();
        dto.setMaSan(entity.getMaSan());
        dto.setMaLoaiSan(entity.getMaLoaiSan());
        dto.setMaChiNhanh(entity.getMaChiNhanh());
        dto.setSoSan(entity.getSoSan());
        dto.setGhiChu(entity.getGhiChu());
        dto.setHinhAnh(entity.getHinhAnh());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public SanTheThao toEntity(SanTheThaoDTO dto) {
        SanTheThao entity = new SanTheThao();
        entity.setMaSan(dto.getMaSan());
        entity.setMaLoaiSan(dto.getMaLoaiSan());
        entity.setMaChiNhanh(dto.getMaChiNhanh());
        entity.setSoSan(dto.getSoSan());
        entity.setGhiChu(dto.getGhiChu());
        entity.setHinhAnh(dto.getHinhAnh());
        return entity;
    }
}

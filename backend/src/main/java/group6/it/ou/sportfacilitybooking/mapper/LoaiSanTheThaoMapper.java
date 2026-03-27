package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.LoaiSanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.entity.LoaiSanTheThao;

@Component
public class LoaiSanTheThaoMapper {

    // Entity → DTO (trả ra ngoài)
    public LoaiSanTheThaoDTO toDTO(LoaiSanTheThao entity) {
        LoaiSanTheThaoDTO dto = new LoaiSanTheThaoDTO();
        dto.setMaLoaiSan(entity.getMaLoaiSan());
        dto.setTenLoaiSan(entity.getTenLoaiSan());
        dto.setHinhAnh(entity.getHinhAnh());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public LoaiSanTheThao toEntity(LoaiSanTheThaoDTO dto) {
        LoaiSanTheThao entity = new LoaiSanTheThao();
        entity.setMaLoaiSan(dto.getMaLoaiSan());
        entity.setTenLoaiSan(dto.getTenLoaiSan());
        entity.setHinhAnh(dto.getHinhAnh());
        return entity;
    }
}

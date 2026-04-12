package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Facility;

@Component
public class FacilityMapper {

    // Entity → DTO (trả ra ngoài)
    public CourtCreateRequest toDTO(Facility entity) {
        CourtCreateRequest dto = new CourtCreateRequest();
        dto.setMaChiNhanh(entity.getMaChiNhanh());
        dto.setTenChiNhanh(entity.getTenChiNhanh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setHinhAnh(entity.getHinhAnh());
        dto.setGhiChu(entity.getGhiChu());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public Facility toEntity(CourtCreateRequest dto) {
        Facility entity = new Facility();
        entity.setMaChiNhanh(dto.getMaChiNhanh());
        entity.setTenChiNhanh(dto.getTenChiNhanh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setHinhAnh(dto.getHinhAnh());
        entity.setGhiChu(dto.getGhiChu());
        return entity;
    }
}

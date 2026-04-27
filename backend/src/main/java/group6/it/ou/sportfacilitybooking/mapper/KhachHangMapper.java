package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.KhachHangDTO;
import group6.it.ou.sportfacilitybooking.entity.KhachHang;

@Component
public class KhachHangMapper {

    // Entity → DTO (trả ra ngoài)
    public KhachHangDTO toDTO(KhachHang entity) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKH(entity.getMaKH());
        dto.setHo(entity.getHo());
        dto.setTen(entity.getTen());
        dto.setSdt(entity.getSdt());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public KhachHang toEntity(KhachHangDTO dto) {
        KhachHang entity = new KhachHang();
        entity.setMaKH(dto.getMaKH());
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}

package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.NhanVienDTO;
import group6.it.ou.sportfacilitybooking.entity.NhanVien;

@Component
public class NhanVienMapper {

    // Entity → DTO (trả ra ngoài)
    public NhanVienDTO toDTO(NhanVien entity) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(entity.getMaNhanVien());
        dto.setHo(entity.getHo());
        dto.setTen(entity.getTen());
        dto.setSdt(entity.getSdt());
        dto.setTitle(entity.getTitle());
        dto.setRole(entity.getRole());
        dto.setMaTaiKhoan(entity.getMaTaiKhoan());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public NhanVien toEntity(NhanVienDTO dto) {
        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setTitle(dto.getTitle());
        entity.setRole(dto.getRole());
        entity.setMaTaiKhoan(dto.getMaTaiKhoan());
        return entity;
    }
}

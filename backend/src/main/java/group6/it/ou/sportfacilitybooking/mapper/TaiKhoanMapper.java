package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.TaiKhoanDTO;
import group6.it.ou.sportfacilitybooking.entity.TaiKhoan;

@Component
public class TaiKhoanMapper {

    // Entity → DTO (trả ra ngoài)
    public TaiKhoanDTO toDTO(TaiKhoan entity) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setMaTaiKhoan(entity.getMaTaiKhoan());
        dto.setTenTaiKhoan(entity.getTenTaiKhoan());
        dto.setMatKhau(entity.getMatKhau());
        dto.setRole(entity.getRole());
        dto.setMaNhanVien(entity.getMaNhanVien());
        dto.setMaKH(entity.getMaKH());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public TaiKhoan toEntity(TaiKhoanDTO dto) {
        TaiKhoan entity = new TaiKhoan();
        entity.setMaTaiKhoan(dto.getMaTaiKhoan());
        entity.setTenTaiKhoan(dto.getTenTaiKhoan());
        entity.setMatKhau(dto.getMatKhau());
        entity.setRole(dto.getRole());
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setMaKH(dto.getMaKH());
        return entity;
    }
}

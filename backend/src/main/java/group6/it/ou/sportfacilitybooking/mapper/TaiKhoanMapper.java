package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.TaiKhoanDTO;
import group6.it.ou.sportfacilitybooking.entity.TaiKhoan;

@Component
public class TaiKhoanMapper {

    public TaiKhoanDTO toDTO(TaiKhoan entity) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setMaTaiKhoan(entity.getMaTaiKhoan());
        dto.setTenTaiKhoan(entity.getTenTaiKhoan());
        dto.setMatKhau(entity.getMatKhau());
        dto.setRole(entity.getRole());
        if (entity.getNhanVien() != null)
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
        if (entity.getKhachHang() != null)
            dto.setMaKH(entity.getKhachHang().getMaKH());
        return dto;
    }

    public TaiKhoan toEntity(TaiKhoanDTO dto) {
        TaiKhoan entity = new TaiKhoan();
        entity.setMaTaiKhoan(dto.getMaTaiKhoan());
        entity.setTenTaiKhoan(dto.getTenTaiKhoan());
        entity.setMatKhau(dto.getMatKhau());
        entity.setRole(dto.getRole());
        return entity;
    }
}
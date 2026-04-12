package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.entity.User;

@Component
public class TaiKhoanMapper {

    public UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
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

    public User toEntity(UserDTO dto) {
        User entity = new User();
        entity.setMaTaiKhoan(dto.getMaTaiKhoan());
        entity.setTenTaiKhoan(dto.getTenTaiKhoan());
        entity.setMatKhau(dto.getMatKhau());
        entity.setRole(dto.getRole());
        return entity;
    }
}
package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;

import group6.it.ou.sportfacilitybooking.dto.KhachHangDTO;
import group6.it.ou.sportfacilitybooking.entity.Payment;

@Component
public class UserMapper {

    // Entity → DTO (trả ra ngoài)
    public KhachHangDTO toDTO(Payment entity) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKH(entity.getMaKH());
        dto.setHo(entity.getHo());
        dto.setTen(entity.getTen());
        dto.setSdt(entity.getSdt());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    // DTO → Entity (lưu vào DB)
    public Payment toEntity(KhachHangDTO dto) {
        Payment entity = new Payment();
        entity.setMaKH(dto.getMaKH());
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}

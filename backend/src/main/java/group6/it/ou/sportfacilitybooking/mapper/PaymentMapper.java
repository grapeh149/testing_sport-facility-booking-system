package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.NhanVienDTO;
import group6.it.ou.sportfacilitybooking.entity.PaymentStatus;

@Component
public class PaymentMapper {

    public NhanVienDTO toDTO(PaymentStatus entity) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(entity.getMaNhanVien());
        dto.setHo(entity.getHo());
        dto.setTen(entity.getTen());
        dto.setSdt(entity.getSdt());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    public PaymentStatus toEntity(NhanVienDTO dto) {
        PaymentStatus entity = new PaymentStatus();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setTitle(dto.getTitle());
        return entity;
    }
}
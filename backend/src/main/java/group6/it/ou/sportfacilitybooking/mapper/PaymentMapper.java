package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.entity.PaymentType;
import group6.it.ou.sportfacilitybooking.entity.PaymentStatus;

@Component
public class PaymentMapper {
    
    // Entity → DTO
    public PaymentDTO toDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setVnpayTxnRef(entity.getVnpayTxnRef());
        dto.setVnpayTxnNo(entity.getVnpayTxnNo());
        dto.setAmount(entity.getAmount());
        
        if (entity.getPaymentType() != null) {
            dto.setPaymentType(entity.getPaymentType().name());
        }
        
        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }
        
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setBankCode(entity.getBankCode());
        dto.setPaidAt(entity.getPaidAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setDescription(entity.getDescription());
        dto.setTransactionRef(entity.getTransactionRef());
        
        if (entity.getBooking() != null) {
            dto.setBookingId(entity.getBooking().getId());
        }
        
        return dto;
    }

    // DTO → Entity
    public Payment toEntity(PaymentDTO dto) {
        Payment entity = new Payment();
        entity.setId(dto.getId());
        entity.setVnpayTxnRef(dto.getVnpayTxnRef());
        entity.setVnpayTxnNo(dto.getVnpayTxnNo());
        entity.setAmount(dto.getAmount());
        
        if (dto.getPaymentType() != null) {
            entity.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
        }
        
        if (dto.getStatus() != null) {
            entity.setStatus(PaymentStatus.valueOf(dto.getStatus()));
        }
        
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setBankCode(dto.getBankCode());
        entity.setPaidAt(dto.getPaidAt());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setDescription(dto.getDescription());
        entity.setTransactionRef(dto.getTransactionRef());
        
        return entity;
    }
}

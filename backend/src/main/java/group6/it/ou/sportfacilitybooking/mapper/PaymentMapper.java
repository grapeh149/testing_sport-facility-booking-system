package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment entity) {
        if (entity == null) return null;
        
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBooking().getId());
        dto.setVnpayTxnRef(entity.getVnpayTxnRef());
        dto.setAmount(entity.getAmount());
        dto.setPaymentType(entity.getPaymentType().toString());
        dto.setStatus(entity.getStatus().toString());
        dto.setBankCode(entity.getBankCode());
        return dto;
    }

    public Payment toEntity(PaymentDTO dto) {
        if (dto == null) return null;
        
        Payment entity = new Payment();
        entity.setId(dto.getId());
        entity.setVnpayTxnRef(dto.getVnpayTxnRef());
        entity.setAmount(dto.getAmount());
        entity.setBankCode(dto.getBankCode());
        return entity;
    }
}

package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;

@Component
public class BookingMapper {

    public BookingDTO toDTO(Booking entity) {
        if (entity == null) return null;

        BookingDTO dto = new BookingDTO();
        dto.setId(entity.getId());
        dto.setBookingCode(entity.getBookingCode());
        dto.setCustomerId(entity.getCustomer().getId());
        dto.setCustomerName(entity.getCustomer().getFullName());
        dto.setCourtId(entity.getCourt().getId());
        dto.setCourtName(entity.getCourt().getName());
        dto.setFacilityName(entity.getCourt().getFacility().getName());
        dto.setBookingDate(entity.getBookingDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setDepositAmount(entity.getDepositAmount());
        dto.setStatus(entity.getStatus().toString());
        dto.setNote(entity.getNote());
        return dto;
    }

    public Booking toEntity(BookingDTO dto) {
        if (dto == null) return null;

        Booking entity = new Booking();
        entity.setId(dto.getId());
        entity.setBookingCode(dto.getBookingCode());
        entity.setBookingDate(dto.getBookingDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setDepositAmount(dto.getDepositAmount());
        entity.setNote(dto.getNote());
        return entity;
    }
}

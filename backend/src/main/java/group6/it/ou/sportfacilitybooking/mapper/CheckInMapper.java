package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.CheckIn;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;

@Component
public class CheckInMapper {

    public CheckInDTO toDTO(CheckIn entity) {
        if (entity == null) return null;

        CheckInDTO dto = new CheckInDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBooking().getId());
        dto.setCheckedByUserId(entity.getCheckedByUser().getId());
        dto.setCheckedInAt(entity.getCheckedInAt());
        dto.setNote(entity.getNote());
        return dto;
    }

    public CheckIn toEntity(CheckInDTO dto) {
        if (dto == null) return null;

        CheckIn entity = new CheckIn();
        entity.setId(dto.getId());
        entity.setCheckedInAt(dto.getCheckedInAt());
        entity.setNote(dto.getNote());
        return entity;
    }

    public CheckIn toEntity(CheckInRequest request) {
        if (request == null) return null;

        CheckIn entity = new CheckIn();
        entity.setNote(request.getNote());
        return entity;
    }
}

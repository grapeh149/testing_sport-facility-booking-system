package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import group6.it.ou.sportfacilitybooking.dto.TimeSlotDTO;

@Component
public class TimeSlotMapper {

    public TimeSlotDTO toDTO(TimeSlot entity) {
        if (entity == null) return null;

        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setId(entity.getId());
        dto.setCourtId(entity.getCourt().getId());
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setPrice(entity.getPrice());
        dto.setDepositRate(entity.getDepositRate());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public TimeSlot toEntity(TimeSlotDTO dto) {
        if (dto == null) return null;

        TimeSlot entity = new TimeSlot();
        entity.setId(dto.getId());
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setPrice(dto.getPrice());
        entity.setDepositRate(dto.getDepositRate());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}

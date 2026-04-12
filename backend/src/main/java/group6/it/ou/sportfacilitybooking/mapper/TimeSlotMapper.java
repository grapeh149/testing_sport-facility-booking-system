package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.DatSan.TimeSlotDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.TimeSlotRequest;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {
    /**
     * Request → Entity (dùng cho POST tạo mới)
     * maGio để null, DB sẽ tự sinh
     */
    public TimeSlot toEntity(TimeSlotRequest request) {
        TimeSlot entity = new TimeSlot();
        entity.setGioBatDau(request.getGioBatDau());
        entity.setGioKetThuc(request.getGioKetThuc());
        entity.setGiaTien(request.getGiaTien());
        return entity;
    }

    /**
     * Request → Entity hiện có (dùng cho PUT cập nhật)
     * Không tạo entity mới, chỉ update field — giữ nguyên maGio và relations
     */
    public void updateEntity(TimeSlotRequest request, TimeSlot entity) {
        entity.setGioBatDau(request.getGioBatDau());
        entity.setGioKetThuc(request.getGioKetThuc());
        entity.setGiaTien(request.getGiaTien());
    }

    /**
     * Entity → Response (dùng cho GET)
     */
    public TimeSlotDTO toResponse(TimeSlot entity) {
        return new TimeSlotDTO(
                entity.getMaGio(),
                entity.getGioBatDau(),
                entity.getGioKetThuc(),
                entity.getGiaTien()
        );
    }

}

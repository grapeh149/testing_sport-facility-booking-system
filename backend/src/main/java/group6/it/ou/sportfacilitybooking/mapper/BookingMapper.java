package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatSanRequest;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    @Autowired
    private CheckInMapper depositMapper;

    /**
     * Request → Entity (dùng khi POST tạo booking API-20)
     * - status mặc định = PENDING
     * - totalPrice = giaTien từ TimeSlot (Service truyền vào)
     * - Các FK object (sanTheThao, timeSlot, khachHang) được truyền riêng từ Service
     */
    public BookingStatus toEntity(DatSanRequest request,
                                   Court sanTheThao,
                                   TimeSlot timeSlot,
                                   Payment khachHang) {
        BookingStatus entity = new BookingStatus();
        entity.setBookingDate(request.getBookingDate());
        entity.setStatus(BookingStatus.BookingStatus.PENDING);
        entity.setTotalPrice(timeSlot.getGiaTien()); // tính từ TimeSlot
        entity.setSanTheThao(sanTheThao);
        entity.setTimeSlot(timeSlot);
        entity.setKhachHang(khachHang);
        return entity;
    }

    /**
     * Entity → BookingSummaryResponse
     * Dùng cho GET list (API-21, API-27)
     */
    public CheckInDTO toSummaryResponse(BookingStatus entity) {
        Payment kh = entity.getKhachHang();
        TimeSlot ts = entity.getTimeSlot();
        Court san = entity.getSanTheThao();

        return new CheckInDTO(
                entity.getMaDatSan(),
                "San #" + san.getMaSan() + " - " + san.getSoSan(), // tenSan
                kh.getHo() + " " + kh.getTen(),                    // tenKhachHang
                entity.getBookingDate(),
                ts.getGioBatDau(),
                ts.getGioKetThuc(),
                entity.getTotalPrice(),
                entity.getStatus()
        );
    }

    /**
     * Entity → BookingDetailResponse
     * Dùng cho GET chi tiết (API-22) — đầy đủ nhất
     */
    public CheckInRequest toDetailResponse(BookingStatus entity) {
        Payment kh = entity.getKhachHang();
        TimeSlot ts = entity.getTimeSlot();
        Court san = entity.getSanTheThao();

        return new CheckInRequest(
                entity.getMaDatSan(),
                entity.getBookingDate(),
                entity.getTotalPrice(),
                entity.getStatus(),
                san.getMaSan(),
                "San #" + san.getSoSan(),
                ts.getMaGio(),
                ts.getGioBatDau(),
                ts.getGioKetThuc(),
                kh.getMaKH(),
                kh.getHo() + " " + kh.getTen(),
                kh.getSdt(),
                depositMapper.toResponse(entity.getThongTinDatCoc()) // có thể null
        );
    }

    /**
     * Entity → BookingStatusResponse
     * Dùng sau khi PATCH cancel / confirm / reject / checkin
     */
    public BookingCreateRequest toStatusResponse(BookingStatus entity, String message) {
        return new BookingCreateRequest(
                entity.getMaDatSan(),
                entity.getStatus(),
                message
        );
    }


}

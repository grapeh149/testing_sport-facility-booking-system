package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.DatSan.ChiTietDatSanDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatSanDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatSanRequest;
import group6.it.ou.sportfacilitybooking.dto.DatSan.TrangThaiDatSanRequest;
import group6.it.ou.sportfacilitybooking.entity.KhachHang;
import group6.it.ou.sportfacilitybooking.entity.SanTheThao;
import group6.it.ou.sportfacilitybooking.entity.ThongTinDatSan;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatSanMapper {

    @Autowired
    private DatCocMapper depositMapper;

    /**
     * Request → Entity (dùng khi POST tạo booking API-20)
     * - status mặc định = PENDING
     * - totalPrice = giaTien từ TimeSlot (Service truyền vào)
     * - Các FK object (sanTheThao, timeSlot, khachHang) được truyền riêng từ Service
     */
    public ThongTinDatSan toEntity(DatSanRequest request,
                                   SanTheThao sanTheThao,
                                   TimeSlot timeSlot,
                                   KhachHang khachHang) {
        ThongTinDatSan entity = new ThongTinDatSan();
        entity.setBookingDate(request.getBookingDate());
        entity.setStatus(ThongTinDatSan.BookingStatus.PENDING);
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
    public DatSanDTO toSummaryResponse(ThongTinDatSan entity) {
        KhachHang kh = entity.getKhachHang();
        TimeSlot ts = entity.getTimeSlot();
        SanTheThao san = entity.getSanTheThao();

        return new DatSanDTO(
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
    public ChiTietDatSanDTO toDetailResponse(ThongTinDatSan entity) {
        KhachHang kh = entity.getKhachHang();
        TimeSlot ts = entity.getTimeSlot();
        SanTheThao san = entity.getSanTheThao();

        return new ChiTietDatSanDTO(
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
    public TrangThaiDatSanRequest toStatusResponse(ThongTinDatSan entity, String message) {
        return new TrangThaiDatSanRequest(
                entity.getMaDatSan(),
                entity.getStatus(),
                message
        );
    }


}

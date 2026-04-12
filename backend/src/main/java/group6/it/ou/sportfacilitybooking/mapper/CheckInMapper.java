package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocRequest;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CheckInMapper {
    /**
     * Request → Entity (tạo cọc mới khi booking)
     * - tinhTrangThanhToan mặc định = PENDING (chưa xác nhận)
     * - ngayThanhToan = null (chưa thanh toán xong)
     * - thongTinDatSan sẽ được set ở Service sau khi lưu booking
     */
    public Booking toEntity(DatCocRequest request) {
        Booking entity = new Booking();
        entity.setTienCoc(request.getTienCoc());
        entity.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        entity.setTinhTrangThanhToan(Booking.PaymentStatus.PENDING); // mặc định
        entity.setNgayThanhToan(null);                        // chưa thanh toán
        return entity;
    }

    /**
     * Entity → Response (nhúng vào BookingDetailResponse)
     */
    public BookingDTO toResponse(Booking entity) {
        if (entity == null) return null; // booking chưa có cọc
        return new BookingDTO(
                entity.getMaDatCoc(),
                entity.getTienCoc(),
                entity.getPhuongThucThanhToan(),
                entity.getTinhTrangThanhToan(),
                entity.getNgayThanhToan()
        );
    }

    /**
     * Cập nhật trạng thái thanh toán thành PAID
     * Gọi khi xác nhận thanh toán thành công
     */
    public void markAsPaid(Booking entity) {
        entity.setTinhTrangThanhToan(Booking.PaymentStatus.PAID);
        entity.setNgayThanhToan(LocalDateTime.now());
    }
}

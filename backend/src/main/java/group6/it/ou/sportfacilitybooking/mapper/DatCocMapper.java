package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocRequest;
import group6.it.ou.sportfacilitybooking.entity.ThongTinDatCoc;

import java.time.LocalDateTime;

public class DatCocMapper {
    /**
     * Request → Entity (tạo cọc mới khi booking)
     * - tinhTrangThanhToan mặc định = PENDING (chưa xác nhận)
     * - ngayThanhToan = null (chưa thanh toán xong)
     * - thongTinDatSan sẽ được set ở Service sau khi lưu booking
     */
    public ThongTinDatCoc toEntity(DatCocRequest request) {
        ThongTinDatCoc entity = new ThongTinDatCoc();
        entity.setTienCoc(request.getTienCoc());
        entity.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        entity.setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus.PENDING); // mặc định
        entity.setNgayThanhToan(null);                        // chưa thanh toán
        return entity;
    }

    /**
     * Entity → Response (nhúng vào BookingDetailResponse)
     */
    public DatCocDTO toResponse(ThongTinDatCoc entity) {
        if (entity == null) return null; // booking chưa có cọc
        return new DatCocDTO(
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
    public void markAsPaid(ThongTinDatCoc entity) {
        entity.setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus.PAID);
        entity.setNgayThanhToan(LocalDateTime.now());
    }
}

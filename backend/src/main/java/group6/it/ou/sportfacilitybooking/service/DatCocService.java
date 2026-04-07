package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocRequest;
import group6.it.ou.sportfacilitybooking.entity.ThongTinDatCoc;
import group6.it.ou.sportfacilitybooking.entity.ThongTinDatSan;
import group6.it.ou.sportfacilitybooking.mapper.DatCocMapper;
import group6.it.ou.sportfacilitybooking.repository.ThongTinDatCocRepository;
import group6.it.ou.sportfacilitybooking.repository.ThongTinDatSanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DatCocService {
    private static final Set<String> VALID_PAYMENT_METHODS = Set.of("MOMO", "BANKING", "CASH");

    @Autowired
    private ThongTinDatCocRepository datCocRepository;

    @Autowired
    private ThongTinDatSanRepository datSanRepository;

    @Autowired
    private DatCocMapper datCocMapper;

    // ============================================================
    // READ (TC42): trả về toàn bộ lịch sử (FAILED + PAID) theo maDatSan
    // ============================================================
    // READ (TC42)
    public DatCocDTO getByMaDatSan(Integer maDatSan) {
        findBookingOrThrow(maDatSan);
        return datCocRepository.findByThongTinDatSanMaDatSan(maDatSan)
                .map(datCocMapper::toResponse)
                .orElse(null);
    }


    // ============================================================
    // CREATE (TC22–TC32, TC35, TC40) bỏ tc 22 pending
    // fail nhiều - update bản ghi cũ
    // ============================================================
    @Transactional
    public DatCocDTO createDatCoc(Integer maDatSan, DatCocRequest request) {
        ThongTinDatSan booking = findBookingOrThrow(maDatSan);

        // TC22–TC25: booking phải là CONFIRMED
        if (booking.getStatus() != ThongTinDatSan.BookingStatus.CONFIRMED &&
                booking.getStatus() != ThongTinDatSan.BookingStatus.PENDING
        ) {
             throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ tạo cọc khi booking có trạng thái CONFIRMED or PENDING. Trạng thái hiện tại: "
                            + booking.getStatus());
        }

        // TC32: đã có bản ghi PAID → không cho tạo/retry
        ThongTinDatCoc existing = booking.getThongTinDatCoc();
        if (existing != null
                && existing.getTinhTrangThanhToan() == ThongTinDatCoc.PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Booking này đã có cọc PAID. Không thể tạo thêm.");
        }

        // TC26 (= 20000), TC27 (âm): tienCoc phải >= 20000 (TC28 = 1 cho phép)
        BigDecimal tienCoc = request.getTienCoc();
        if (tienCoc == null || tienCoc.compareTo(BigDecimal.valueOf(20000)) < 0) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tiền cọc phải >= 20000.");
        }

        // TC31: tienCoc không được vượt totalPrice
        BigDecimal totalPrice = booking.getTotalPrice();
        if (tienCoc.compareTo(totalPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tiền cọc không được vượt quá tổng giá trị booking (" + totalPrice + ").");
        }

        // TC40: phương thức thanh toán phải nằm trong whitelist
        String pttt = request.getPhuongThucThanhToan();
        if (pttt == null || !VALID_PAYMENT_METHODS.contains(pttt.toUpperCase())) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phương thức thanh toán không hợp lệ. Chấp nhận: " + VALID_PAYMENT_METHODS);
        }

        ThongTinDatCoc entity;

        if (existing != null
                && existing.getTinhTrangThanhToan() == ThongTinDatCoc.PaymentStatus.FAILED) {
            // TC35: đã có FAILED → update lại bản ghi cũ (reset về PENDING)
            existing.setTienCoc(request.getTienCoc());
            existing.setPhuongThucThanhToan(pttt.toUpperCase());
            existing.setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus.PENDING);
            existing.setNgayThanhToan(null);
            entity = datCocRepository.save(existing);
        } else {
            // Chưa có cọc nào → tạo mới
            entity = datCocMapper.toEntity(request);
            entity.setThongTinDatSan(booking);
            entity = datCocRepository.save(entity);
        }

        return datCocMapper.toResponse(entity);
    }


    // ============================================================
    // UPDATE
    // ============================================================

    // PENDING → PAID (TC21): dùng markAsPaid() của mapper để set ngayThanhToan = now()
    @Transactional
    public DatCocDTO markAsPaid(Integer maDatCoc) {
        ThongTinDatCoc entity = findDatCocOrThrow(maDatCoc);
        validateStatus(entity, ThongTinDatCoc.PaymentStatus.PENDING, "markAsPaid");
        datCocMapper.markAsPaid(entity);
        entity = datCocRepository.save(entity);
        return datCocMapper.toResponse(entity);
    }

    // PENDING → FAILED (TC34): không set ngayThanhToan, booking vẫn CONFIRMED để retry
    @Transactional
    public DatCocDTO markAsFailed(Integer maDatCoc) {
        ThongTinDatCoc entity = findDatCocOrThrow(maDatCoc);
        validateStatus(entity, ThongTinDatCoc.PaymentStatus.PENDING, "markAsFailed");
        entity.setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus.FAILED);
        entity = datCocRepository.save(entity);
        return datCocMapper.toResponse(entity);
    }

//    // PAID → REFUNDED (TC36, TC37, TC39) -- HIỆN TẠI CHƯA CÓ REFUND KO LÀM CHỨC NĂNG HỦY MẤT TIỀN!
        // Bỏ tc TC36, TC37, TC39
//    // Trước bookingDate: soTienHoan = 100% tienCoc
//    // Sau/bằng bookingDate: soTienHoan = 0
//    @Transactional
//    public DatCocDTO processRefund(Integer maDatCoc) {
//        ThongTinDatCoc entity = findDatCocOrThrow(maDatCoc);
//        validateStatus(entity, ThongTinDatCoc.PaymentStatus.PAID, "processRefund");
//
//        LocalDate bookingDate = entity.getThongTinDatSan().getBookingDate();
//        BigDecimal soTienHoan = LocalDate.now().isBefore(bookingDate)
//                ? entity.getTienCoc()   // TC36: hủy trước deadline → hoàn 100%
//                : BigDecimal.ZERO;      // TC37: hủy sau deadline → hoàn 0
//
//        entity.setTinhTrangThanhToan(ThongTinDatCoc.PaymentStatus.REFUNDED);
//        entity = datCocRepository.save(entity);
//
//        // soTienHoan có thể trả về controller nếu cần mở rộng DatCocDTO
//        return datCocMapper.toResponse(entity);
//    }



    // ============================================================
    // DELETE — không xóa vật lý
    // Theo spec: soft delete chỉ là chuyển trạng thái
    // → Dùng markAsFailed() hoặc processRefund() tùy flow nghiệp vụ
    // Không expose physical delete.
    // ============================================================



    // ============================================================
    // Helpers
    // ============================================================
    private ThongTinDatSan findBookingOrThrow(Integer maDatSan) {
        return datSanRepository.findById(maDatSan)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy booking với MaDatSan: " + maDatSan));
    }

    private ThongTinDatCoc findDatCocOrThrow(Integer maDatCoc) {
        return datCocRepository.findById(maDatCoc)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy bản ghi cọc với MaDatCoc: " + maDatCoc));
    }

    private void validateStatus(ThongTinDatCoc entity,
                                ThongTinDatCoc.PaymentStatus required,
                                String action) {
        if (entity.getTinhTrangThanhToan() != required) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Không thể thực hiện '" + action + "' khi trạng thái là "
                            + entity.getTinhTrangThanhToan()
                            + ". Yêu cầu trạng thái: " + required);
        }

    }
    // DELETE — hard delete vật lý
    @Transactional
    public void hardDeleteDatCoc(Integer maDatCoc) {
        ThongTinDatCoc datCoc = findDatCocOrThrow(maDatCoc);
        datCocRepository.delete(datCoc);
    }



}

package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocRequest;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.mapper.CheckInMapper;
import group6.it.ou.sportfacilitybooking.repository.CheckInRepository;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
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
public class CheckInService {
    private static final Set<String> VALID_PAYMENT_METHODS = Set.of("MOMO", "BANKING", "CASH");

    @Autowired
    private CheckInRepository datCocRepository;

    @Autowired
    private BookingRepository datSanRepository;

    @Autowired
    private CheckInMapper datCocMapper;

    // ============================================================
    // READ (TC42): trả về toàn bộ lịch sử (FAILED + PAID) theo maDatSan
    // ============================================================
    // READ (TC42)
    public BookingDTO getByMaDatSan(Integer maDatSan) {
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
    public BookingDTO createDatCoc(Integer maDatSan, DatCocRequest request) {
        BookingStatus booking = findBookingOrThrow(maDatSan);

        // TC22–TC25: booking phải là CONFIRMED
        if (booking.getStatus() != BookingStatus.BookingStatus.CONFIRMED &&
                booking.getStatus() != BookingStatus.BookingStatus.PENDING
        ) {
             throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ tạo cọc khi booking có trạng thái CONFIRMED or PENDING. Trạng thái hiện tại: "
                            + booking.getStatus());
        }

        // TC32: đã có bản ghi PAID → không cho tạo/retry
        Booking existing = booking.getThongTinDatCoc();
        if (existing != null
                && existing.getTinhTrangThanhToan() == Booking.PaymentStatus.PAID) {
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

        Booking entity;

        if (existing != null
                && existing.getTinhTrangThanhToan() == Booking.PaymentStatus.FAILED) {
            // TC35: đã có FAILED → update lại bản ghi cũ (reset về PENDING)
            existing.setTienCoc(request.getTienCoc());
            existing.setPhuongThucThanhToan(pttt.toUpperCase());
            existing.setTinhTrangThanhToan(Booking.PaymentStatus.PENDING);
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
    public BookingDTO markAsPaid(Integer maDatCoc) {
        Booking entity = findDatCocOrThrow(maDatCoc);
        validateStatus(entity, Booking.PaymentStatus.PENDING, "markAsPaid");
        datCocMapper.markAsPaid(entity);
        entity = datCocRepository.save(entity);
        return datCocMapper.toResponse(entity);
    }

    // PENDING → FAILED (TC34): không set ngayThanhToan, booking vẫn CONFIRMED để retry
    @Transactional
    public BookingDTO markAsFailed(Integer maDatCoc) {
        Booking entity = findDatCocOrThrow(maDatCoc);
        validateStatus(entity, Booking.PaymentStatus.PENDING, "markAsFailed");
        entity.setTinhTrangThanhToan(Booking.PaymentStatus.FAILED);
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
    private BookingStatus findBookingOrThrow(Integer maDatSan) {
        return datSanRepository.findById(maDatSan)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy booking với MaDatSan: " + maDatSan));
    }

    private Booking findDatCocOrThrow(Integer maDatCoc) {
        return datCocRepository.findById(maDatCoc)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy bản ghi cọc với MaDatCoc: " + maDatCoc));
    }

    private void validateStatus(Booking entity,
                                Booking.PaymentStatus required,
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
        Booking datCoc = findDatCocOrThrow(maDatCoc);
        datCocRepository.delete(datCoc);
    }



}

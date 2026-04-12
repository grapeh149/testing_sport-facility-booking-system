package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO.DayCalendarDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO.SlotStatusDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO.WeekCalendarDTO;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatSanRequest;
import group6.it.ou.sportfacilitybooking.entity.*;
import group6.it.ou.sportfacilitybooking.mapper.CheckInMapper;
import group6.it.ou.sportfacilitybooking.mapper.BookingMapper;
import group6.it.ou.sportfacilitybooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository datSanRepository;

    @Autowired
    private CheckInRepository datCocRepository;

    @Autowired
    private CourtRepository sanTheThaoRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private UserRepository khachHangRepository;

    @Autowired
    private BookingMapper datSanMapper;

    @Autowired
    private CheckInMapper datCocMapper;

    // Các trạng thái còn "đang hoạt động" (chưa kết thúc)
    private static final List<BookingStatus.BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.BookingStatus.PENDING, BookingStatus.BookingStatus.CONFIRMED);


    // ============================================================
    // READ
    // ============================================================

    // Chi tiết theo maDatSan (API-22)
    public CheckInRequest getByMaDatSan(Integer maDatSan) {
        return datSanMapper.toDetailResponse(findOrThrow(maDatSan));
    }

    // Danh sách theo khách hàng (API-27)
    public List<CheckInDTO> getByMaKH(Integer maKH) {
        return datSanRepository.findByKhachHangMaKH(maKH)
                .stream()
                .map(datSanMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // Danh sách theo sân (API-21)
    public List<CheckInDTO> getByMaSan(Integer maSan) {
        return datSanRepository.findBySanTheThaoMaSan(maSan)
                .stream()
                .map(datSanMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // Danh sách theo chi nhánh (API-27)
    public List<CheckInDTO> getByMaChiNhanh(Integer maChiNhanh) {
        return datSanRepository.findBySanTheThaoMaChiNhanh(maChiNhanh)
                .stream()
                .map(datSanMapper::toSummaryResponse)
                .toList();
    }


    // ============================================================
    // CREATE — đặt sân (API-20)
    // ============================================================
    @Transactional
    public CheckInRequest createBooking(Integer maKH, DatSanRequest request) {
        LocalDate bookingDate = request.getBookingDate();
        LocalDate today = LocalDate.now();

        // TC14: bookingDate không được trong quá khứ theo hiện tại
        if (bookingDate.isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày đặt không được là ngày trong quá khứ.");
        }

        // TC16/TC17: không vượt quá 30 ngày kể từ hôm nay (đúng 30 ngày vẫn cho)
        // không được đặt quá xa trong tương lai.
        if (bookingDate.isAfter(today.plusDays(30))) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày đặt không được vượt quá 30 ngày kể từ hôm nay.");
        }

        // Load TimeSlot trước để kiểm tra TC15
            TimeSlot timeSlot = timeSlotRepository.findById(request.getMaGio())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khung giờ với MaGio: " + request.getMaGio()));

        // TC15: đặt hôm nay → giờ hiện tại phải trước gioBatDau
        if (bookingDate.isEqual(today) && !LocalTime.now().isBefore(timeSlot.getGioBatDau())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Đặt sân trong ngày hôm nay phải trước giờ bắt đầu của khung giờ ("
                            + timeSlot.getGioBatDau() + ").");

        }

        // Load SanTheThao
        Court sanTheThao = sanTheThaoRepository.findById(request.getMaSan())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy sân với MaSan: " + request.getMaSan()));

        // TC19: sân phải đang Active
        // TODO: bổ sung khi SanTheThao entity có field trangThai
        // if (!"ACTIVE".equals(sanTheThao.getTrangThai())) {
        //  throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Sân không còn hoạt động.");
        // }

        // TC11/TC12: trùng slot (maSan + maGio + bookingDate) với PENDING/CONFIRMED
        boolean slotConflict = datSanRepository
                .existsBySanTheThaoMaSanAndTimeSlotMaGioAndBookingDateAndStatusIn(
                        request.getMaSan(), request.getMaGio(), bookingDate, ACTIVE_STATUSES);
        if (slotConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Khung giờ này đã được đặt. Vui lòng chọn khung giờ khác.");
        }

        // Load KhachHang
        Payment khachHang = khachHangRepository.findById(maKH)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khách hàng với MaKH: " + maKH));

        // TC18: cùng khách hàng, cùng maGio + bookingDate (dù sân khác nhau)
        // (nếu trùng sân đã bị chặn ở TC11/TC12 → đây chỉ bắt trùng sân khác)
        // 1 h chỉ cho chơi 1 sân thui
        boolean customerSlotConflict = datSanRepository
                .existsByKhachHangMaKHAndTimeSlotMaGioAndBookingDateAndStatusIn(
                        maKH, request.getMaGio(), bookingDate, ACTIVE_STATUSES);
        if (customerSlotConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bạn đã có lịch đặt sân vào cùng khung giờ và ngày này.");
        }

        // TC20: totalPrice = giaTien từ timeSlot tại thời điểm đặt (mapper set luôn)
        BookingStatus booking = datSanMapper.toEntity(request, sanTheThao, timeSlot, khachHang);
        booking = datSanRepository.save(booking);

        // Tạo ThongTinDatCoc nếu request có deposit - có thể gọi riêng  DatCocService bước này
//        if (request.getDeposit() != null) {
//            ThongTinDatCoc datCoc = datCocMapper.toEntity(request.getDeposit());
//            datCoc.setThongTinDatSan(booking);
//            datCoc = datCocRepository.save(datCoc);
//            booking.setThongTinDatCoc(datCoc);
//        }

        return datSanMapper.toDetailResponse(booking);
    }


    // ============================================================
    // UPDATE — chuyển trạng thái
    // ============================================================

    // PENDING → CONFIRMED (Owner/Staff xác nhận — TC43)
    @Transactional
    public BookingCreateRequest confirmBooking(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        validateTransition(booking,
                BookingStatus.BookingStatus.PENDING,
                BookingStatus.BookingStatus.CONFIRMED);
        booking.setStatus(BookingStatus.BookingStatus.CONFIRMED);
        datSanRepository.save(booking);
        return datSanMapper.toStatusResponse(booking, "Booking đã được xác nhận.");
    }

    // PENDING → CANCELLED (Owner/Staff từ chối — TC44)
    @Transactional
    public BookingCreateRequest rejectBooking(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        validateTransition(booking,
                BookingStatus.BookingStatus.PENDING,
                BookingStatus.BookingStatus.CANCELLED);
        booking.setStatus(BookingStatus.BookingStatus.CANCELLED);
        datSanRepository.save(booking);
        return datSanMapper.toStatusResponse(booking, "Booking đã bị từ chối.");
    }

    // CONFIRMED → CANCELLED (Khách hàng hủy)
    @Transactional
    public BookingCreateRequest cancelByCustomer(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        validateTransition(booking,
                BookingStatus.BookingStatus.CONFIRMED,
                BookingStatus.BookingStatus.CANCELLED);
        booking.setStatus(BookingStatus.BookingStatus.CANCELLED);
        datSanRepository.save(booking);

        // TC36/TC37/TC38: nếu cọc đã PAID → cần hoàn tiền -
//        checkAndFlagRefund(booking);

        return datSanMapper.toStatusResponse(booking, "Booking đã bị hủy bởi khách hàng.");
    }

    // CONFIRMED → COMPLETED (Hệ thống sau check-in)
    @Transactional
    public BookingCreateRequest completeBooking(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        validateTransition(booking,
                BookingStatus.BookingStatus.CONFIRMED,
                BookingStatus.BookingStatus.COMPLETED);
        booking.setStatus(BookingStatus.BookingStatus.COMPLETED);
        datSanRepository.save(booking);
        return datSanMapper.toStatusResponse(booking, "Booking đã hoàn thành.");
    }

    // ============================================================
    // DELETE — soft delete (không xóa vật lý, chuyển về CANCELLED)
    // ============================================================
    @Transactional
    public BookingCreateRequest deleteBooking(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        BookingStatus.BookingStatus current = booking.getStatus();

        // Trạng thái cuối không được xóa
        if (current == BookingStatus.BookingStatus.CANCELLED
                || current == BookingStatus.BookingStatus.COMPLETED) {
             throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Không thể hủy booking ở trạng thái: " + current);
        }

        booking.setStatus(BookingStatus.BookingStatus.CANCELLED);
        datSanRepository.save(booking);

        // TC36/TC37/TC38: nếu cọc đã PAID → cần hoàn tiền
//        checkAndFlagRefund(booking);

        return datSanMapper.toStatusResponse(booking, "Booking đã bị hủy.");
    }

    // DELETE — hard delete vật lý
    @Transactional
    public void hardDeleteBooking(Integer maDatSan) {
        BookingStatus booking = findOrThrow(maDatSan);
        // Nếu có cọc → xóa cọc trước để tránh FK constraint
        if (booking.getThongTinDatCoc() != null) {
            datCocRepository.delete(booking.getThongTinDatCoc());
        }
        datSanRepository.delete(booking);
    }

    // ============================================================
    // Helpers
    // ============================================================
    private BookingStatus findOrThrow(Integer maDatSan) {
        return datSanRepository.findById(maDatSan)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy booking với MaDatSan: " + maDatSan));
    }

    /**
     * Kiểm tra luồng chuyển trạng thái hợp lệ.
     * Quy tắc chung:
     *  - Không cho quay về PENDING
     *  - COMPLETED / CANCELLED là trạng thái cuối, không đổi được
     *  - Phải đúng trạng thái hiện tại mới được chuyển
     */
    private void validateTransition(BookingStatus booking,
                                    BookingStatus.BookingStatus requiredCurrent,
                                    BookingStatus.BookingStatus target) {
        BookingStatus.BookingStatus current = booking.getStatus();

        if (target == BookingStatus.BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Không được chuyển trạng thái về PENDING.");
        }
        if (current == BookingStatus.BookingStatus.COMPLETED
                || current == BookingStatus.BookingStatus.CANCELLED) {
             throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Booking ở trạng thái " + current + " không thể thay đổi.");
        }
        if (current != requiredCurrent) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Không thể chuyển từ " + current + " sang " + target
                            + ". Trạng thái hiện tại phải là " + requiredCurrent + ".");
        }
    }

//    /**
//     * TC36/TC37/TC38: nếu booking bị hủy mà đã có cọc PAID → flag cần hoàn tiền.
//     * TODO: thay bằng event/notification khi có refund flow thực sự.
//     */
//    private void checkAndFlagRefund(ThongTinDatSan booking) {
//        ThongTinDatCoc datCoc = booking.getThongTinDatCoc();
//        if (datCoc != null
//                && ThongTinDatCoc.PaymentStatus.PAID.equals(datCoc.getTinhTrangThanhToan())) {
//            // TODO: gửi event hoàn tiền hoặc cập nhật flag refund pending
//        }
//    }

    @Transactional(readOnly = true)
    public WeekCalendarDTO getWeeklyCalendar(Integer maSan, Integer week) {
        if (week == null || week < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "week phải >= 1.");
        }

        LocalDate today = LocalDate.now();
        LocalDate from = today.plusDays((long) (week - 1) * 7);
        LocalDate to = from.plusDays(6);
        LocalDate maxDate = today.plusDays(30);

        // Validate không vượt 30 ngày kể từ hôm nay
        if (from.isAfter(maxDate) || to.isAfter(maxDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tuần yêu cầu vượt quá giới hạn 30 ngày kể từ hôm nay."
            );
        }

        // Optional nhưng nên có: maSan tồn tại
        if (!sanTheThaoRepository.existsById(maSan)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sân với MaSan: " + maSan);
        }

        // Bước 1: 1 query [tìm sân khoảng tg 1 tuần]
        List<BookingStatus> bookings =
                datSanRepository.findBySanTheThaoMaSanAndBookingDateBetween(maSan, from, to);

        // Bước 2: map in-memory O(n)
        Map<LocalDate, Map<Integer, String>> bookedMap = new HashMap<>();
        for (BookingStatus booking : bookings) {
            LocalDate date = booking.getBookingDate();
            Integer maGio = booking.getTimeSlot().getMaGio();
            String status = booking.getStatus().name();

            bookedMap
                    .computeIfAbsent(date, d -> new HashMap<>())
                    .put(maGio, status);
        }

        // Build response: loop 7 ngày
        List<DayCalendarDTO> days = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            LocalDate date = from.plusDays(i);
            Map<Integer, String> slots = bookedMap.getOrDefault(date, Collections.emptyMap());

            List<SlotStatusDTO> bookedSlots = slots.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> new SlotStatusDTO(e.getKey(), e.getValue()))
                    .toList();

            days.add(new DayCalendarDTO(date, bookedSlots));
        }

        return new WeekCalendarDTO(week, from, to, days);
    }




}

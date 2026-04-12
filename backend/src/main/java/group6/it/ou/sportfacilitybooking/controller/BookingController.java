package group6.it.ou.sportfacilitybooking.controller;


import group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO.WeekCalendarDTO;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatSanRequest;
import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dat-san")
public class BookingController {
    @Autowired

    private BookingService datSanService;

    // API-20: POST /api/dat-san
    // Tạm truyền maKH qua query param vì chưa có JWT
    @PostMapping
    public CheckInRequest createBooking(
            @RequestParam Integer maKH,
            @RequestBody DatSanRequest request) {
        return datSanService.createBooking(maKH, request);
    }

    // API-27: GET /api/dat-san/ca-nhan?maKH=..
    // Tạm truyền maKH qua query param vì chưa có JWT
    @GetMapping("/ca-nhan")
    public List<CheckInDTO> getMyBookings(@RequestParam Integer maKH) {
        return datSanService.getByMaKH(maKH);
    }

    // API-22: GET /api/dat-san/chi-tiet/{id}
    @GetMapping("/chi-tiet/{id}")
    public CheckInRequest getBookingDetail(@PathVariable("id") Integer maDatSan) {
        return datSanService.getByMaDatSan(maDatSan);
    }

    // API-23: PATCH /api/dat-san/{id}/huy
    @PatchMapping("/{id}/huy")
    public BookingCreateRequest cancelBooking(@PathVariable("id") Integer maDatSan) {
        return datSanService.cancelByCustomer(maDatSan);
    }

    // API-24: PATCH /api/dat-san/{id}/xac-nhan
    @PatchMapping("/{id}/xac-nhan")
    public BookingCreateRequest confirmBooking(@PathVariable("id") Integer maDatSan) {
        return datSanService.confirmBooking(maDatSan);
    }

    // API-25: PATCH /api/dat-san/{id}/tu-choi
    @PatchMapping("/{id}/tu-choi")
    public BookingCreateRequest rejectBooking(@PathVariable("id") Integer maDatSan) {
        return datSanService.rejectBooking(maDatSan);
    }

    // API-26: POST /api/dat-san/{id}/checkin
    @PatchMapping("/{id}/checkin")
    public BookingCreateRequest checkinBooking(@PathVariable("id") Integer maDatSan) {
        return datSanService.completeBooking(maDatSan);
    }

    // DELETE /api/dat-san/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteBooking(@PathVariable("id") Integer maDatSan) {
        datSanService.hardDeleteBooking(maDatSan);
    }

    // API-2x: GET /api/dat-san/chi-nhanh/{facilityId}/danh-sach-dat-san
    @GetMapping("/chi-nhanh/{facilityId}/danh-sach-dat-san")
    public List<CheckInDTO> getBookingsByFacility(@PathVariable Integer facilityId) {
        return datSanService.getByMaChiNhanh(facilityId);
    }

    @GetMapping("/lich")
    public WeekCalendarDTO getWeeklyCalendar(
            @RequestParam Integer maSan,
            @RequestParam(defaultValue = "1") Integer week) {
        return datSanService.getWeeklyCalendar(maSan, week);
    }




}

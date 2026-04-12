package group6.it.ou.sportfacilitybooking.controller;

import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocDTO;
import group6.it.ou.sportfacilitybooking.dto.DatSan.DatCocRequest;
import group6.it.ou.sportfacilitybooking.service.DatCocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dat-coc")
public class DatCocController {

    @Autowired
    private DatCocService datCocService;

    // Lấy cọc theo booking
    @GetMapping("/dat-san/{bookingId}/danh-sach-dat-coc")
    public DatCocDTO getDepositByBooking(@PathVariable("bookingId") Integer maDatSan) {
        return datCocService.getByMaDatSan(maDatSan);
    }

    // Tạo/retry cọc cho booking
    @PostMapping("/dat-san/{bookingId}/tao-dat-coc")
    public DatCocDTO createDeposit(
            @PathVariable("bookingId") Integer maDatSan,
            @RequestBody DatCocRequest request) {
        return datCocService.createDatCoc(maDatSan, request);
    }

    // Đánh dấu thanh toán thành công
    @PatchMapping("/{depositId}/thanh-toan-thanh-cong")
    public DatCocDTO markDepositPaid(@PathVariable("depositId") Integer maDatCoc) {
        return datCocService.markAsPaid(maDatCoc);
    }

    // Đánh dấu thanh toán thất bại
    @PatchMapping("/{depositId}/thanh-toan-that-bai")
    public DatCocDTO markDepositFailed(@PathVariable("depositId") Integer maDatCoc) {
        return datCocService.markAsFailed(maDatCoc);
    }

    // DELETE /api/dat-coc/{depositId}
    @DeleteMapping("/{depositId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteDatCoc(@PathVariable("depositId") Integer maDatCoc) {
        datCocService.hardDeleteDatCoc(maDatCoc);
    }
}

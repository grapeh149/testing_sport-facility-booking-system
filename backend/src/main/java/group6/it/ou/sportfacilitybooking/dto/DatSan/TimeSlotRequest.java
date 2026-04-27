package group6.it.ou.sportfacilitybooking.dto.DatSan;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Dùng cho:
 *   POST /api/courts/{courtId}/time-slots  (API-17)
 *   PUT  /api/time-slots/{id}              (API-18)
 *
 * KHÔNG có maGio vì:
 *   - POST: DB tự sinh
 *   - PUT : maGio lấy từ path variable
 */
public class TimeSlotRequest {

    // * Bắt buộc
    private LocalTime gioBatDau;

    // * Bắt buộc
    private LocalTime gioKetThuc;

    // * Bắt buộc
    private BigDecimal giaTien;

    // ---- NoArgs ----
    public TimeSlotRequest() {}

    // ---- AllArgs ----
    public TimeSlotRequest(LocalTime gioBatDau, LocalTime gioKetThuc, BigDecimal giaTien) {
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.giaTien = giaTien;
    }

    // ---- Getters ----
    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getGiaTien() { return giaTien; }

    // ---- Setters ----
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void setGiaTien(BigDecimal giaTien) { this.giaTien = giaTien; }
}
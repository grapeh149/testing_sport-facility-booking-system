package group6.it.ou.sportfacilitybooking.dto.DatSan;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Dùng cho:
 *   GET /api/courts/{courtId}/time-slots  (API-16)
 *
 * Trả về đủ thông tin để frontend hiển thị lịch khung giờ.
 */
public class TimeSlotDTO {

    private Integer maGio;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private BigDecimal giaTien;

    // ---- NoArgs ----
    public TimeSlotDTO() {}

    // ---- AllArgs ----
    public TimeSlotDTO(Integer maGio, LocalTime gioBatDau,
                            LocalTime gioKetThuc, BigDecimal giaTien) {
        this.maGio = maGio;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.giaTien = giaTien;
    }

    // ---- Getters ----
    public Integer getMaGio() { return maGio; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getGiaTien() { return giaTien; }

    // ---- Setters ----
    public void setMaGio(Integer maGio) { this.maGio = maGio; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void setGiaTien(BigDecimal giaTien) { this.giaTien = giaTien; }
}

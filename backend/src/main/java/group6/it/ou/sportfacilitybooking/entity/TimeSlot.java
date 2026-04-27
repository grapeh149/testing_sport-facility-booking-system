package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Time_Slot")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGio")
    private Integer maGio;

    @Column(name = "GioBatDau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "GioKetThuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "GiaTien", precision = 10, scale = 2)
    private BigDecimal giaTien;

    @OneToMany(mappedBy = "timeSlot", fetch = FetchType.LAZY)
    private List<ThongTinDatSan> danhSachDatSan;

    // ==========================================
    // Constructor không tham số (NoArgsConstructor)
    // ==========================================
    public TimeSlot() {
    }

    // ==========================================
    // Constructor tất cả tham số (AllArgsConstructor)
    // ==========================================
    public TimeSlot(Integer maGio, LocalTime gioBatDau, LocalTime gioKetThuc,
                    BigDecimal giaTien, List<ThongTinDatSan> danhSachDatSan) {
        this.maGio = maGio;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.giaTien = giaTien;
        this.danhSachDatSan = danhSachDatSan;
    }

    // ==========================================
    // Getters
    // ==========================================
    public Integer getMaGio() {
        return maGio;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public BigDecimal getGiaTien() {
        return giaTien;
    }

    public List<ThongTinDatSan> getDanhSachDatSan() {
        return danhSachDatSan;
    }

    // ==========================================
    // Setters
    // ==========================================
    public void setMaGio(Integer maGio) {
        this.maGio = maGio;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public void setGiaTien(BigDecimal giaTien) {
        this.giaTien = giaTien;
    }

    public void setDanhSachDatSan(List<ThongTinDatSan> danhSachDatSan) {
        this.danhSachDatSan = danhSachDatSan;
    }

    // ==========================================
    // Builder
    // ==========================================
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer maGio;
        private LocalTime gioBatDau;
        private LocalTime gioKetThuc;
        private BigDecimal giaTien;
        private List<ThongTinDatSan> danhSachDatSan;

        public Builder maGio(Integer maGio) {
            this.maGio = maGio;
            return this;
        }

        public Builder gioBatDau(LocalTime gioBatDau) {
            this.gioBatDau = gioBatDau;
            return this;
        }

        public Builder gioKetThuc(LocalTime gioKetThuc) {
            this.gioKetThuc = gioKetThuc;
            return this;
        }

        public Builder giaTien(BigDecimal giaTien) {
            this.giaTien = giaTien;
            return this;
        }

        public Builder danhSachDatSan(List<ThongTinDatSan> danhSachDatSan) {
            this.danhSachDatSan = danhSachDatSan;
            return this;
        }

        public TimeSlot build() {
            return new TimeSlot(maGio, gioBatDau, gioKetThuc, giaTien, danhSachDatSan);
        }
    }

    // ==========================================
    // toString
    // ==========================================
    @Override
    public String toString() {
        return "TimeSlot{" +
                "maGio=" + maGio +
                ", gioBatDau=" + gioBatDau +
                ", gioKetThuc=" + gioKetThuc +
                ", giaTien=" + giaTien +
                '}';
    }
}

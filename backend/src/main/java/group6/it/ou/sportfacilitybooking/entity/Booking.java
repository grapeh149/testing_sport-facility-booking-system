package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongTinDatCoc")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDatCoc")
    private Integer maDatCoc;

    @Column(name = "TienCoc", precision = 10, scale = 2, nullable = false)
    private BigDecimal tienCoc;

    @Column(name = "PhuongThucThanhToan", length = 50)
    private String phuongThucThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(name = "TinhTrangThanhToan", length = 50, nullable = false)
    private PaymentStatus tinhTrangThanhToan;

    @Column(name = "NgayThanhToan")
    private LocalDateTime ngayThanhToan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatSan", referencedColumnName = "MaDatSan", nullable = false)
    private BookingStatus thongTinDatSan;

    // ==========================================
    // Enum PaymentStatus
    // ==========================================
    public enum PaymentStatus {
        PENDING,
        PAID,
        REFUNDED,
        FAILED
    }

    // ==========================================
    // Constructor không tham số (NoArgsConstructor)
    // ==========================================
    public Booking() {
    }

    // ==========================================
    // Constructor tất cả tham số (AllArgsConstructor)
    // ==========================================
    public Booking(Integer maDatCoc, BigDecimal tienCoc, String phuongThucThanhToan,
                          PaymentStatus tinhTrangThanhToan, LocalDateTime ngayThanhToan,
                          BookingStatus thongTinDatSan) {
        this.maDatCoc = maDatCoc;
        this.tienCoc = tienCoc;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.tinhTrangThanhToan = tinhTrangThanhToan;
        this.ngayThanhToan = ngayThanhToan;
        this.thongTinDatSan = thongTinDatSan;
    }

    // ==========================================
    // Getters
    // ==========================================
    public Integer getMaDatCoc() {
        return maDatCoc;
    }

    public BigDecimal getTienCoc() {
        return tienCoc;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public PaymentStatus getTinhTrangThanhToan() {
        return tinhTrangThanhToan;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public BookingStatus getThongTinDatSan() {
        return thongTinDatSan;
    }

    // ==========================================
    // Setters
    // ==========================================
    public void setMaDatCoc(Integer maDatCoc) {
        this.maDatCoc = maDatCoc;
    }

    public void setTienCoc(BigDecimal tienCoc) {
        this.tienCoc = tienCoc;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public void setTinhTrangThanhToan(PaymentStatus tinhTrangThanhToan) {
        this.tinhTrangThanhToan = tinhTrangThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public void setThongTinDatSan(BookingStatus thongTinDatSan) {
        this.thongTinDatSan = thongTinDatSan;
    }

    // ==========================================
    // Builder
    // ==========================================
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer maDatCoc;
        private BigDecimal tienCoc;
        private String phuongThucThanhToan;
        private PaymentStatus tinhTrangThanhToan;
        private LocalDateTime ngayThanhToan;
        private BookingStatus thongTinDatSan;

        public Builder maDatCoc(Integer maDatCoc) {
            this.maDatCoc = maDatCoc;
            return this;
        }

        public Builder tienCoc(BigDecimal tienCoc) {
            this.tienCoc = tienCoc;
            return this;
        }

        public Builder phuongThucThanhToan(String phuongThucThanhToan) {
            this.phuongThucThanhToan = phuongThucThanhToan;
            return this;
        }

        public Builder tinhTrangThanhToan(PaymentStatus tinhTrangThanhToan) {
            this.tinhTrangThanhToan = tinhTrangThanhToan;
            return this;
        }

        public Builder ngayThanhToan(LocalDateTime ngayThanhToan) {
            this.ngayThanhToan = ngayThanhToan;
            return this;
        }

        public Builder thongTinDatSan(BookingStatus thongTinDatSan) {
            this.thongTinDatSan = thongTinDatSan;
            return this;
        }

        public Booking build() {
            return new Booking(maDatCoc, tienCoc, phuongThucThanhToan,
                    tinhTrangThanhToan, ngayThanhToan, thongTinDatSan);
        }
    }

    // ==========================================
    // toString
    // ==========================================
    @Override
    public String toString() {
        return "ThongTinDatCoc{" +
                "maDatCoc=" + maDatCoc +
                ", tienCoc=" + tienCoc +
                ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
                ", tinhTrangThanhToan=" + tinhTrangThanhToan +
                ", ngayThanhToan=" + ngayThanhToan +
                '}';
    }
}
package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "ThongTinDatSan",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_san_gio_ngay",
                        columnNames = {"MaSan","MaGio", "BookingDate"}
                )
        }
)
public class BookingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDatSan")
    private Integer maDatSan;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20, nullable = false)
    private BookingStatus status;

    @Column(name = "TotalPrice", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKH", referencedColumnName = "MaKH", nullable = false)
    private Payment khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSan", referencedColumnName = "MaSan", nullable = false)
    private Court sanTheThao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGio", referencedColumnName = "MaGio", nullable = false)
    private TimeSlot timeSlot;

    @OneToOne(mappedBy = "thongTinDatSan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Booking thongTinDatCoc;

    // ==========================================
    // Enum BookingStatus
    // ==========================================
    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }

    // ==========================================
    // Constructor không tham số (NoArgsConstructor)
    // ==========================================
    public BookingStatus() {
    }

    // ==========================================
    // Constructor tất cả tham số (AllArgsConstructor)
    // ==========================================
    public BookingStatus(Integer maDatSan, BookingStatus status, BigDecimal totalPrice,
                          LocalDate bookingDate, Payment khachHang,
                          Court sanTheThao, TimeSlot timeSlot,
                          Booking thongTinDatCoc) {
        this.maDatSan = maDatSan;
        this.status = status;
        this.totalPrice = totalPrice;
        this.bookingDate = bookingDate;
        this.khachHang = khachHang;
        this.sanTheThao = sanTheThao;
        this.timeSlot = timeSlot;
        this.thongTinDatCoc = thongTinDatCoc;
    }

    // ==========================================
    // Getters
    // ==========================================
    public Integer getMaDatSan() {
        return maDatSan;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public Payment getKhachHang() {
        return khachHang;
    }

    public Court getSanTheThao() {
        return sanTheThao;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Booking getThongTinDatCoc() {
        return thongTinDatCoc;
    }

    // ==========================================
    // Setters
    // ==========================================
    public void setMaDatSan(Integer maDatSan) {
        this.maDatSan = maDatSan;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setKhachHang(Payment khachHang) {
        this.khachHang = khachHang;
    }

    public void setSanTheThao(Court sanTheThao) {
        this.sanTheThao = sanTheThao;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setThongTinDatCoc(Booking thongTinDatCoc) {
        this.thongTinDatCoc = thongTinDatCoc;
    }

    // ==========================================
    // Builder
    // ==========================================
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer maDatSan;
        private BookingStatus status;
        private BigDecimal totalPrice;
        private LocalDate bookingDate;
        private Payment khachHang;
        private Court sanTheThao;
        private TimeSlot timeSlot;
        private Booking thongTinDatCoc;

        public Builder maDatSan(Integer maDatSan) {
            this.maDatSan = maDatSan;
            return this;
        }

        public Builder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public Builder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder bookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }

        public Builder khachHang(Payment khachHang) {
            this.khachHang = khachHang;
            return this;
        }

        public Builder sanTheThao(Court sanTheThao) {
            this.sanTheThao = sanTheThao;
            return this;
        }

        public Builder timeSlot(TimeSlot timeSlot) {
            this.timeSlot = timeSlot;
            return this;
        }

        public Builder thongTinDatCoc(Booking thongTinDatCoc) {
            this.thongTinDatCoc = thongTinDatCoc;
            return this;
        }

        public BookingStatus build() {
            return new BookingStatus(maDatSan, status, totalPrice, bookingDate,
                    khachHang, sanTheThao, timeSlot, thongTinDatCoc);
        }
    }

    // ==========================================
    // toString
    // ==========================================
    @Override
    public String toString() {
        return "ThongTinDatSan{" +
                "maDatSan=" + maDatSan +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
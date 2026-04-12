package group6.it.ou.sportfacilitybooking.dto;

import  java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private Long id;
    private String bookingCode;
    private Long customerId;
    private String customerName;
    private Long courtId;
    private String courtName;
    private String facilityName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String status;
    private String note;
    private String vnpayUrl;
    private Boolean hasReview;

    public BookingDTO() {}

    public BookingDTO(Long id, String bookingCode, String status) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getCourtId() { return courtId; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getVnpayUrl() { return vnpayUrl; }
    public void setVnpayUrl(String vnpayUrl) { this.vnpayUrl = vnpayUrl; }

    public Boolean getHasReview() { return hasReview != null ? hasReview : false; }
    public void setHasReview(Boolean hasReview) { this.hasReview = hasReview; }
}

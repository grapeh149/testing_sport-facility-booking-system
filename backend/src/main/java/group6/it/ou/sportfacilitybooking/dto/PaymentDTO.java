package group6.it.ou.sportfacilitybooking.dto;

import java.math.BigDecimal;

public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private String vnpayTxnRef;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private String bankCode;

    public PaymentDTO() {}

    public PaymentDTO(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getVnpayTxnRef() { return vnpayTxnRef; }
    public void setVnpayTxnRef(String vnpayTxnRef) { this.vnpayTxnRef = vnpayTxnRef; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
}

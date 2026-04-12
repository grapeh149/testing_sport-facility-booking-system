package group6.it.ou.sportfacilitybooking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private String vnpayTxnRef;
    private String vnpayTxnNo;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private String paymentMethod;
    private String bankCode;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private String description;
    private String transactionRef;
}

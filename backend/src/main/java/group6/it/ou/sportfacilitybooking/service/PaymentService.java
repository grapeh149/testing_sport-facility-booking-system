package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.dto.PaymentIpnRequest;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.entity.PaymentStatus;
import group6.it.ou.sportfacilitybooking.entity.PaymentType;
import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.NotificationType;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.PaymentMapper;
import group6.it.ou.sportfacilitybooking.repository.PaymentRepository;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@Transactional
public class PaymentService {

    private static final DateTimeFormatter VNP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${vnpay.tmn-code:}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret:}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url:http://localhost:3000/payment/vnpay-return}")
    private String defaultReturnUrl;

    @Value("${vnpay.version:2.1.0}")
    private String vnpVersion;

    @Value("${vnpay.command:pay}")
    private String vnpCommand;

    @Value("${vnpay.curr-code:VND}")
    private String vnpCurrCode;

    @Value("${vnpay.locale:vn}")
    private String vnpLocale;

    @Value("${vnpay.order-type:other}")
    private String vnpOrderType;

    public String createVNPayPaymentUrl(Long bookingId, String returnUrl, String ipAddress, String bankCode, String vnpayTxnRef) {
        validateVNPayConfig();

        // Try eager load first, fallback to regular findById
        Booking booking;
        try {
            booking = bookingRepository.findByIdWithTimeSlot(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
        } catch (Exception e) {
            // Fallback if eager load fails
            booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Booking is not in pending payment state");
        }

        // Validate basic booking time only if data exists
        // Only validate for today's bookings to prevent users from paying for expired slots
        LocalDate today = LocalDate.now();
        if (booking.getBookingDate() != null && booking.getBookingDate().isEqual(today)) {
            LocalTime bookingStartTime = booking.getStartTime();
            if (bookingStartTime == null && booking.getTimeSlot() != null) {
                bookingStartTime = booking.getTimeSlot().getStartTime();
            }

            if (bookingStartTime != null) {
                LocalTime now = LocalTime.now();
                if (bookingStartTime.isBefore(now) || bookingStartTime.equals(now)) {
                    throw new RuntimeException("Cannot create payment for past or ongoing booking");
                }
            }
        }

        String resolvedReturnUrl = (returnUrl == null || returnUrl.isBlank()) ? defaultReturnUrl : returnUrl;

        String resolvedTxnRef = resolveTxnRef(vnpayTxnRef, bookingId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setVnpayTxnRef(resolvedTxnRef);
        payment.setAmount(booking.getDepositAmount());
        payment.setPaymentType(PaymentType.DEPOSIT);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime expireAt = now.plusMinutes(15);

        TreeMap<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnpVersion);
        params.put("vnp_Command", vnpCommand);
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", toVNPayAmount(payment.getAmount()));
        params.put("vnp_CurrCode", vnpCurrCode);
        params.put("vnp_TxnRef", resolvedTxnRef);
        params.put("vnp_OrderInfo", "Thanh toan dat coc don " + booking.getBookingCode());
        params.put("vnp_OrderType", vnpOrderType);
        params.put("vnp_Locale", vnpLocale);
        params.put("vnp_ReturnUrl", resolvedReturnUrl);
        params.put("vnp_IpAddr", normalizeIpAddress(ipAddress));
        params.put("vnp_CreateDate", now.format(VNP_DATE_FORMAT));
        params.put("vnp_ExpireDate", expireAt.format(VNP_DATE_FORMAT));

        if (bankCode != null && !bankCode.isBlank()) {
            params.put("vnp_BankCode", bankCode.trim());
        }

        String queryData = buildQueryData(params);
        String secureHash = hmacSHA512(vnpHashSecret, queryData);
        return vnpPayUrl + "?" + queryData + "&vnp_SecureHash=" + secureHash;
    }

    public void handleVNPayIPN(PaymentIpnRequest request) {
        Map<String, String> input = new HashMap<>();
        input.put("vnp_Amount", request.getVnp_Amount());
        input.put("vnp_BankCode", request.getVnp_BankCode());
        input.put("vnp_BankTranNo", request.getVnp_BankTranNo());
        input.put("vnp_CardType", request.getVnp_CardType());
        input.put("vnp_OrderInfo", request.getVnp_OrderInfo());
        input.put("vnp_PayDate", request.getVnp_PayDate());
        input.put("vnp_ResponseCode", request.getVnp_ResponseCode());
        input.put("vnp_TmnCode", request.getVnp_TmnCode());
        input.put("vnp_TransactionNo", request.getVnp_TransactionNo());
        input.put("vnp_TransactionStatus", request.getVnp_TransactionStatus());
        input.put("vnp_TxnRef", request.getVnp_TxnRef());
        input.put("vnp_SecureHash", request.getVnp_SecureHash());
        processVNPayCallback(input, false);
    }

    public PaymentDTO handleVNPayReturn(Map<String, String> requestParams) {
        Payment payment = processVNPayCallback(requestParams, true);
        return paymentMapper.toDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void processRefund(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Can only refund successful payments");
        }

        Payment refund = new Payment();
        refund.setBooking(payment.getBooking());
        refund.setVnpayTxnRef(resolveTxnRef("REFUND" + paymentId, payment.getBooking().getId()));
        refund.setAmount(payment.getAmount());
        refund.setPaymentType(PaymentType.REFUND);
        refund.setStatus(PaymentStatus.SUCCESS);
        refund.setPaidAt(LocalDateTime.now());
        refund.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(refund);

        payment.setStatus(PaymentStatus.REFUNDED);

        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        createNotification(
                booking.getCustomer(),
                NotificationType.REFUND_PROCESSED,
                "Hoàn tiền thành công",
                "Đơn " + booking.getBookingCode() + " đã được hoàn tiền " + payment.getAmount() + " VND.",
                booking.getId(),
                "BOOKING"
        );
    }

    private String resolveTxnRef(String inputTxnRef, Long bookingId) {
        if (inputTxnRef != null && !inputTxnRef.isBlank()) {
            String normalized = inputTxnRef.trim();
            if (normalized.length() > 50) {
                throw new RuntimeException("vnpayTxnRef must be <= 50 characters");
            }
            if (paymentRepository.existsByVnpayTxnRef(normalized)) {
                throw new RuntimeException("vnpayTxnRef already exists");
            }
            return normalized;
        }

        String candidate;
        do {
            candidate = "BK" + bookingId + "_" + System.currentTimeMillis();
        } while (paymentRepository.existsByVnpayTxnRef(candidate));
        return candidate;
    }

    private Payment processVNPayCallback(Map<String, String> requestParams, boolean verifySecureHash) {
        validateVNPayConfig();

        String txnRef = requestParams.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            throw new RuntimeException("Missing vnp_TxnRef");
        }

        if (verifySecureHash) {
            verifyVNPaySignature(requestParams);
        }

        Payment payment = paymentRepository.findByVnpayTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String responseCode = requestParams.get("vnp_ResponseCode");
        String transactionStatus = requestParams.get("vnp_TransactionStatus");
        boolean success = "00".equals(responseCode) && (transactionStatus == null || transactionStatus.isBlank() || "00".equals(transactionStatus));

        if (success) {
            boolean wasSuccess = payment.getStatus() == PaymentStatus.SUCCESS;
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setVnpayTxnNo(requestParams.get("vnp_TransactionNo"));
            payment.setBankCode(requestParams.get("vnp_BankCode"));
            payment.setPaidAt(LocalDateTime.now());

            Booking booking = payment.getBooking();
            if (booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
                booking.setStatus(BookingStatus.PENDING_CONFIRM);
                booking.setUpdatedAt(LocalDateTime.now());
                bookingRepository.save(booking);
            }

            if (!wasSuccess) {
                createNotification(
                        booking.getCustomer(),
                        NotificationType.BOOKING_PAYMENT_SUCCESS,
                        "Thanh toán thành công",
                        "Thanh toán cọc cho đơn " + booking.getBookingCode() + " đã thành công.",
                        booking.getId(),
                        "BOOKING"
                );

                // Send notification to owner for approval
                User owner = booking.getCourt().getFacility().getOwner();
                createNotification(
                        owner,
                        NotificationType.BOOKING_CREATED,
                        "Đơn đặt sân cần duyệt",
                        "Khách hàng " + booking.getCustomer().getFullName() + " vừa thanh toán cọc cho đơn " + booking.getBookingCode() + ". Vui lòng duyệt.",
                        booking.getId(),
                        "BOOKING"
                );
            }
        } else if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.FAILED);

            Booking booking = payment.getBooking();
            createNotification(
                    booking.getCustomer(),
                    NotificationType.BOOKING_PAYMENT_FAILED,
                    "Thanh toán thất bại",
                    "Thanh toán cọc cho đơn " + booking.getBookingCode() + " không thành công. Vui lòng thử lại.",
                    booking.getId(),
                    "BOOKING"
            );
        }

        return paymentRepository.save(payment);
    }

    private void verifyVNPaySignature(Map<String, String> requestParams) {
        String secureHash = requestParams.get("vnp_SecureHash");
        if (secureHash == null || secureHash.isBlank()) {
            throw new RuntimeException("Missing vnp_SecureHash");
        }

        TreeMap<String, String> filtered = new TreeMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || value.isBlank()) {
                continue;
            }
            if (!key.startsWith("vnp_")) {
                continue;
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }
            filtered.put(key, value);
        }

        String hashData = buildQueryData(filtered);
        String calculatedHash = hmacSHA512(vnpHashSecret, hashData);
        if (!calculatedHash.equalsIgnoreCase(secureHash)) {
            throw new RuntimeException("Invalid VNPay secure hash");
        }
    }

    private String buildQueryData(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate VNPay secure hash", e);
        }
    }

    private String toVNPayAmount(BigDecimal amount) {
        if (amount == null) {
            throw new RuntimeException("Payment amount is required");
        }
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String normalizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank() || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            return "127.0.0.1";
        }
        return ipAddress;
    }

    private void validateVNPayConfig() {
        if (vnpTmnCode == null || vnpTmnCode.isBlank()) {
            throw new RuntimeException("VNPay config missing: vnpay.tmn-code");
        }
        if (vnpHashSecret == null || vnpHashSecret.isBlank()) {
            throw new RuntimeException("VNPay config missing: vnpay.hash-secret");
        }
    }

    private void createNotification(User user, NotificationType type, String title, String message, Long refId, String refType) {
        if (user == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRefId(refId);
        notification.setRefType(refType);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}

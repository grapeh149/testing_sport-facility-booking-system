package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.dto.PaymentIpnRequest;
import group6.it.ou.sportfacilitybooking.entity.*;
import group6.it.ou.sportfacilitybooking.mapper.PaymentMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.PaymentRepository;
import group6.it.ou.sportfacilitybooking.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // ARRANGE
        setValidVNPayConfig();
        lenient().when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validateVNPayConfig_missingTmnCode_throwsException(String tmnCode) {
        // ARRANGE
        ReflectionTestUtils.setField(paymentService, "vnpTmnCode", tmnCode);

        // ACT + ASSERT
        assertThrowsMessage("VNPay config missing: vnpay.tmn-code",
                () -> invoke("validateVNPayConfig"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validateVNPayConfig_missingHashSecret_throwsException(String hashSecret) {
        // ARRANGE
        ReflectionTestUtils.setField(paymentService, "vnpHashSecret", hashSecret);

        // ACT + ASSERT
        assertThrowsMessage("VNPay config missing: vnpay.hash-secret",
                () -> invoke("validateVNPayConfig"));
    }

    @Test
    void validateVNPayConfig_validConfig_doesNotThrow() {
        // ACT + ASSERT
        assertDoesNotThrow(() -> invoke("validateVNPayConfig"));
    }

    @ParameterizedTest
    @MethodSource("normalizeIpCases")
    void normalizeIpAddress_inputs_returnExpected(String ipAddress, String expected) {
        // ACT
        String result = invoke("normalizeIpAddress", ipAddress);

        // ASSERT
        assertEquals(expected, result);
    }

    static Stream<Arguments> normalizeIpCases() {
        return Stream.of(
                Arguments.of(null, "127.0.0.1"),
                Arguments.of(" ", "127.0.0.1"),
                Arguments.of("0:0:0:0:0:0:0:1", "127.0.0.1"),
                Arguments.of("192.168.1.100", "192.168.1.100"));
    }

    @Test
    void toVNPayAmount_nullAmount_throwsException() {
        // ACT + ASSERT
        assertThrowsMessage("Payment amount is required",
                () -> invoke("toVNPayAmount", (Object) null));
    }

    @ParameterizedTest
    @CsvSource({
            "100000, 10000000",
            "100000.5, 10000050"
    })
    void toVNPayAmount_validAmount_returnVNPayAmount(String amount, String expected) {
        // ACT
        String result = invoke("toVNPayAmount", new BigDecimal(amount));

        // ASSERT
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void resolveTxnRef_missingInput_autoGenerateTxnRef(String inputTxnRef) {
        // ARRANGE
        when(paymentRepository.existsByVnpayTxnRef(startsWith("BK1_"))).thenReturn(false);

        // ACT
        String result = invoke("resolveTxnRef", inputTxnRef, 1L);

        // ASSERT
        assertTrue(result.startsWith("BK1_"));
    }

    @Test
    void resolveTxnRef_inputTooLong_throwsException() {
        // ARRANGE
        String inputTxnRef = "A".repeat(51);

        // ACT + ASSERT
        assertThrowsMessage("vnpayTxnRef must be <= 50 characters",
                () -> invoke("resolveTxnRef", inputTxnRef, 1L));
    }

    @Test
    void resolveTxnRef_existingInput_throwsException() {
        // ARRANGE
        when(paymentRepository.existsByVnpayTxnRef("VALID")).thenReturn(true);

        // ACT + ASSERT
        assertThrowsMessage("vnpayTxnRef already exists",
                () -> invoke("resolveTxnRef", "VALID", 1L));
    }

    @Test
    void resolveTxnRef_validInput_returnNormalizedTxnRef() {
        // ARRANGE
        when(paymentRepository.existsByVnpayTxnRef("VALID")).thenReturn(false);

        // ACT
        String result = invoke("resolveTxnRef", " VALID ", 1L);

        // ASSERT
        assertEquals("VALID", result);
    }

    @Test
    void resolveTxnRef_generatedDuplicate_generateAgain() {
        // ARRANGE
        when(paymentRepository.existsByVnpayTxnRef(startsWith("BK1_"))).thenReturn(true, false);

        // ACT
        String result = invoke("resolveTxnRef", null, 1L);

        // ASSERT
        assertTrue(result.startsWith("BK1_"));
        verify(paymentRepository, times(2)).existsByVnpayTxnRef(startsWith("BK1_"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void verifyVNPaySignature_missingSecureHash_throwsException(String secureHash) {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        params.put("vnp_SecureHash", secureHash);

        // ACT + ASSERT
        assertThrowsMessage("Missing vnp_SecureHash",
                () -> invoke("verifyVNPaySignature", params));
    }

    @Test
    void verifyVNPaySignature_skipInvalidAndExcludedEntries_doesNotThrow() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        params.put(null, "ignored");
        params.put("vnp_Empty", "");
        params.put("custom_field", "ignored");
        params.put("vnp_SecureHashType", "HmacSHA512");

        // ACT + ASSERT
        assertDoesNotThrow(() -> invoke("verifyVNPaySignature", params));
    }

    @Test
    void verifyVNPaySignature_invalidHash_throwsException() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        params.put("vnp_SecureHash", "wrong-hash");

        // ACT + ASSERT
        assertThrowsMessage("Invalid VNPay secure hash",
                () -> invoke("verifyVNPaySignature", params));
    }

    @Test
    void verifyVNPaySignature_validHash_doesNotThrow() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");

        // ACT + ASSERT
        assertDoesNotThrow(() -> invoke("verifyVNPaySignature", params));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void processVNPayCallback_missingTxnRef_throwsException(String txnRef) {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        params.put("vnp_TxnRef", txnRef);

        // ACT + ASSERT
        assertThrowsMessage("Missing vnp_TxnRef",
                () -> invoke("processVNPayCallback", params, false));
    }

    @Test
    void processVNPayCallback_paymentNotFound_throwsException() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrowsMessage("Payment not found",
                () -> invoke("processVNPayCallback", params, false));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "00" })
    void processVNPayCallback_successResponse_updatePaymentSuccess(String transactionStatus) {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", transactionStatus);
        Payment payment = payment(PaymentStatus.PENDING, BookingStatus.PENDING_PAYMENT);
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));

        // ACT
        Payment result = invoke("processVNPayCallback", params, true);

        // ASSERT
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(BookingStatus.PENDING_CONFIRM, result.getBooking().getStatus());
        verify(bookingRepository).save(result.getBooking());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void processVNPayCallback_failedResponse_pendingPayment_updateFailed() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "01", null);
        Payment payment = payment(PaymentStatus.PENDING, BookingStatus.PENDING_PAYMENT);
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));

        // ACT
        Payment result = invoke("processVNPayCallback", params, false);

        // ASSERT
        assertEquals(PaymentStatus.FAILED, result.getStatus());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void processVNPayCallback_responseOkButTransactionStatusFailed_updateFailed() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "99");
        Payment payment = payment(PaymentStatus.PENDING, BookingStatus.PENDING_PAYMENT);
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));

        // ACT
        Payment result = invoke("processVNPayCallback", params, false);

        // ASSERT
        assertEquals(PaymentStatus.FAILED, result.getStatus());
    }

    @Test
    void processVNPayCallback_alreadySuccess_doesNotCreateNotificationAgain() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        Payment payment = payment(PaymentStatus.SUCCESS, BookingStatus.PENDING_CONFIRM);
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));

        // ACT
        Payment result = invoke("processVNPayCallback", params, false);

        // ASSERT
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createVNPayPaymentUrl_validBooking_returnUrlContainsVNPayParams() {
        // ARRANGE
        Booking booking = booking(BookingStatus.PENDING_PAYMENT, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByVnpayTxnRef("CUSTOM_REF")).thenReturn(false);

        // ACT
        String result = paymentService.createVNPayPaymentUrl(1L, "http://custom.com", "0:0:0:0:0:0:0:1", "NCB",
                "CUSTOM_REF");

        // ASSERT
        assertTrue(result.startsWith("https://pay.test?"));
        assertTrue(result.contains("vnp_TxnRef=CUSTOM_REF"));
        assertTrue(result.contains("vnp_BankCode=NCB"));
        assertTrue(result.contains("vnp_IpAddr=127.0.0.1"));
        assertTrue(result.contains("vnp_SecureHash="));
    }

    @Test
    void createVNPayPaymentUrl_bookingNotFound_throwsException() {
        // ARRANGE
        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrowsMessage("Booking not found",
                () -> paymentService.createVNPayPaymentUrl(1L, null, null, null, null));
    }

    @Test
    void createVNPayPaymentUrl_bookingNotPendingPayment_throwsException() {
        // ARRANGE
        Booking booking = booking(BookingStatus.CONFIRMED, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.of(booking));

        // ACT + ASSERT
        assertThrowsMessage("Booking is not in pending payment state",
                () -> paymentService.createVNPayPaymentUrl(1L, null, null, null, null));
    }

    @Test
    void createVNPayPaymentUrl_todayPastTimeSlot_throwsException() {
        // ARRANGE
        Booking booking = booking(BookingStatus.PENDING_PAYMENT, LocalDate.now(), null);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalTime.now().minusHours(1));
        booking.setTimeSlot(timeSlot);
        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.of(booking));

        // ACT + ASSERT
        assertThrowsMessage("Cannot create payment for past or ongoing booking",
                () -> paymentService.createVNPayPaymentUrl(1L, null, null, null, null));
    }

    @Test
    void handleVNPayIPN_validRequest_updatePaymentSuccessWithoutHashVerify() {
        // ARRANGE
        Payment payment = payment(PaymentStatus.PENDING, BookingStatus.PENDING_PAYMENT);
        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));

        PaymentIpnRequest request = new PaymentIpnRequest();
        request.setVnp_TxnRef("TXN1");
        request.setVnp_ResponseCode("00");
        request.setVnp_TransactionStatus("00");
        request.setVnp_SecureHash("wrong-hash");

        // ACT
        paymentService.handleVNPayIPN(request);

        // ASSERT
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void handleVNPayReturn_validHash_returnPaymentDTO() {
        // ARRANGE
        Map<String, String> params = signedParams("TXN1", "00", "00");
        Payment payment = payment(PaymentStatus.PENDING, BookingStatus.PENDING_PAYMENT);
        PaymentDTO dto = new PaymentDTO(1L, "SUCCESS");

        when(paymentRepository.findByVnpayTxnRef("TXN1")).thenReturn(Optional.of(payment));
        when(paymentMapper.toDTO(payment)).thenReturn(dto);

        // ACT
        PaymentDTO result = paymentService.handleVNPayReturn(params);

        // ASSERT
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void getPaymentsByBooking_repositoryReturnsPayments_returnPaymentDTOs() {
        // ARRANGE
        Payment payment1 = payment(PaymentStatus.SUCCESS, BookingStatus.PENDING_CONFIRM);
        Payment payment2 = payment(PaymentStatus.FAILED, BookingStatus.PENDING_PAYMENT);
        when(paymentRepository.findByBookingId(1L)).thenReturn(List.of(payment1, payment2));
        when(paymentMapper.toDTO(any(Payment.class))).thenReturn(new PaymentDTO());

        // ACT
        List<PaymentDTO> result = paymentService.getPaymentsByBooking(1L);

        // ASSERT
        assertEquals(2, result.size());
    }

    @Test
    void getPaymentsByBooking_emptyRepository_returnEmptyList() {
        // ARRANGE
        when(paymentRepository.findByBookingId(99L)).thenReturn(List.of());

        // ACT
        List<PaymentDTO> result = paymentService.getPaymentsByBooking(99L);

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @Test
    void processRefund_paymentNotFound_throwsException() {
        // ARRANGE
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrowsMessage("Payment not found", () -> paymentService.processRefund(1L));
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = { "PENDING", "FAILED" })
    void processRefund_notSuccessfulPayment_throwsException(PaymentStatus status) {
        // ARRANGE
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment(status, BookingStatus.PENDING_PAYMENT)));

        // ACT + ASSERT
        assertThrowsMessage("Can only refund successful payments", () -> paymentService.processRefund(1L));
    }

    @Test
    void processRefund_successfulPayment_createRefundAndNotification() {
        // ARRANGE
        Payment payment = payment(PaymentStatus.SUCCESS, BookingStatus.PENDING_CONFIRM);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.existsByVnpayTxnRef("REFUND1")).thenReturn(false);

        // ACT
        paymentService.processRefund(1L);

        // ASSERT
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_nullUser_doesNotSaveNotification() {
        // ACT
        invoke("createNotification", null, NotificationType.BOOKING_PAYMENT_SUCCESS,
                "title", "message", 1L, "BOOKING");

        // ASSERT
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    private void setValidVNPayConfig() {
        ReflectionTestUtils.setField(paymentService, "vnpTmnCode", "TMN");
        ReflectionTestUtils.setField(paymentService, "vnpHashSecret", "SECRET");
        ReflectionTestUtils.setField(paymentService, "vnpPayUrl", "https://pay.test");
        ReflectionTestUtils.setField(paymentService, "defaultReturnUrl", "http://localhost:3000/payment/vnpay-return");
        ReflectionTestUtils.setField(paymentService, "vnpVersion", "2.1.0");
        ReflectionTestUtils.setField(paymentService, "vnpCommand", "pay");
        ReflectionTestUtils.setField(paymentService, "vnpCurrCode", "VND");
        ReflectionTestUtils.setField(paymentService, "vnpLocale", "vn");
        ReflectionTestUtils.setField(paymentService, "vnpOrderType", "other");
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(String methodName, Object... args) {
        return ReflectionTestUtils.invokeMethod(paymentService, methodName, args);
    }

    private void assertThrowsMessage(String expectedMessage, Executable executable) {
        RuntimeException exception = assertThrows(RuntimeException.class, executable);
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private Booking booking(BookingStatus status, LocalDate bookingDate, LocalTime startTime) {
        User customer = new User();
        customer.setFullName("Customer");

        User owner = new User();
        owner.setFullName("Owner");

        Facility facility = new Facility();
        facility.setOwner(owner);

        Court court = new Court();
        court.setFacility(facility);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingCode("BK001");
        booking.setCustomer(customer);
        booking.setCourt(court);
        booking.setStatus(status);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setDepositAmount(BigDecimal.valueOf(100000));
        return booking;
    }

    private Payment payment(PaymentStatus paymentStatus, BookingStatus bookingStatus) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking(bookingStatus, LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        payment.setVnpayTxnRef("TXN1");
        payment.setAmount(BigDecimal.valueOf(100000));
        payment.setPaymentType(PaymentType.DEPOSIT);
        payment.setStatus(paymentStatus);
        return payment;
    }

    private Map<String, String> signedParams(String txnRef, String responseCode, String transactionStatus) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "10000000");
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_ResponseCode", responseCode);
        params.put("vnp_TmnCode", "TMN");
        params.put("vnp_TransactionNo", "VN123");
        if (transactionStatus != null) {
            params.put("vnp_TransactionStatus", transactionStatus);
        }
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_SecureHash", vnpHash(params));
        return params;
    }

    private String vnpHash(Map<String, String> params) {
        TreeMap<String, String> filtered = new TreeMap<>();
        params.forEach((key, value) -> {
            if (key != null && value != null && !value.isBlank()
                    && key.startsWith("vnp_")
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)) {
                filtered.put(key, value);
            }
        });

        String data = filtered.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");

        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec("SECRET".getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

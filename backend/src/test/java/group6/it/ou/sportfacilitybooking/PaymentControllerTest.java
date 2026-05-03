package group6.it.ou.sportfacilitybooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.config.JwtTokenProvider;
import group6.it.ou.sportfacilitybooking.controller.PaymentController;
import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.dto.PaymentIpnRequest;
import group6.it.ou.sportfacilitybooking.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    private static final String FAKE_TOKEN = "fake-jwt-token";
    private static final String AUTH_HEADER = "Bearer " + FAKE_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUpJwt() {
        when(jwtTokenProvider.validateToken(FAKE_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(FAKE_TOKEN)).thenReturn("test@example.com");
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(1L);
    }

    // ─── createVNPayPaymentUrl() ──────────────────────────────────────────────

    // TC-CVP-01: Happy path — đủ params (có bankCode + vnpayTxnRef) → success=true
    @Test
    void createVNPayPaymentUrl_fullParams_returnSuccess() throws Exception {
        // ARRANGE
        String url = "https://pay.vnpay.vn/?vnp_TxnRef=CUSTOM_REF";
        when(paymentService.createVNPayPaymentUrl(eq(1L), eq("http://return.com"), anyString(), eq("NCB"), eq("CUSTOM_REF")))
                .thenReturn(url);

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{bookingId}/vnpay", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("returnUrl", "http://return.com")
                        .param("bankCode", "NCB")
                        .param("vnpayTxnRef", "CUSTOM_REF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tạo url thanh toán thành công"))
                .andExpect(jsonPath("$.data").value(url));

        verify(paymentService).createVNPayPaymentUrl(eq(1L), eq("http://return.com"), anyString(), eq("NCB"), eq("CUSTOM_REF"));
    }

    // TC-CVP-02: Happy path — bankCode + vnpayTxnRef vắng mặt (optional) → success=true
    @Test
    void createVNPayPaymentUrl_optionalParamsAbsent_returnSuccess() throws Exception {
        // ARRANGE
        String url = "https://pay.vnpay.vn/?vnp_TxnRef=BK1_AUTO";
        when(paymentService.createVNPayPaymentUrl(eq(1L), eq("http://return.com"), anyString(), isNull(), isNull()))
                .thenReturn(url);

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{bookingId}/vnpay", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("returnUrl", "http://return.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(url));

        verify(paymentService).createVNPayPaymentUrl(eq(1L), eq("http://return.com"), anyString(), isNull(), isNull());
    }

    // TC-CVP-03 + TC-CVP-04: Service throw → success=false (gom @ParameterizedTest)
    @ParameterizedTest
    @ValueSource(strings = {
            "Booking not found",
            "Booking is not in pending payment state"
    })
    void createVNPayPaymentUrl_serviceThrows_returnFailure(String message) throws Exception {
        // ARRANGE
        when(paymentService.createVNPayPaymentUrl(anyLong(), anyString(), anyString(), any(), any()))
                .thenThrow(new RuntimeException(message));

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{bookingId}/vnpay", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("returnUrl", "http://return.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(message));

        verify(paymentService).createVNPayPaymentUrl(anyLong(), anyString(), anyString(), any(), any());
    }

    // TC-CVP-05: Thiếu @RequestParam returnUrl (required) → Spring chặn HTTP 400
    @Test
    void createVNPayPaymentUrl_missingReturnUrl_returnBadRequest() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{bookingId}/vnpay", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        MissingServletRequestParameterException.class,
                        result.getResolvedException()
                ));

        verifyNoInteractions(paymentService);
    }

    // ─── handleVNPayReturn() ──────────────────────────────────────────────────

    // TC-HVR-01: Happy path → success=true, data=PaymentDTO
    @Test
    void handleVNPayReturn_validParams_returnSuccess() throws Exception {
        // ARRANGE
        PaymentDTO dto = paymentDto(10L, "SUCCESS");
        when(paymentService.handleVNPayReturn(anyMap())).thenReturn(dto);

        // ACT + ASSERT
        mockMvc.perform(get("/api/payments/vnpay/return")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("vnp_TxnRef", "TXN1")
                        .param("vnp_ResponseCode", "00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Xử lý kết quả thanh toán thành công"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        verify(paymentService).handleVNPayReturn(anyMap());
    }

    // TC-HVR-02 + TC-HVR-03: Service throw → success=false (gom @ParameterizedTest)
    @ParameterizedTest
    @ValueSource(strings = {
            "Invalid VNPay secure hash",
            "Payment not found"
    })
    void handleVNPayReturn_serviceThrows_returnFailure(String message) throws Exception {
        // ARRANGE
        when(paymentService.handleVNPayReturn(anyMap()))
                .thenThrow(new RuntimeException(message));

        // ACT + ASSERT
        mockMvc.perform(get("/api/payments/vnpay/return")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("vnp_TxnRef", "TXN1")
                        .param("vnp_ResponseCode", "01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(message));

        verify(paymentService).handleVNPayReturn(anyMap());
    }

    // ─── handleVNPayIPN() ─────────────────────────────────────────────────────

    // TC-IPN-01: Happy path → success=true, message="Xử lý IPN thành công"
    @Test
    void handleVNPayIPN_validRequest_returnSuccess() throws Exception {
        // ARRANGE
        doNothing().when(paymentService).handleVNPayIPN(any(PaymentIpnRequest.class));

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/vnpay/ipn")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ipnRequest("TXN1", "00", "00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Xử lý IPN thành công"));

        verify(paymentService).handleVNPayIPN(any(PaymentIpnRequest.class));
    }

    // TC-IPN-02: Service throw → success=false
    @Test
    void handleVNPayIPN_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        doThrow(new RuntimeException("Payment not found"))
                .when(paymentService).handleVNPayIPN(any(PaymentIpnRequest.class));

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/vnpay/ipn")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ipnRequest("TXN1", "01", "99"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Payment not found"));

        verify(paymentService).handleVNPayIPN(any(PaymentIpnRequest.class));
    }

    // ─── getPaymentsByBooking() ───────────────────────────────────────────────

    // TC-GPB-01: Có payments → success=true, data=[list]
    @Test
    void getPaymentsByBooking_hasPayments_returnSuccess() throws Exception {
        // ARRANGE
        List<PaymentDTO> payments = List.of(paymentDto(1L, "SUCCESS"), paymentDto(2L, "FAILED"));
        when(paymentService.getPaymentsByBooking(5L)).thenReturn(payments);

        // ACT + ASSERT
        mockMvc.perform(get("/api/payments/booking/{bookingId}", 5L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách thanh toán thành công"))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(paymentService).getPaymentsByBooking(5L);
    }

    // TC-GPB-02: Danh sách rỗng → success=true, data=[]
    @Test
    void getPaymentsByBooking_emptyList_returnSuccessWithEmptyData() throws Exception {
        // ARRANGE
        when(paymentService.getPaymentsByBooking(99L)).thenReturn(List.of());

        // ACT + ASSERT
        mockMvc.perform(get("/api/payments/booking/{bookingId}", 99L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(paymentService).getPaymentsByBooking(99L);
    }

    // TC-GPB-03: Service throw → success=false
    @Test
    void getPaymentsByBooking_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(paymentService.getPaymentsByBooking(5L))
                .thenThrow(new RuntimeException("Booking not found"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/payments/booking/{bookingId}", 5L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Booking not found"));

        verify(paymentService).getPaymentsByBooking(5L);
    }

    // ─── processRefund() ──────────────────────────────────────────────────────

    // TC-PR-01: Happy path → success=true, message="Hoàn tiền thành công"
    @Test
    void processRefund_validPayment_returnSuccess() throws Exception {
        // ARRANGE
        doNothing().when(paymentService).processRefund(1L);

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{paymentId}/refund", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hoàn tiền thành công"));

        verify(paymentService).processRefund(1L);
    }

    // TC-PR-02 + TC-PR-03: Service throw → success=false (gom @ParameterizedTest)
    @ParameterizedTest
    @ValueSource(strings = {
            "Payment not found",
            "Can only refund successful payments"
    })
    void processRefund_serviceThrows_returnFailure(String message) throws Exception {
        // ARRANGE
        doThrow(new RuntimeException(message)).when(paymentService).processRefund(1L);

        // ACT + ASSERT
        mockMvc.perform(post("/api/payments/{paymentId}/refund", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(message));

        verify(paymentService).processRefund(1L);
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private PaymentDTO paymentDto(Long id, String status) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(id);
        dto.setStatus(status);
        return dto;
    }

    private PaymentIpnRequest ipnRequest(String txnRef, String responseCode, String transactionStatus) {
        PaymentIpnRequest req = new PaymentIpnRequest();
        req.setVnp_TxnRef(txnRef);
        req.setVnp_ResponseCode(responseCode);
        req.setVnp_TransactionStatus(transactionStatus);
        return req;
    }
}

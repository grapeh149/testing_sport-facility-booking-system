package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.dto.PaymentIpnRequest;
import group6.it.ou.sportfacilitybooking.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController Unit Tests")
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).setValidator(new org.springframework.validation.Validator() { public boolean supports(Class<?> c) { return true; } public void validate(Object o, org.springframework.validation.Errors e) {} }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver()).build();
        paymentDTO = new PaymentDTO();
        paymentDTO.setId(1L);
    }

    @Test
    @DisplayName("Should create VNPay payment url")
    void testCreateVNPayPaymentUrl() throws Exception {
        when(paymentService.createVNPayPaymentUrl(eq(1L), anyString(), anyString(), any(), any())).thenReturn("http://vnpay.url");

        mockMvc.perform(post("/api/payments/1/vnpay")
                .param("returnUrl", "http://return.url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when create VNPay payment url")
    void testCreateVNPayPaymentUrl_Exception() throws Exception {
        when(paymentService.createVNPayPaymentUrl(eq(1L), anyString(), anyString(), any(), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/payments/1/vnpay")
                .param("returnUrl", "http://return.url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle VNPay return")
    void testHandleVNPayReturn() throws Exception {
        when(paymentService.handleVNPayReturn(any())).thenReturn(paymentDTO);

        mockMvc.perform(get("/api/payments/vnpay/return")
                .param("vnp_ResponseCode", "00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when VNPay return")
    void testHandleVNPayReturn_Exception() throws Exception {
        when(paymentService.handleVNPayReturn(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/payments/vnpay/return")
                .param("vnp_ResponseCode", "00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle VNPay IPN")
    void testHandleVNPayIPN() throws Exception {
        PaymentIpnRequest request = new PaymentIpnRequest();
        doNothing().when(paymentService).handleVNPayIPN(any());

        mockMvc.perform(post("/api/payments/vnpay/ipn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle exception when VNPay IPN")
    void testHandleVNPayIPN_Exception() throws Exception {
        PaymentIpnRequest request = new PaymentIpnRequest();
        doThrow(new RuntimeException("Error")).when(paymentService).handleVNPayIPN(any());

        mockMvc.perform(post("/api/payments/vnpay/ipn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get payments by booking")
    void testGetPaymentsByBooking() throws Exception {
        when(paymentService.getPaymentsByBooking(1L)).thenReturn(List.of(paymentDTO));

        mockMvc.perform(get("/api/payments/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get payments by booking")
    void testGetPaymentsByBooking_Exception() throws Exception {
        when(paymentService.getPaymentsByBooking(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/payments/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should process refund")
    void testProcessRefund() throws Exception {
        doNothing().when(paymentService).processRefund(1L);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when process refund")
    void testProcessRefund_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(paymentService).processRefund(1L);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}

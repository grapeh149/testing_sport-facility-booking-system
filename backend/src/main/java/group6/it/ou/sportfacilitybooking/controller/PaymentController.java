package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.PaymentDTO;
import group6.it.ou.sportfacilitybooking.dto.PaymentIpnRequest;
import group6.it.ou.sportfacilitybooking.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/{bookingId}/vnpay")
    public ApiResponse<String> createVNPayPaymentUrl(@PathVariable Long bookingId,
                                                     @RequestParam String returnUrl,
                                                     @RequestParam(required = false) String bankCode,
                                                     HttpServletRequest request,
                                                     @RequestParam(required = false) String vnpayTxnRef) {
        try {
            String result = paymentService.createVNPayPaymentUrl(bookingId, returnUrl, request.getRemoteAddr(), bankCode, vnpayTxnRef);
            return new ApiResponse<>(true, result, "Tạo url thanh toán thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/vnpay/return")
    public ApiResponse<PaymentDTO> handleVNPayReturn(@RequestParam Map<String, String> queryParams) {
        try {
            PaymentDTO result = paymentService.handleVNPayReturn(queryParams);
            return new ApiResponse<>(true, result, "Xử lý kết quả thanh toán thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/vnpay/ipn")
    public ApiResponse<?> handleVNPayIPN(@RequestBody PaymentIpnRequest request) {
        try {
            paymentService.handleVNPayIPN(request);
            return new ApiResponse<>(true, null, "Xử lý IPN thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<List<PaymentDTO>> getPaymentsByBooking(@PathVariable Long bookingId) {
        try {
            List<PaymentDTO> result = paymentService.getPaymentsByBooking(bookingId);
            return new ApiResponse<>(true, result, "Lấy danh sách thanh toán thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/{paymentId}/refund")
    public ApiResponse<?> processRefund(@PathVariable Long paymentId) {
        try {
            paymentService.processRefund(paymentId);
            return new ApiResponse<>(true, null, "Hoàn tiền thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}

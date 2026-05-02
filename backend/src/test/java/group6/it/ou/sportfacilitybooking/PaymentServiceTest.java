package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.entity.Booking;
import group6.it.ou.sportfacilitybooking.entity.BookingStatus;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.mapper.PaymentMapper;
import group6.it.ou.sportfacilitybooking.repository.BookingRepository;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.repository.PaymentRepository;
import group6.it.ou.sportfacilitybooking.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void setupConfig() {
        ReflectionTestUtils.setField(paymentService, "vnpTmnCode", "TESTTMNCODE");
        ReflectionTestUtils.setField(paymentService, "vnpHashSecret", "TESTSECRET");
        ReflectionTestUtils.setField(paymentService, "vnpPayUrl", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        ReflectionTestUtils.setField(paymentService, "defaultReturnUrl", "http://localhost:3000/payment/vnpay-return");
        ReflectionTestUtils.setField(paymentService, "vnpVersion", "2.1.0");
        ReflectionTestUtils.setField(paymentService, "vnpCommand", "pay");
        ReflectionTestUtils.setField(paymentService, "vnpCurrCode", "VND");
        ReflectionTestUtils.setField(paymentService, "vnpLocale", "vn");
        ReflectionTestUtils.setField(paymentService, "vnpOrderType", "other");
    }

    @Test
    void createVNPayPaymentUrl_shouldReturnUrlContainingSecureHash() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingCode("BOOKING123");
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setDepositAmount(BigDecimal.valueOf(100000));
        booking.setBookingDate(LocalDate.now().plusDays(1));

        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByVnpayTxnRef(anyString())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String url = paymentService.createVNPayPaymentUrl(1L, null, "0:0:0:0:0:0:0:1", "VCB", "REF123");

        assertNotNull(url);
        assertTrue(url.contains("vnp_TmnCode=TESTTMNCODE"));
        assertTrue(url.contains("vnp_SecureHash="));
        assertTrue(url.contains("vnp_BankCode=VCB"));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createVNPayPaymentUrl_shouldThrowWhenBookingNotPendingPayment() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING_CONFIRM);

        when(bookingRepository.findByIdWithTimeSlot(1L)).thenReturn(Optional.of(booking));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> paymentService.createVNPayPaymentUrl(1L, null, "127.0.0.1", null, null));

        assertEquals("Booking is not in pending payment state", exception.getMessage());
    }
}

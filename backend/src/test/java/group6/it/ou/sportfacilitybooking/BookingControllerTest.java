package group6.it.ou.sportfacilitybooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.config.JwtTokenProvider;
import group6.it.ou.sportfacilitybooking.controller.BookingController;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String FAKE_TOKEN = "fake-jwt-token";
    private static final String AUTH_HEADER = "Bearer " + FAKE_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Trước mỗi test: mock JWT hợp lệ để request vượt qua filter chain.
     * Các test cần userId cụ thể sẽ override lại getUserIdFromToken() trong method đó.
     */
    @BeforeEach
    void setUpJwt() {
        when(jwtTokenProvider.validateToken(FAKE_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(FAKE_TOKEN)).thenReturn("test@example.com");
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(1L);
    }

    // ─── 1. getAllBookings() — GET /api/bookings ──────────────────────────────

    // TC-GAB-01: response static, không gọi service
    @Test
    void getAllBookings_returnStaticMessage() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Use /api/bookings/my-bookings")))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verifyNoInteractions(bookingService);
    }

    // ─── 2. createBooking() — POST /api/bookings ─────────────────────────────

    // TC-CB-01: happy path, userId=5L từ JWT attribute
    @Test
    void createBooking_validRequest_returnSuccess() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(5L);
        BookingDTO dto = bookingDTO(10L);
        when(bookingService.createBooking(any(BookingCreateRequest.class), eq(5L))).thenReturn(dto);

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courtId\":1,\"timeSlotId\":2,\"bookingDate\":\"2026-05-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đặt sân thành công"))
                .andExpect(jsonPath("$.data.id").value(10));

        verify(bookingService).createBooking(
                argThat(r -> r.getCourtId().equals(1L) && r.getTimeSlotId().equals(2L)),
                eq(5L)
        );
    }

    // TC-CB-02: service throw → message chứa exception message
    @Test
    void createBooking_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(5L);
        when(bookingService.createBooking(any(), eq(5L)))
                .thenThrow(new RuntimeException("Sân đã được đặt"));

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courtId\":1,\"timeSlotId\":2,\"bookingDate\":\"2026-05-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(containsString("Sân đã được đặt")));

        verify(bookingService).createBooking(any(), eq(5L));
    }

    // TC-CB-03 + TC-CB-04 + TC-CB-05: gom @ParameterizedTest — mỗi case null 1 field bắt buộc
    @ParameterizedTest
    @MethodSource("invalidBookingRequestBodies")
    void createBooking_requiredFieldNull_returnBadRequest(String body) throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        MethodArgumentNotValidException.class,
                        result.getResolvedException()
                ));

        verifyNoInteractions(bookingService);
    }

    // Source cho TC-CB-03/04/05
    static Stream<String> invalidBookingRequestBodies() {
        return Stream.of(
                // TC-CB-03: courtId null
                "{\"courtId\":null,\"timeSlotId\":2,\"bookingDate\":\"2026-05-10\"}",
                // TC-CB-04: timeSlotId null
                "{\"courtId\":1,\"timeSlotId\":null,\"bookingDate\":\"2026-05-10\"}",
                // TC-CB-05: bookingDate null
                "{\"courtId\":1,\"timeSlotId\":2,\"bookingDate\":null}"
        );
    }

    // TC-CB-06: JWT hợp lệ nhưng getUserIdFromToken trả null → service nhận null
    @Test
    void createBooking_userIdNull_serviceCalledWithNull() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(null);
        when(bookingService.createBooking(any(), eq(null))).thenReturn(bookingDTO(10L));

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courtId\":1,\"timeSlotId\":2,\"bookingDate\":\"2026-05-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(bookingService).createBooking(any(), eq(null));
    }

    // ─── 3. getBookingDetails() — GET /api/bookings/code/{bookingCode} ────────

    // TC-GBD-01
    @Test
    void getBookingDetails_existingCode_returnSuccess() throws Exception {
        // ARRANGE
        when(bookingService.getBookingDetails("BK20260501")).thenReturn(bookingDTO(1L));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/code/{bookingCode}", "BK20260501")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy thông tin đặt sân thành công"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(bookingService).getBookingDetails("BK20260501");
    }

    // TC-GBD-02
    @Test
    void getBookingDetails_notFound_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.getBookingDetails("NOTEXIST"))
                .thenThrow(new RuntimeException("Booking not found"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/code/{bookingCode}", "NOTEXIST")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Booking not found"));

        verify(bookingService).getBookingDetails("NOTEXIST");
    }

    // ─── 4. getMyBookings() — GET /api/bookings/my-bookings ──────────────────

    // TC-GMB-01 (page=0, size=10) + TC-GMB-02 (page=2, size=5)
    @ParameterizedTest
    @CsvSource({"0, 10", "2, 5"})
    void getMyBookings_validRequest_returnSuccess(int page, int size) throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(3L);
        Page<BookingDTO> pageResult = new PageImpl<>(
                List.of(bookingDTO(1L), bookingDTO(2L)),
                PageRequest.of(page, size), 2
        );
        when(bookingService.getCustomerBookingHistory(eq(3L),
                argThat(p -> p.getPageNumber() == page && p.getPageSize() == size)))
                .thenReturn(pageResult);

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/my-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.currentPage").value(page))
                .andExpect(jsonPath("$.data.totalPages").exists())
                .andExpect(jsonPath("$.data.totalElements").exists())
                .andExpect(jsonPath("$.data.hasNextPage").exists())
                .andExpect(jsonPath("$.data.hasPreviousPage").exists());

        verify(bookingService).getCustomerBookingHistory(eq(3L),
                argThat(p -> p.getPageNumber() == page && p.getPageSize() == size));
    }

    // TC-GMB-03
    @Test
    void getMyBookings_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(3L);
        when(bookingService.getCustomerBookingHistory(eq(3L), any()))
                .thenThrow(new RuntimeException("DB error"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/my-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("DB error"));

        verify(bookingService).getCustomerBookingHistory(eq(3L), any());
    }

    // TC-GMB-04: không có userId attribute → service nhận null
    @Test
    void getMyBookings_userIdNull_serviceCalledWithNull() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(null);
        when(bookingService.getCustomerBookingHistory(eq(null), any()))
                .thenReturn(new PageImpl<>(List.of()));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/my-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(bookingService).getCustomerBookingHistory(eq(null), any());
    }

    // ─── 5. getOwnerPendingBookings() — GET /api/bookings/owner/pending-bookings

    // TC-OPB-01: happy path, default page/size, verify pageSize=10
    @Test
    void getOwnerPendingBookings_validRequest_returnSuccess() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(7L);
        Page<BookingDTO> page = new PageImpl<>(List.of(bookingDTO(1L)), PageRequest.of(0, 10), 1);
        when(bookingService.getOwnerPendingBookings(eq(7L), any())).thenReturn(page);

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/pending-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.currentPage").exists())
                .andExpect(jsonPath("$.data.totalPages").exists())
                .andExpect(jsonPath("$.data.totalElements").exists())
                .andExpect(jsonPath("$.data.hasNextPage").exists())
                .andExpect(jsonPath("$.data.hasPreviousPage").exists());

        verify(bookingService).getOwnerPendingBookings(eq(7L),
                argThat(p -> p.getPageSize() == 10));
    }

    // TC-OPB-02
    @Test
    void getOwnerPendingBookings_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(7L);
        when(bookingService.getOwnerPendingBookings(eq(7L), any()))
                .thenThrow(new RuntimeException("lỗi DB"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/pending-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(bookingService).getOwnerPendingBookings(eq(7L), any());
    }

    // TC-OPB-03: custom page=1, size=20 → truyền xuống đúng
    @Test
    void getOwnerPendingBookings_customPageSize_passedCorrectly() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(7L);
        when(bookingService.getOwnerPendingBookings(eq(7L), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 20), 0));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/pending-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(bookingService).getOwnerPendingBookings(eq(7L),
                argThat(p -> p.getPageNumber() == 1 && p.getPageSize() == 20));
    }

    // ─── 6. getOwnerAllBookings() — GET /api/bookings/owner/all-bookings ──────

    // TC-OAB-01: không truyền size → default=100 (khác các endpoint khác dùng 10)
    @Test
    void getOwnerAllBookings_defaultParams_defaultSizeIs100() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(7L);
        when(bookingService.getOwnerAllBookings(eq(7L), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 100), 0));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/all-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.currentPage").exists())
                .andExpect(jsonPath("$.data.totalPages").exists())
                .andExpect(jsonPath("$.data.totalElements").exists())
                .andExpect(jsonPath("$.data.hasNextPage").exists())
                .andExpect(jsonPath("$.data.hasPreviousPage").exists());

        // Kiểm tra default size=100, không phải 10
        verify(bookingService).getOwnerAllBookings(eq(7L),
                argThat(p -> p.getPageSize() == 100));
    }

    // TC-OAB-02
    @Test
    void getOwnerAllBookings_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(7L);
        when(bookingService.getOwnerAllBookings(eq(7L), any()))
                .thenThrow(new RuntimeException("lỗi DB"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/all-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(bookingService).getOwnerAllBookings(eq(7L), any());
    }

    // TC-OAB-03: ownerId=9L được extract từ JWT và truyền đúng
    @Test
    void getOwnerAllBookings_ownerIdExtractedCorrectly() throws Exception {
        // ARRANGE
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(9L);
        when(bookingService.getOwnerAllBookings(eq(9L), any()))
                .thenReturn(new PageImpl<>(List.of()));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/owner/all-bookings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(bookingService).getOwnerAllBookings(eq(9L), any());
    }

    // ─── 7. getBookingById() — GET /api/bookings/{id} ────────────────────────

    // TC-GBID-01
    @Test
    void getBookingById_validId_returnSuccess() throws Exception {
        // ARRANGE
        when(bookingService.getBookingById(10L)).thenReturn(bookingDTO(10L));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/{id}", 10L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));

        verify(bookingService).getBookingById(10L);
    }

    // TC-GBID-02
    @Test
    void getBookingById_notFound_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.getBookingById(999L))
                .thenThrow(new RuntimeException("Booking không tồn tại"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Booking không tồn tại"));

        verify(bookingService).getBookingById(999L);
    }

    // ─── 8. getCourtBookings() — GET /api/bookings/court/{courtId} ───────────

    // TC-GCB-01 (có 3 items) + TC-GCB-02 (empty list): gom @ParameterizedTest
    @ParameterizedTest
    @CsvSource({"3", "0"})
    void getCourtBookings_returnList(int itemCount) throws Exception {
        // ARRANGE
        List<BookingDTO> items = itemCount > 0
                ? List.of(bookingDTO(1L), bookingDTO(2L), bookingDTO(3L))
                : List.of();
        when(bookingService.getCourtBookings(5L)).thenReturn(items);

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/court/{courtId}", 5L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(itemCount)));

        verify(bookingService).getCourtBookings(5L);
    }

    // TC-GCB-03
    @Test
    void getCourtBookings_courtNotFound_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.getCourtBookings(999L))
                .thenThrow(new RuntimeException("Sân không tồn tại"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/bookings/court/{courtId}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(bookingService).getCourtBookings(999L);
    }

    // ─── 9. confirmBooking() — POST /api/bookings/{id}/confirm ───────────────
    // Lưu ý: ownerId là @RequestParam, KHÔNG lấy từ JWT attribute

    // TC-CFB-01
    @Test
    void confirmBooking_validOwner_returnSuccess() throws Exception {
        // ARRANGE
        when(bookingService.confirmBooking(1L, 7L)).thenReturn(bookingDTO(1L));

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings/{id}/confirm", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("ownerId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Xác nhận đặt sân thành công"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(bookingService).confirmBooking(1L, 7L);
    }

    // TC-CFB-02
    @Test
    void confirmBooking_unauthorizedOwner_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.confirmBooking(1L, 99L))
                .thenThrow(new RuntimeException("Không có quyền"));

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings/{id}/confirm", 1L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("ownerId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Không có quyền"));

        verify(bookingService).confirmBooking(1L, 99L);
    }

    // ─── 10. cancelBookingPost() — POST /api/bookings/{id}/cancel ────────────

    // TC-CPB-01 (reason có) + TC-CPB-02 (reason null → service nhận "")
    @ParameterizedTest
    @CsvSource({
            "Bận đột xuất, Bận đột xuất",   // TC-CPB-01: reason truyền nguyên
            ",  "                            // TC-CPB-02: không gửi reason → service nhận ""
    })
    void cancelBookingPost_reasonBranch_passedCorrectly(String reasonParam, String expectedReason) throws Exception {
        // ARRANGE
        when(bookingService.cancelBooking(2L, expectedReason.strip())).thenReturn(bookingDTO(2L));

        // ACT
        var request = post("/api/bookings/{id}/cancel", 2L)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER);
        if (reasonParam != null && !reasonParam.isBlank()) {
            request = request.param("reason", reasonParam);
        }

        // ASSERT
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hủy đặt sân thành công"));

        verify(bookingService).cancelBooking(2L, expectedReason.strip());
    }

    // TC-CPB-03
    @Test
    void cancelBookingPost_serviceThrows_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.cancelBooking(2L, "xxx"))
                .thenThrow(new RuntimeException("Không thể hủy"));

        // ACT + ASSERT
        mockMvc.perform(post("/api/bookings/{id}/cancel", 2L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("reason", "xxx"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Không thể hủy"));

        verify(bookingService).cancelBooking(2L, "xxx");
    }

    // ─── 11. cancelBooking() — DELETE /api/bookings/{id} ─────────────────────

    // TC-CAB-01: luôn truyền "" xuống service
    @Test
    void cancelBooking_validId_returnSuccess() throws Exception {
        // ARRANGE
        when(bookingService.cancelBooking(3L, "")).thenReturn(bookingDTO(3L));

        // ACT + ASSERT
        mockMvc.perform(delete("/api/bookings/{id}", 3L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hủy đặt sân thành công"));

        verify(bookingService).cancelBooking(3L, ""); // hardcoded "" trong controller
    }

    // TC-CAB-02
    @Test
    void cancelBooking_notFound_returnFailure() throws Exception {
        // ARRANGE
        when(bookingService.cancelBooking(999L, ""))
                .thenThrow(new RuntimeException("Booking không tồn tại"));

        // ACT + ASSERT
        mockMvc.perform(delete("/api/bookings/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(bookingService).cancelBooking(999L, "");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private BookingDTO bookingDTO(Long id) {
        BookingDTO dto = new BookingDTO();
        dto.setId(id);
        dto.setBookingCode("BK" + id);
        return dto;
    }
}

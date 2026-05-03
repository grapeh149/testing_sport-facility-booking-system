package group6.it.ou.sportfacilitybooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.config.JwtTokenProvider;
import group6.it.ou.sportfacilitybooking.controller.CheckInController;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.service.CheckInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@WebMvcTest(CheckInController.class)
// KHÔNG dùng @AutoConfigureMockMvc(addFilters = false)  do đã mock JWT ở @BeforeEach
class CheckInControllerTest {

    private static final String FAKE_TOKEN = "fake-jwt-token";
    private static final String AUTH_HEADER = "Bearer " + FAKE_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CheckInService checkInService;

    // Mock JwtTokenProvider để filter xác thực thành công với FAKE_TOKEN
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Trước mỗi test: mock JWT hợp lệ để request đi qua filter chain mà không bị 401.
     */
    @BeforeEach
    void setUpJwt() {
        when(jwtTokenProvider.validateToken(FAKE_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(FAKE_TOKEN)).thenReturn("test@example.com");
        when(jwtTokenProvider.getUserIdFromToken(FAKE_TOKEN)).thenReturn(1L);
    }

    // ─── checkIn() ───────────────────────────────────────────────────────────

    // TC-CI-01 (note = "Đúng giờ") + TC-CI-02 (note = null)
    @ParameterizedTest
    @ValueSource(strings = "Đúng giờ")
    @NullSource
    void checkIn_validRequest_returnSuccess(String note) throws Exception {
        // ARRANGE
        CheckInDTO dto = dto(100L, 1L, 5L, note);
        when(checkInService.checkIn(eq(1L), eq(5L), any(CheckInRequest.class)))
                .thenReturn(dto);

        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("bookingId", "1")
                        .param("checkedByUserId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(1L, note))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Check-in thành công"))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.bookingId").value(1))
                .andExpect(jsonPath("$.data.checkedByUserId").value(5));

        verify(checkInService).checkIn(eq(1L), eq(5L), argThat(r ->
                Objects.equals(r.getBookingId(), 1L) && Objects.equals(r.getNote(), note)
        ));
    }

    // TC-CI-03 (Booking không tồn tại) + TC-CI-04 (đã check-in rồi)
    @ParameterizedTest
    @ValueSource(strings = {
            "Booking không tồn tại",
            "Booking này đã được check-in"
    })
    void checkIn_serviceThrows_returnFailure(String message) throws Exception {
        // ARRANGE
        when(checkInService.checkIn(eq(1L), eq(5L), any(CheckInRequest.class)))
                .thenThrow(new RuntimeException(message));

        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("bookingId", "1")
                        .param("checkedByUserId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(1L, "Đúng giờ"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(message));

        verify(checkInService).checkIn(eq(1L), eq(5L), any(CheckInRequest.class));
    }

    // TC-CI-05: bookingId trong body null → @NotNull chặn, service không được gọi
    @Test
    void checkIn_bodyBookingIdNull_returnBadRequestAndNotCallService() throws Exception {
        // ARRANGE
        CheckInRequest request = request(null, "Đúng giờ");

        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("bookingId", "1")
                        .param("checkedByUserId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        MethodArgumentNotValidException.class,
                        result.getResolvedException()
                ));

        verifyNoInteractions(checkInService);
    }

    // TC-CI-06: thiếu @RequestParam bookingId → Spring chặn, service không được gọi
    @Test
    void checkIn_missingBookingIdParam_returnBadRequestAndNotCallService() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("checkedByUserId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(1L, "Đúng giờ"))))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        MissingServletRequestParameterException.class,
                        result.getResolvedException()
                ));

        verifyNoInteractions(checkInService);
    }

    // TC-CI-07: thiếu @RequestParam checkedByUserId → Spring chặn, service không được gọi
    @Test
    void checkIn_missingCheckedByUserIdParam_returnBadRequestAndNotCallService() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("bookingId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(1L, "Đúng giờ"))))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        MissingServletRequestParameterException.class,
                        result.getResolvedException()
                ));

        verifyNoInteractions(checkInService);
    }

    // TC-CI-08: @RequestParam bookingId=10 thắng body bookingId=99 — service phải nhận 10L
    @Test
    void checkIn_paramBookingIdDifferentFromBodyBookingId_useParamBookingId() throws Exception {
        // ARRANGE
        when(checkInService.checkIn(eq(10L), eq(5L), any(CheckInRequest.class)))
                .thenReturn(dto(100L, 10L, 5L, "Đúng giờ"));

        // ACT + ASSERT
        mockMvc.perform(post("/api/checkins")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .param("bookingId", "10")
                        .param("checkedByUserId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(99L, "Đúng giờ"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // service nhận param 10L, không phải body 99L; body vẫn giữ nguyên 99L
        verify(checkInService).checkIn(eq(10L), eq(5L), argThat(r ->
                Objects.equals(r.getBookingId(), 99L)
        ));
    }

    // ─── getCheckInRecord() ──────────────────────────────────────────────────

    // TC-GCR-01: có record → trả về success
    @Test
    void getCheckInRecord_existingRecord_returnSuccess() throws Exception {
        // ARRANGE
        when(checkInService.getCheckInRecord(5L))
                .thenReturn(dto(100L, 5L, 2L, "Đúng giờ"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/checkins/booking/{bookingId}", 5L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy thông tin check-in thành công"))
                .andExpect(jsonPath("$.data.bookingId").value(5));

        verify(checkInService).getCheckInRecord(5L);
    }

    // TC-GCR-02 (chưa check-in) + TC-GCR-03 (booking không tồn tại)
    @ParameterizedTest
    @ValueSource(strings = {
            "Chưa check-in",
            "Booking không tồn tại"
    })
    void getCheckInRecord_serviceThrows_returnFailure(String message) throws Exception {
        // ARRANGE
        when(checkInService.getCheckInRecord(5L))
                .thenThrow(new RuntimeException(message));

        // ACT + ASSERT
        mockMvc.perform(get("/api/checkins/booking/{bookingId}", 5L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value(message));

        verify(checkInService).getCheckInRecord(5L);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private CheckInRequest request(Long bookingId, String note) {
        CheckInRequest r = new CheckInRequest(bookingId);
        r.setNote(note);
        return r;
    }

    private CheckInDTO dto(Long id, Long bookingId, Long checkedByUserId, String note) {
        CheckInDTO dto = new CheckInDTO();
        dto.setId(id);
        dto.setBookingId(bookingId);
        dto.setCheckedByUserId(checkedByUserId);
        dto.setNote(note);
        return dto;
    }
}

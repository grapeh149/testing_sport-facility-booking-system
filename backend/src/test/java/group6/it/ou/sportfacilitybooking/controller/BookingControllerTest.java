package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingController Unit Tests")
class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private BookingDTO bookingDTO;
    private Page<BookingDTO> bookingPage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);
        bookingDTO.setBookingCode("B-123");

        bookingPage = new PageImpl<>(List.of(bookingDTO));
    }

    @Test
    @DisplayName("Should get all bookings info")
    void testGetAllBookings() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should create booking")
    void testCreateBooking() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setCourtId(1L);

        when(bookingService.createBooking(any(BookingCreateRequest.class), eq(1L))).thenReturn(bookingDTO);

        mockMvc.perform(post("/api/bookings")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("Should handle exception when create booking")
    void testCreateBooking_Exception() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setCourtId(1L);

        when(bookingService.createBooking(any(BookingCreateRequest.class), eq(1L)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/bookings")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle create booking without userId")
    void testCreateBooking_NoUserId() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();

        when(bookingService.createBooking(any(BookingCreateRequest.class), isNull()))
                .thenThrow(new RuntimeException("Không xác định được người dùng"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get booking by code")
    void testGetBookingByCode() throws Exception {
        when(bookingService.getBookingDetails("B-123")).thenReturn(bookingDTO);

        mockMvc.perform(get("/api/bookings/code/B-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get booking by code")
    void testGetBookingByCode_Exception() throws Exception {
        when(bookingService.getBookingDetails("B-123")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/code/B-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get my bookings")
    void testGetMyBookings() throws Exception {
        when(bookingService.getCustomerBookingHistory(eq(1L), any(Pageable.class))).thenReturn(bookingPage);

        mockMvc.perform(get("/api/bookings/my-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get my bookings")
    void testGetMyBookings_Exception() throws Exception {
        when(bookingService.getCustomerBookingHistory(eq(1L), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/my-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get owner pending bookings")
    void testGetOwnerPendingBookings() throws Exception {
        when(bookingService.getOwnerPendingBookings(eq(1L), any(Pageable.class))).thenReturn(bookingPage);

        mockMvc.perform(get("/api/bookings/owner/pending-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get owner pending bookings")
    void testGetOwnerPendingBookings_Exception() throws Exception {
        when(bookingService.getOwnerPendingBookings(eq(1L), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/owner/pending-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get owner all bookings")
    void testGetOwnerAllBookings() throws Exception {
        when(bookingService.getOwnerAllBookings(eq(1L), any(Pageable.class))).thenReturn(bookingPage);

        mockMvc.perform(get("/api/bookings/owner/all-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get owner all bookings")
    void testGetOwnerAllBookings_Exception() throws Exception {
        when(bookingService.getOwnerAllBookings(eq(1L), any(Pageable.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/owner/all-bookings")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get booking by id")
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(bookingDTO);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get booking by id")
    void testGetBookingById_Exception() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get court bookings")
    void testGetCourtBookings() throws Exception {
        when(bookingService.getCourtBookings(1L)).thenReturn(List.of(bookingDTO));

        mockMvc.perform(get("/api/bookings/court/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get court bookings")
    void testGetCourtBookings_Exception() throws Exception {
        when(bookingService.getCourtBookings(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/court/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should confirm booking")
    void testConfirmBooking() throws Exception {
        when(bookingService.confirmBooking(1L, 2L)).thenReturn(bookingDTO);

        mockMvc.perform(post("/api/bookings/1/confirm")
                .param("ownerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when confirm booking")
    void testConfirmBooking_Exception() throws Exception {
        when(bookingService.confirmBooking(1L, 2L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/bookings/1/confirm")
                .param("ownerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should cancel booking post")
    void testCancelBookingPost() throws Exception {
        when(bookingService.cancelBooking(1L, "reason")).thenReturn(bookingDTO);

        mockMvc.perform(post("/api/bookings/1/cancel")
                .param("reason", "reason"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when cancel booking post")
    void testCancelBookingPost_Exception() throws Exception {
        when(bookingService.cancelBooking(1L, "reason")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/bookings/1/cancel")
                .param("reason", "reason"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should cancel booking delete")
    void testCancelBookingDelete() throws Exception {
        when(bookingService.cancelBooking(1L, "")).thenReturn(bookingDTO);

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when cancel booking delete")
    void testCancelBookingDelete_Exception() throws Exception {
        when(bookingService.cancelBooking(1L, "")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}

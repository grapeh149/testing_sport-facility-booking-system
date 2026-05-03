package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.service.CheckInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckInController Unit Tests")
class CheckInControllerTest {

    @InjectMocks
    private CheckInController checkInController;

    private MockMvc mockMvc;

    @Mock
    private CheckInService checkInService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CheckInDTO checkInDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkInController).setValidator(new org.springframework.validation.Validator() { public boolean supports(Class<?> c) { return true; } public void validate(Object o, org.springframework.validation.Errors e) {} }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver()).build();
        checkInDTO = new CheckInDTO();
        checkInDTO.setId(1L);
        checkInDTO.setBookingId(1L);
        checkInDTO.setNote("Checked in");
    }

    @Test
    @DisplayName("Should check in")
    void testCheckIn() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setNote("Checked in");

        when(checkInService.checkIn(eq(1L), eq(2L), any(CheckInRequest.class))).thenReturn(checkInDTO);

        mockMvc.perform(post("/api/checkins")
                .param("bookingId", "1")
                .param("checkedByUserId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when check in")
    void testCheckIn_Exception() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setNote("Checked in");

        when(checkInService.checkIn(eq(1L), eq(2L), any(CheckInRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/checkins")
                .param("bookingId", "1")
                .param("checkedByUserId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get check-in record")
    void testGetCheckInRecord() throws Exception {
        when(checkInService.getCheckInRecord(1L)).thenReturn(checkInDTO);

        mockMvc.perform(get("/api/checkins/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get check-in record")
    void testGetCheckInRecord_Exception() throws Exception {
        when(checkInService.getCheckInRecord(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/checkins/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}

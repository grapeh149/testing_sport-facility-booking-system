package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.TimeSlotDTO;
import group6.it.ou.sportfacilitybooking.service.TimeSlotService;
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
@DisplayName("TimeSlotController Unit Tests")
class TimeSlotControllerTest {

    @InjectMocks
    private TimeSlotController timeSlotController;

    private MockMvc mockMvc;

    @Mock
    private TimeSlotService timeSlotService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TimeSlotDTO timeSlotDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(timeSlotController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        timeSlotDTO = new TimeSlotDTO();
        timeSlotDTO.setId(1L);
    }

    @Test
    @DisplayName("Should get all time slots")
    void testGetAllTimeSlots() throws Exception {
        when(timeSlotService.getAllTimeSlots()).thenReturn(List.of(timeSlotDTO));

        mockMvc.perform(get("/api/timeslots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in get all time slots")
    void testGetAllTimeSlots_Exception() throws Exception {
        when(timeSlotService.getAllTimeSlots()).thenThrow(new RuntimeException("Error fetching timeslots"));

        mockMvc.perform(get("/api/timeslots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error fetching timeslots"));
    }

    @Test
    @DisplayName("Should get time slots by court id")
    void testGetTimeSlotsByCourtId() throws Exception {
        when(timeSlotService.getTimeSlotsByCourtId(1L)).thenReturn(List.of(timeSlotDTO));

        mockMvc.perform(get("/api/timeslots/court/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in get time slots by court id")
    void testGetTimeSlotsByCourtId_Exception() throws Exception {
        when(timeSlotService.getTimeSlotsByCourtId(1L)).thenThrow(new RuntimeException("Court not found"));

        mockMvc.perform(get("/api/timeslots/court/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Court not found"));
    }

    @Test
    @DisplayName("Should create time slot")
    void testCreateTimeSlot() throws Exception {
        when(timeSlotService.createTimeSlot(any(TimeSlotDTO.class))).thenReturn(timeSlotDTO);

        mockMvc.perform(post("/api/timeslots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeSlotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in create time slot")
    void testCreateTimeSlot_Exception() throws Exception {
        when(timeSlotService.createTimeSlot(any(TimeSlotDTO.class))).thenThrow(new RuntimeException("Overlap"));

        mockMvc.perform(post("/api/timeslots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeSlotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Overlap"));
    }

    @Test
    @DisplayName("Should update time slot")
    void testUpdateTimeSlot() throws Exception {
        when(timeSlotService.updateTimeSlot(eq(1L), any(TimeSlotDTO.class))).thenReturn(timeSlotDTO);

        mockMvc.perform(put("/api/timeslots/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeSlotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in update time slot")
    void testUpdateTimeSlot_Exception() throws Exception {
        when(timeSlotService.updateTimeSlot(eq(1L), any(TimeSlotDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/timeslots/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeSlotDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Update failed"));
    }

    @Test
    @DisplayName("Should delete time slot")
    void testDeleteTimeSlot() throws Exception {
        mockMvc.perform(delete("/api/timeslots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(timeSlotService, times(1)).deleteTimeSlot(1L);
    }

    @Test
    @DisplayName("Should handle exception in delete time slot")
    void testDeleteTimeSlot_Exception() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(timeSlotService).deleteTimeSlot(1L);

        mockMvc.perform(delete("/api/timeslots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Delete failed"));
    }
}

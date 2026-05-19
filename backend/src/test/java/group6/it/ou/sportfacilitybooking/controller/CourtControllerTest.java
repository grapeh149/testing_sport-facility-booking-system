package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.service.CourtService;
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
@DisplayName("CourtController Unit Tests")
class CourtControllerTest {

    @InjectMocks
    private CourtController courtController;

    private MockMvc mockMvc;

    @Mock
    private CourtService courtService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CourtDTO courtDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courtController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        courtDTO = new CourtDTO();
        courtDTO.setId(1L);
        courtDTO.setName("Court 1");
    }

    @Test
    @DisplayName("Should get courts by facility")
    void testGetCourtsByFacility() throws Exception {
        when(courtService.getCourtsByFacility(1L)).thenReturn(List.of(courtDTO));

        mockMvc.perform(get("/api/courts/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get courts by facility")
    void testGetCourtsByFacility_Exception() throws Exception {
        when(courtService.getCourtsByFacility(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/courts/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get court details")
    void testGetCourtDetails() throws Exception {
        when(courtService.getCourtDetails(1L)).thenReturn(courtDTO);

        mockMvc.perform(get("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get court details")
    void testGetCourtDetails_Exception() throws Exception {
        when(courtService.getCourtDetails(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should create court")
    void testCreateCourt() throws Exception {
        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("Court 1");
        request.setFacilityId(1L);
        request.setSportTypeId(1);
        request.setSurfaceType("Grass");
        request.setIsIndoor(false);

        when(courtService.createCourt(any(CourtCreateRequest.class))).thenReturn(courtDTO);

        mockMvc.perform(post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when create court")
    void testCreateCourt_Exception() throws Exception {
        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("Court 1");

        when(courtService.createCourt(any(CourtCreateRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update court")
    void testUpdateCourt() throws Exception {
        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("Updated");

        when(courtService.updateCourt(eq(1L), any(CourtCreateRequest.class))).thenReturn(courtDTO);

        mockMvc.perform(put("/api/courts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when update court")
    void testUpdateCourt_Exception() throws Exception {
        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("Updated");

        when(courtService.updateCourt(eq(1L), any(CourtCreateRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/courts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete court")
    void testDeleteCourt() throws Exception {
        mockMvc.perform(delete("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when delete court")
    void testDeleteCourt_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(courtService).deleteCourt(1L);

        mockMvc.perform(delete("/api/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should search courts")
    void testSearchCourts() throws Exception {
        when(courtService.searchCourts(anyString(), eq(1L))).thenReturn(List.of(courtDTO));

        mockMvc.perform(get("/api/courts/search")
                .param("address", "Hanoi")
                .param("sportTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when search courts")
    void testSearchCourts_Exception() throws Exception {
        when(courtService.searchCourts(anyString(), eq(1L))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/courts/search")
                .param("address", "Hanoi")
                .param("sportTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
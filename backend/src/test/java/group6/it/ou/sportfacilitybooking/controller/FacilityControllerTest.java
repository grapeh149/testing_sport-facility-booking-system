package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import group6.it.ou.sportfacilitybooking.dto.FacilityCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;
import group6.it.ou.sportfacilitybooking.entity.FacilityStatus;
import group6.it.ou.sportfacilitybooking.service.FacilityService;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityController Unit Tests")
class FacilityControllerTest {

    @InjectMocks
    private FacilityController facilityController;

    private MockMvc mockMvc;

    @Mock
    private FacilityService facilityService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private FacilityDTO facilityDTO;
    private Page<FacilityDTO> facilityPage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facilityController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        facilityDTO = new FacilityDTO();
        facilityDTO.setId(1L);
        facilityDTO.setName("Test Facility");

        facilityPage = new PageImpl<>(List.of(facilityDTO));
    }

    @Test
    @DisplayName("Should search facilities")
    void testSearchFacilities() throws Exception {
        when(facilityService.searchFacilitiesWithPagination(anyString(), any(Pageable.class))).thenReturn(facilityPage);

        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when search facilities")
    void testSearchFacilities_Exception() throws Exception {
        when(facilityService.searchFacilitiesWithPagination(anyString(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get facility details")
    void testGetFacilityDetails() throws Exception {
        when(facilityService.getFacilityDetails(1L)).thenReturn(facilityDTO);

        mockMvc.perform(get("/api/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get facility details")
    void testGetFacilityDetails_Exception() throws Exception {
        when(facilityService.getFacilityDetails(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get facilities by owner")
    void testGetFacilitiesByOwner() throws Exception {
        when(facilityService.getFacilitiesByOwner(1L)).thenReturn(List.of(facilityDTO));

        mockMvc.perform(get("/api/facilities/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get facilities by owner")
    void testGetFacilitiesByOwner_Exception() throws Exception {
        when(facilityService.getFacilitiesByOwner(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/facilities/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get facilities for admin")
    void testGetFacilitiesForAdmin() throws Exception {
        when(facilityService.getFacilitiesForAdmin(any(Pageable.class), eq(null))).thenReturn(facilityPage);

        mockMvc.perform(get("/api/facilities/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get facilities for admin with status filter")
    void testGetFacilitiesForAdminWithStatus() throws Exception {
        when(facilityService.getFacilitiesForAdmin(any(Pageable.class), eq(FacilityStatus.APPROVED)))
                .thenReturn(facilityPage);

        mockMvc.perform(get("/api/facilities/admin/all").param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get facilities for admin")
    void testGetFacilitiesForAdmin_Exception() throws Exception {
        when(facilityService.getFacilitiesForAdmin(any(Pageable.class), eq(null)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/facilities/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should create facility")
    void testCreateFacility() throws Exception {
        FacilityCreateRequest request = new FacilityCreateRequest();
        request.setName("Test Facility");

        when(facilityService.createFacility(any(FacilityCreateRequest.class), eq(1L))).thenReturn(facilityDTO);

        mockMvc.perform(post("/api/facilities")
                .param("ownerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when create facility")
    void testCreateFacility_Exception() throws Exception {
        FacilityCreateRequest request = new FacilityCreateRequest();
        request.setName("Test Facility");

        when(facilityService.createFacility(any(FacilityCreateRequest.class), eq(1L)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/facilities")
                .param("ownerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update facility")
    void testUpdateFacility() throws Exception {
        FacilityCreateRequest request = new FacilityCreateRequest();
        request.setName("Updated Facility");

        when(facilityService.updateFacility(eq(1L), any(FacilityCreateRequest.class))).thenReturn(facilityDTO);

        mockMvc.perform(put("/api/facilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when update facility")
    void testUpdateFacility_Exception() throws Exception {
        FacilityCreateRequest request = new FacilityCreateRequest();
        request.setName("Updated Facility");

        when(facilityService.updateFacility(eq(1L), any(FacilityCreateRequest.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/facilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update facility cover image")
    void testUpdateFacilityCoverImage() throws Exception {
        when(facilityService.updateFacilityCoverImage(eq(1L), anyString())).thenReturn(facilityDTO);

        mockMvc.perform(patch("/api/facilities/1/cover-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverImageUrl\":\"http://example.com/image.jpg\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when update facility cover image")
    void testUpdateFacilityCoverImage_Exception() throws Exception {
        when(facilityService.updateFacilityCoverImage(eq(1L), anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(patch("/api/facilities/1/cover-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverImageUrl\":\"http://example.com/image.jpg\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle missing coverImageUrl")
    void testUpdateFacilityCoverImage_MissingUrl() throws Exception {
        mockMvc.perform(patch("/api/facilities/1/cover-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete facility")
    void testDeleteFacility() throws Exception {
        mockMvc.perform(delete("/api/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when delete facility")
    void testDeleteFacility_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(facilityService).deleteFacility(1L);

        mockMvc.perform(delete("/api/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should approve facility")
    void testApproveFacility() throws Exception {
        mockMvc.perform(post("/api/facilities/1/approve")
                .param("adminId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when approve facility")
    void testApproveFacility_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(facilityService).approveFacility(1L, 2L);

        mockMvc.perform(post("/api/facilities/1/approve")
                .param("adminId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reject facility")
    void testRejectFacility() throws Exception {
        mockMvc.perform(post("/api/facilities/1/reject")
                .param("adminId", "2")
                .param("reason", "Missing info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when reject facility")
    void testRejectFacility_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(facilityService).rejectFacility(1L, 2L, "Missing info");

        mockMvc.perform(post("/api/facilities/1/reject")
                .param("adminId", "2")
                .param("reason", "Missing info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
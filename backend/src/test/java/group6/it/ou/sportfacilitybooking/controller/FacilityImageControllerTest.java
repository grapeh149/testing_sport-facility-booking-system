package group6.it.ou.sportfacilitybooking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import group6.it.ou.sportfacilitybooking.service.FacilityImageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityImageController Unit Tests")
class FacilityImageControllerTest {

    @Mock
    private FacilityImageService facilityImageService;

    @InjectMocks
    private FacilityImageController facilityImageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facilityImageController).build();
    }

    @Test
    @DisplayName("Should get images by facility ID")
    void testGetImagesByFacility_Success() throws Exception {
        when(facilityImageService.getImagesByFacilityId(1L)).thenReturn(List.of(new FacilityImage()));

        mockMvc.perform(get("/api/facility-images/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return error when get images by facility throws exception")
    void testGetImagesByFacility_Exception() throws Exception {
        when(facilityImageService.getImagesByFacilityId(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/facility-images/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    @DisplayName("Should add single image successfully")
    void testAddImage_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrl", "http://test.com/img.jpg");
        request.put("sortOrder", 1);

        when(facilityImageService.addImage(eq(1L), anyString(), anyInt())).thenReturn(new FacilityImage());

        mockMvc.perform(post("/api/facility-images/facility/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return error when imageUrl is empty")
    void testAddImage_EmptyUrl() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrl", "   ");

        mockMvc.perform(post("/api/facility-images/facility/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle add image exception")
    void testAddImage_Exception() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrl", "http://test.com/img.jpg");
        request.put("sortOrder", 1);

        when(facilityImageService.addImage(anyLong(), anyString(), anyInt())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/facility-images/facility/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should add multiple images")
    void testAddImages_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrls", List.of("url1", "url2"));

        when(facilityImageService.addImages(eq(1L), anyList())).thenReturn(List.of(new FacilityImage()));

        mockMvc.perform(post("/api/facility-images/facility/1/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return error when imageUrls is empty")
    void testAddImages_Empty() throws Exception {
        Map<String, Object> request = new HashMap<>();

        mockMvc.perform(post("/api/facility-images/facility/1/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return error when imageUrls > 5")
    void testAddImages_ExceedLimit() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrls", List.of("1", "2", "3", "4", "5", "6"));

        mockMvc.perform(post("/api/facility-images/facility/1/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle add images exception")
    void testAddImages_Exception() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("imageUrls", List.of("url1"));

        when(facilityImageService.addImages(anyLong(), anyList())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/facility-images/facility/1/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete image")
    void testDeleteImage_Success() throws Exception {
        mockMvc.perform(delete("/api/facility-images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(facilityImageService, times(1)).deleteImage(1L);
    }

    @Test
    @DisplayName("Should handle delete image exception")
    void testDeleteImage_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(facilityImageService).deleteImage(1L);

        mockMvc.perform(delete("/api/facility-images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete all images")
    void testDeleteAllImages_Success() throws Exception {
        mockMvc.perform(delete("/api/facility-images/facility/1/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(facilityImageService, times(1)).deleteAllImagesByFacility(1L);
    }

    @Test
    @DisplayName("Should handle delete all images exception")
    void testDeleteAllImages_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(facilityImageService).deleteAllImagesByFacility(1L);

        mockMvc.perform(delete("/api/facility-images/facility/1/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update image sort order")
    void testUpdateSortOrder_Success() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("sortOrder", 2);

        when(facilityImageService.updateImageSortOrder(1L, 2)).thenReturn(new FacilityImage());

        mockMvc.perform(put("/api/facility-images/1/sort-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return error when sortOrder is null")
    void testUpdateSortOrder_Null() throws Exception {
        Map<String, Integer> request = new HashMap<>();

        mockMvc.perform(put("/api/facility-images/1/sort-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle update sort order exception")
    void testUpdateSortOrder_Exception() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("sortOrder", 2);

        when(facilityImageService.updateImageSortOrder(1L, 2)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/facility-images/1/sort-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
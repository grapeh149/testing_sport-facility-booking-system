package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.service.SportTypeService;

@ExtendWith(MockitoExtension.class)
@DisplayName("SportTypeController Unit Tests")
class SportTypeControllerTest {

    @InjectMocks
    private SportTypeController sportTypeController;

    private MockMvc mockMvc;

    @Mock
    private SportTypeService sportTypeService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private SportTypeDTO sportTypeDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sportTypeController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        sportTypeDTO = new SportTypeDTO();
        sportTypeDTO.setId(1);
        sportTypeDTO.setName("Soccer");
    }

    @Test
    @DisplayName("Should get all active sport types")
    void testGetAllActiveSportTypes() throws Exception {
        when(sportTypeService.getAllActiveSportTypes()).thenReturn(List.of(sportTypeDTO));

        mockMvc.perform(get("/api/sport-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in get all active sport types")
    void testGetAllActiveSportTypes_Exception() throws Exception {
        when(sportTypeService.getAllActiveSportTypes()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/sport-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    @DisplayName("Should get all sport types")
    void testGetAllSportTypes() throws Exception {
        when(sportTypeService.getAllSportTypes()).thenReturn(List.of(sportTypeDTO));

        mockMvc.perform(get("/api/sport-types/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in get all sport types")
    void testGetAllSportTypes_Exception() throws Exception {
        when(sportTypeService.getAllSportTypes()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/sport-types/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get sport type by id")
    void testGetSportTypeById() throws Exception {
        when(sportTypeService.getSportTypeById(1)).thenReturn(sportTypeDTO);

        mockMvc.perform(get("/api/sport-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in get sport type by id")
    void testGetSportTypeById_Exception() throws Exception {
        when(sportTypeService.getSportTypeById(1)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/sport-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    @DisplayName("Should create sport type")
    void testCreateSportType() throws Exception {
        when(sportTypeService.createSportType(any(SportTypeDTO.class))).thenReturn(sportTypeDTO);

        mockMvc.perform(post("/api/sport-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in create sport type")
    void testCreateSportType_Exception() throws Exception {
        when(sportTypeService.createSportType(any(SportTypeDTO.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/sport-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update sport type")
    void testUpdateSportType() throws Exception {
        when(sportTypeService.updateSportType(eq(1), any(SportTypeDTO.class))).thenReturn(sportTypeDTO);

        mockMvc.perform(put("/api/sport-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception in update sport type")
    void testUpdateSportType_Exception() throws Exception {
        when(sportTypeService.updateSportType(eq(1), any(SportTypeDTO.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/sport-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.mapper.SportTypeMapper;
import group6.it.ou.sportfacilitybooking.repository.SportTypeRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SportTypeService Unit Tests")
class SportTypeServiceTest {

    @Mock
    private SportTypeRepository sportTypeRepository;

    @Mock
    private SportTypeMapper sportTypeMapper;

    @InjectMocks
    private SportTypeService sportTypeService;

    private SportType sportType;
    private SportTypeDTO sportTypeDTO;

    @BeforeEach
    void setUp() {
        sportType = new SportType();
        sportType.setId(1);
        sportType.setName("Soccer");
        sportType.setIsActive(true);

        sportTypeDTO = new SportTypeDTO();
        sportTypeDTO.setId(1);
        sportTypeDTO.setName("Soccer");
    }

    @Test
    @DisplayName("Should get all active sport types")
    void testGetAllActiveSportTypes() {
        when(sportTypeRepository.findByIsActive(true)).thenReturn(List.of(sportType));
        when(sportTypeMapper.toDTO(sportType)).thenReturn(sportTypeDTO);

        List<SportTypeDTO> results = sportTypeService.getAllActiveSportTypes();

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should get all sport types")
    void testGetAllSportTypes() {
        when(sportTypeRepository.findAll()).thenReturn(List.of(sportType));
        when(sportTypeMapper.toDTO(sportType)).thenReturn(sportTypeDTO);

        List<SportTypeDTO> results = sportTypeService.getAllSportTypes();

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should get sport type by id")
    void testGetSportTypeById() {
        when(sportTypeRepository.findById(1)).thenReturn(Optional.of(sportType));
        when(sportTypeMapper.toDTO(sportType)).thenReturn(sportTypeDTO);

        SportTypeDTO result = sportTypeService.getSportTypeById(1);

        assertNotNull(result);
        assertEquals("Soccer", result.getName());
    }

    @Test
    @DisplayName("Should create sport type")
    void testCreateSportType() {
        when(sportTypeRepository.save(any(SportType.class))).thenReturn(sportType);
        when(sportTypeMapper.toDTO(sportType)).thenReturn(sportTypeDTO);

        SportTypeDTO request = new SportTypeDTO();
        request.setName("Basketball");

        SportTypeDTO result = sportTypeService.createSportType(request);

        assertNotNull(result);
        verify(sportTypeRepository).save(any(SportType.class));
    }

    @Test
    @DisplayName("Should throw exception if create with empty name")
    void testCreateSportTypeEmptyName() {
        SportTypeDTO request = new SportTypeDTO();
        request.setName("");

        assertThrows(RuntimeException.class, () -> sportTypeService.createSportType(request));
        verify(sportTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update sport type")
    void testUpdateSportType() {
        when(sportTypeRepository.findById(1)).thenReturn(Optional.of(sportType));
        when(sportTypeRepository.save(any(SportType.class))).thenReturn(sportType);
        when(sportTypeMapper.toDTO(sportType)).thenReturn(sportTypeDTO);

        SportTypeDTO updateReq = new SportTypeDTO();
        updateReq.setName("Updated");

        SportTypeDTO result = sportTypeService.updateSportType(1, updateReq);

        assertNotNull(result);
        verify(sportTypeRepository).save(any(SportType.class));
    }
}
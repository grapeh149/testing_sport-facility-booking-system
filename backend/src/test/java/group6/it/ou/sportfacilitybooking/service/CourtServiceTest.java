package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.mapper.CourtMapper;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import group6.it.ou.sportfacilitybooking.repository.SportTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourtService Unit Tests")
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private SportTypeRepository sportTypeRepository;

    @Mock
    private CourtMapper courtMapper;

    @InjectMocks
    private CourtService courtService;

    private Court court;
    private Facility facility;
    private SportType sportType;
    private CourtCreateRequest request;
    private CourtDTO courtDTO;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");

        sportType = new SportType();
        sportType.setId(1);
        sportType.setName("Soccer");

        court = new Court();
        court.setId(1L);
        court.setName("Court 1");
        court.setFacility(facility);
        court.setSportType(sportType);

        request = new CourtCreateRequest();
        request.setFacilityId(1L);
        request.setSportTypeId(1);
        request.setName("Court 1");
        request.setIsActive(true);

        courtDTO = new CourtDTO();
        courtDTO.setId(1L);
        courtDTO.setName("Court 1");
    }

    @Test
    @DisplayName("Should get courts by facility id")
    void testGetCourtsByFacility() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(courtRepository.findByFacilityId(1L)).thenReturn(List.of(court));
        when(courtMapper.toDTO(court)).thenReturn(courtDTO);

        List<CourtDTO> courts = courtService.getCourtsByFacility(1L);

        assertEquals(1, courts.size());
        assertEquals("Court 1", courts.get(0).getName());
    }

    @Test
    @DisplayName("Should get court details")
    void testGetCourtDetails() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(courtMapper.toDTO(court)).thenReturn(courtDTO);

        CourtDTO result = courtService.getCourtDetails(1L);

        assertNotNull(result);
        assertEquals("Court 1", result.getName());
    }

    @Test
    @DisplayName("Should create court")
    void testCreateCourt() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(sportTypeRepository.findById(1)).thenReturn(Optional.of(sportType));
        when(courtMapper.toEntity(request)).thenReturn(court);
        when(courtRepository.save(any(Court.class))).thenReturn(court);
        when(courtMapper.toDTO(court)).thenReturn(courtDTO);

        CourtDTO result = courtService.createCourt(request);

        assertNotNull(result);
        assertEquals("Court 1", result.getName());
        verify(courtRepository).save(any(Court.class));
    }

    @Test
    @DisplayName("Should update court")
    void testUpdateCourt() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(sportTypeRepository.findById(1)).thenReturn(Optional.of(sportType));
        when(courtRepository.save(any(Court.class))).thenReturn(court);
        when(courtMapper.toDTO(court)).thenReturn(courtDTO);

        request.setDescription("Updated desc");
        request.setSurfaceType("Grass");
        request.setIsIndoor(false);

        CourtDTO result = courtService.updateCourt(1L, request);

        assertNotNull(result);
        verify(courtRepository).save(any(Court.class));
    }

    @Test
    @DisplayName("Should delete court")
    void testDeleteCourt() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        assertDoesNotThrow(() -> courtService.deleteCourt(1L));

        verify(courtRepository).delete(court);
    }

    @Test
    @DisplayName("Should search courts")
    void testSearchCourts() {
        when(courtRepository.searchActiveApprovedCourts(1L)).thenReturn(List.of(court));
        when(courtMapper.toDTO(court)).thenReturn(courtDTO);

        List<CourtDTO> results = courtService.searchCourts("Court 1", 1L);

        assertEquals(1, results.size());
    }
}

package group6.it.ou.sportfacilitybooking.service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import group6.it.ou.sportfacilitybooking.dto.TimeSlotDTO;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import group6.it.ou.sportfacilitybooking.mapper.TimeSlotMapper;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.TimeSlotRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimeSlotService Unit Tests")
class TimeSlotServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private TimeSlotMapper timeSlotMapper;

    @InjectMocks
    private TimeSlotService timeSlotService;

    private Court court;
    private TimeSlot existingSlot;
    private TimeSlotDTO validSlotDTO;

    @BeforeEach
    void setUp() {
        court = new Court();
        court.setId(1L);
        court.setName("Court 1");
        court.setIsActive(true);

        existingSlot = new TimeSlot();
        existingSlot.setId(1L);
        existingSlot.setCourt(court);
        existingSlot.setDayOfWeek((byte) 2); // Monday
        existingSlot.setStartTime(LocalTime.of(8, 0));
        existingSlot.setEndTime(LocalTime.of(10, 0));
        existingSlot.setPrice(BigDecimal.valueOf(100000));
        existingSlot.setDepositRate(BigDecimal.valueOf(30));
        existingSlot.setIsActive(true);

        validSlotDTO = new TimeSlotDTO();
        validSlotDTO.setCourtId(1L);
        validSlotDTO.setDayOfWeek((byte) 2);
        validSlotDTO.setStartTime(LocalTime.of(10, 0));
        validSlotDTO.setEndTime(LocalTime.of(12, 0));
        validSlotDTO.setPrice(BigDecimal.valueOf(120000));
        validSlotDTO.setDepositRate(BigDecimal.valueOf(30));
    }

    @Test
    @DisplayName("Should get all active time slots")
    void testGetAllTimeSlots() {
        TimeSlot inactiveSlot = new TimeSlot();
        inactiveSlot.setIsActive(false);
        when(timeSlotRepository.findAll()).thenReturn(List.of(existingSlot, inactiveSlot));
        when(timeSlotMapper.toDTO(existingSlot)).thenReturn(validSlotDTO);

        List<TimeSlotDTO> result = timeSlotService.getAllTimeSlots();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get active time slots by court ID")
    void testGetTimeSlotsByCourtId() {
        when(timeSlotRepository.findByCourtId(1L)).thenReturn(List.of(existingSlot));
        when(timeSlotMapper.toDTO(existingSlot)).thenReturn(validSlotDTO);

        List<TimeSlotDTO> result = timeSlotService.getTimeSlotsByCourtId(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should create time slot successfully")
    void testCreateTimeSlotSuccess() {
        TimeSlot newSlot = new TimeSlot();
        newSlot.setId(2L);
        newSlot.setCourt(court);
        newSlot.setStartTime(LocalTime.of(10, 0));
        newSlot.setEndTime(LocalTime.of(12, 0));
        newSlot.setIsActive(true);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(timeSlotRepository.findByCourtId(1L)).thenReturn(List.of(existingSlot));
        when(timeSlotMapper.toEntity(validSlotDTO)).thenReturn(newSlot);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(newSlot);
        when(timeSlotMapper.toDTO(newSlot)).thenReturn(validSlotDTO);

        TimeSlotDTO result = timeSlotService.createTimeSlot(validSlotDTO);

        assertNotNull(result);
        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("Should reject create time slot with overlap")
    void testCreateTimeSlot_Overlap() {
        validSlotDTO.setStartTime(LocalTime.of(9, 0)); // overlaps with 08:00 - 10:00
        validSlotDTO.setEndTime(LocalTime.of(11, 0));

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(timeSlotRepository.findByCourtId(1L)).thenReturn(List.of(existingSlot));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> timeSlotService.createTimeSlot(validSlotDTO));
        assertTrue(exception.getMessage().contains("overlaps"));
    }

    @Test
    @DisplayName("Should update time slot successfully")
    void testUpdateTimeSlotSuccess() {
        TimeSlotDTO updateDTO = new TimeSlotDTO();
        updateDTO.setCourtId(1L);
        updateDTO.setDayOfWeek((byte) 2);
        updateDTO.setStartTime(LocalTime.of(10, 0));
        updateDTO.setEndTime(LocalTime.of(12, 0));

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot));
        // There are no other slots except itself
        when(timeSlotRepository.findByCourtId(1L)).thenReturn(List.of(existingSlot));
        when(timeSlotRepository.save(existingSlot)).thenReturn(existingSlot);
        when(timeSlotMapper.toDTO(existingSlot)).thenReturn(updateDTO);

        TimeSlotDTO result = timeSlotService.updateTimeSlot(1L, updateDTO);

        assertNotNull(result);
        verify(timeSlotRepository, times(1)).save(existingSlot);
    }

    @Test
    @DisplayName("Should reject update time slot with overlap")
    void testUpdateTimeSlot_Overlap() {
        TimeSlot otherSlot = new TimeSlot();
        otherSlot.setId(2L);
        otherSlot.setDayOfWeek((byte) 2);
        otherSlot.setStartTime(LocalTime.of(11, 0));
        otherSlot.setEndTime(LocalTime.of(13, 0));
        otherSlot.setIsActive(true);

        TimeSlotDTO updateDTO = new TimeSlotDTO();
        updateDTO.setCourtId(1L);
        updateDTO.setDayOfWeek((byte) 2);
        updateDTO.setStartTime(LocalTime.of(10, 0));
        updateDTO.setEndTime(LocalTime.of(12, 0)); // Overlaps with 11:00-13:00

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot));
        when(timeSlotRepository.findByCourtId(1L)).thenReturn(List.of(existingSlot, otherSlot));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> timeSlotService.updateTimeSlot(1L, updateDTO));
        assertTrue(exception.getMessage().contains("overlaps"));
    }

    @Test
    @DisplayName("Should soft delete time slot")
    void testDeleteTimeSlot() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(existingSlot));

        timeSlotService.deleteTimeSlot(1L);

        assertFalse(existingSlot.getIsActive());
        verify(timeSlotRepository, times(1)).save(existingSlot);
    }
}
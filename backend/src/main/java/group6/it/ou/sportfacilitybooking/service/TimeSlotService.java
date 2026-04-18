package group6.it.ou.sportfacilitybooking.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.TimeSlotDTO;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.TimeSlot;
import group6.it.ou.sportfacilitybooking.mapper.TimeSlotMapper;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.TimeSlotRepository;

@Service
@Transactional
public class TimeSlotService {
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired
    private CourtRepository courtRepository;
    
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    
    public List<TimeSlotDTO> getAllTimeSlots() {
        return timeSlotRepository.findAll().stream()
            .filter(ts -> ts.getIsActive())
            .map(timeSlotMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<TimeSlotDTO> getTimeSlotsByCourtId(Long courtId) {
        return timeSlotRepository.findByCourtId(courtId).stream()
            .filter(ts -> ts.getIsActive())
            .map(timeSlotMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public TimeSlotDTO createTimeSlot(TimeSlotDTO dto) {
        Court court = courtRepository.findById(dto.getCourtId())
            .orElseThrow(() -> new RuntimeException("Court not found"));
        
        // Check for overlapping time slots
        boolean hasOverlap = timeSlotRepository.findByCourtId(dto.getCourtId()).stream()
            .filter(ts -> ts.getIsActive())
            .anyMatch(ts -> {
                // Check if days overlap
                boolean daysOverlap = ts.getDayOfWeek() == null || 
                                     dto.getDayOfWeek() == null || 
                                     ts.getDayOfWeek().equals(dto.getDayOfWeek());
                
                if (!daysOverlap) {
                    return false; // Different days, no overlap
                }
                
                // Check if times overlap
                return doTimesOverlap(dto.getStartTime(), dto.getEndTime(), 
                                     ts.getStartTime(), ts.getEndTime());
            });
        
        if (hasOverlap) {
            throw new RuntimeException("TimeSlot overlaps with existing slot");
        }
        
        TimeSlot timeSlot = timeSlotMapper.toEntity(dto);
        timeSlot.setCourt(court);
        timeSlot.setIsActive(true);
        
        timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(timeSlot);
    }
    
    public TimeSlotDTO updateTimeSlot(Long id, TimeSlotDTO dto) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("TimeSlot not found"));
        
        // Check for overlaps excluding this slot
        boolean hasOverlap = timeSlotRepository.findByCourtId(dto.getCourtId()).stream()
            .filter(ts -> !ts.getId().equals(id) && ts.getIsActive())
            .anyMatch(ts -> doesOverlap(ts, dto));
        
        if (hasOverlap) {
            throw new RuntimeException("TimeSlot overlaps with existing slot");
        }
        
        timeSlot.setStartTime(dto.getStartTime());
        timeSlot.setEndTime(dto.getEndTime());
        timeSlot.setPrice(dto.getPrice());
        timeSlot.setDepositRate(dto.getDepositRate());
        timeSlot.setDayOfWeek(dto.getDayOfWeek());
        
        timeSlotRepository.save(timeSlot);
        return timeSlotMapper.toDTO(timeSlot);
    }
    
    public void deleteTimeSlot(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("TimeSlot not found"));
        timeSlot.setIsActive(false);
        timeSlotRepository.save(timeSlot);
    }
    
    private boolean doesOverlap(TimeSlot existingSlot, TimeSlotDTO newSlot) {
        // Check if days overlap or if either is for all days (NULL)
        boolean daysOverlap = existingSlot.getDayOfWeek() == null || 
                             newSlot.getDayOfWeek() == null || 
                             existingSlot.getDayOfWeek().equals(newSlot.getDayOfWeek());
        
        if (!daysOverlap) {
            return false; // Different days, no overlap
        }
        
        // Check if times overlap
        return doTimesOverlap(newSlot.getStartTime(), newSlot.getEndTime(), 
                             existingSlot.getStartTime(), existingSlot.getEndTime());
    }
    
    private boolean doTimesOverlap(LocalTime startTime1, LocalTime endTime1, 
                                   LocalTime startTime2, LocalTime endTime2) {
        // Two time intervals overlap if: startTime1 < endTime2 AND endTime1 > startTime2
        // Adjacent intervals (e.g., 7-8 and 8-9) do NOT overlap
        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2);
    }
}

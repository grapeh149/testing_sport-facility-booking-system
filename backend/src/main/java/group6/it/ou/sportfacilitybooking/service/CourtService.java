package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.mapper.CourtMapper;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import group6.it.ou.sportfacilitybooking.repository.SportTypeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourtService {
    
    @Autowired
    private CourtRepository courtRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private SportTypeRepository sportTypeRepository;
    
    @Autowired
    private CourtMapper courtMapper;
    
    public List<CourtDTO> getCourtsByFacility(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        return courtRepository.findByFacilityId(facilityId).stream()
            .map(courtMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    public CourtDTO getCourtDetails(Long id) {
        Court court = courtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Court not found"));
        return courtMapper.toDTO(court);
    }
    
    public CourtDTO createCourt(CourtCreateRequest request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        SportType sportType = sportTypeRepository.findById(request.getSportTypeId())
            .orElseThrow(() -> new RuntimeException("SportType not found"));
        
        Court court = courtMapper.toEntity(request);
        court.setFacility(facility);
        court.setSportType(sportType);
        court.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        court.setCreatedAt(LocalDateTime.now());
        
        courtRepository.save(court);
        return courtMapper.toDTO(court);
    }
    
    public CourtDTO updateCourt(Long id, CourtCreateRequest request) {
        Court court = courtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Court not found"));
        
        // Update name if provided
        if (request.getName() != null && !request.getName().isEmpty()) {
            court.setName(request.getName());
        }
        
        // Update description if provided
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            court.setDescription(request.getDescription());
        }
        
        // Update surfaceType if provided
        if (request.getSurfaceType() != null && !request.getSurfaceType().isEmpty()) {
            court.setSurfaceType(request.getSurfaceType());
        }
        
        // Update isIndoor if provided
        if (request.getIsIndoor() != null) {
            court.setIsIndoor(request.getIsIndoor());
        }

        // Update isActive if provided
        if (request.getIsActive() != null) {
            court.setIsActive(request.getIsActive());
        }

        // Update facility only if facilityId is provided
        if (request.getFacilityId() != null) {
            Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));
            court.setFacility(facility);
        }
        
        // Update sportType only if sportTypeId is provided
        if (request.getSportTypeId() != null) {
            SportType sportType = sportTypeRepository.findById(request.getSportTypeId())
                .orElseThrow(() -> new RuntimeException("SportType not found"));
            court.setSportType(sportType);
        }
        
        courtRepository.save(court);
        return courtMapper.toDTO(court);
    }

    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Court not found"));

        courtRepository.delete(court);
    }

    // Search courts with filters
    public List<CourtDTO> searchCourts(String address, Long sportTypeId) {
        return courtRepository.searchCourts(address, sportTypeId).stream()
            .map(courtMapper::toDTO)
            .collect(Collectors.toList());
    }
}

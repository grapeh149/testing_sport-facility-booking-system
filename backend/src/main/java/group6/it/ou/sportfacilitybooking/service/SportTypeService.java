package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.mapper.SportTypeMapper;
import group6.it.ou.sportfacilitybooking.repository.SportTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportTypeService {
    
    @Autowired
    private SportTypeRepository sportTypeRepository;
    
    @Autowired
    private SportTypeMapper sportTypeMapper;
    
    public List<SportTypeDTO> getAllActiveSportTypes() {
        List<SportType> sportTypes = sportTypeRepository.findByIsActive(true);
        return sportTypes.stream()
                .map(sportTypeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<SportTypeDTO> getAllSportTypes() {
        List<SportType> sportTypes = sportTypeRepository.findAll();
        return sportTypes.stream()
                .map(sportTypeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public SportTypeDTO getSportTypeById(Integer id) {
        SportType sportType = sportTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loại thể thao không tìm thấy"));
        return sportTypeMapper.toDTO(sportType);
    }
    
    public SportTypeDTO createSportType(SportTypeDTO sportTypeDTO) {
        if (sportTypeDTO.getName() == null || sportTypeDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên loại thể thao không được để trống");
        }
        
        SportType sportType = new SportType();
        sportType.setName(sportTypeDTO.getName());
        sportType.setDescription(sportTypeDTO.getDescription());
        sportType.setIconUrl(sportTypeDTO.getIconUrl());
        sportType.setIsActive(sportTypeDTO.getIsActive() != null ? sportTypeDTO.getIsActive() : true);
        
        SportType savedSportType = sportTypeRepository.save(sportType);
        return sportTypeMapper.toDTO(savedSportType);
    }
    
    public SportTypeDTO updateSportType(Integer id, SportTypeDTO sportTypeDTO) {
        if (sportTypeDTO.getName() == null || sportTypeDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên loại thể thao không được để trống");
        }
        
        SportType sportType = sportTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loại thể thao không tìm thấy"));
        
        sportType.setName(sportTypeDTO.getName());
        sportType.setDescription(sportTypeDTO.getDescription());
        sportType.setIconUrl(sportTypeDTO.getIconUrl());
        if (sportTypeDTO.getIsActive() != null) {
            sportType.setIsActive(sportTypeDTO.getIsActive());
        }
        
        SportType updatedSportType = sportTypeRepository.save(sportType);
        return sportTypeMapper.toDTO(updatedSportType);
    }
}

package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.entity.SportType;
import group6.it.ou.sportfacilitybooking.mapper.SportTypeMapper;
import group6.it.ou.sportfacilitybooking.repository.SportTypeRepository;

@Service
public class SportTypeService {

    @Autowired
    private SportTypeRepository repository;

    @Autowired
    private SportTypeMapper mapper;

    // SELECT * FROM LoaiSanTheThao
    public List<SportTypeDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaLoaiSan = ?
    public SportTypeDTO getById(String id) {
        SportType entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LoaiSanTheThao với MaLoaiSan: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public SportTypeDTO create(SportTypeDTO dto) {
        SportType entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public SportTypeDTO update(String id, SportTypeDTO dto) {
        SportType entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LoaiSanTheThao với MaLoaiSan: " + id));
        entity.setTenLoaiSan(dto.getTenLoaiSan());
        entity.setHinhAnh(dto.getHinhAnh());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(String id) {
        repository.deleteById(id);
    }
}

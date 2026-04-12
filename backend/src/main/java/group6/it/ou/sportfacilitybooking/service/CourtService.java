package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.entity.Court;
import group6.it.ou.sportfacilitybooking.mapper.CourtMapper;
import group6.it.ou.sportfacilitybooking.repository.CourtRepository;

@Service
public class CourtService {

    @Autowired
    private CourtRepository repository;

    @Autowired
    private CourtMapper mapper;

    // SELECT * FROM SanTheThao
    public List<CourtDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT * FROM SanTheThao WHERE MaChiNhanh = ?
    public List<SanTheThaoDTO> getByFacilityId(Integer facilityId) {
        return repository.findByMaChiNhanh(facilityId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaSan = ?
    public CourtDTO getById(Integer id) {
        Court entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SanTheThao với MaSan: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public CourtDTO create(CourtDTO dto) {
        Court entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // INSERT court for facility from path variable
    public SanTheThaoDTO createForFacility(Integer facilityId, SanTheThaoDTO dto) {
        SanTheThao entity = mapper.toEntity(dto);
        entity.setMaChiNhanh(facilityId);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public CourtDTO update(Integer id, CourtDTO dto) {
        Court entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SanTheThao với MaSan: " + id));
        entity.setMaLoaiSan(dto.getMaLoaiSan());
        entity.setMaChiNhanh(dto.getMaChiNhanh());
        entity.setSoSan(dto.getSoSan());
        entity.setGhiChu(dto.getGhiChu());
        entity.setHinhAnh(dto.getHinhAnh());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}

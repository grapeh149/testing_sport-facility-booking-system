package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.mapper.FacilityMapper;
import group6.it.ou.sportfacilitybooking.repository.FalicilyRepository;

@Service
public class FacilityService {

    @Autowired
    private FalicilyRepository repository;

    @Autowired
    private FacilityMapper mapper;

    // SELECT * FROM ChiNhanh
    public List<CourtCreateRequest> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaChiNhanh = ?
    public CourtCreateRequest getById(Integer id) {
        Facility entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ChiNhanh với MaChiNhanh: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public CourtCreateRequest create(CourtCreateRequest dto) {
        Facility entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public CourtCreateRequest update(Integer id, CourtCreateRequest dto) {
        Facility entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ChiNhanh với MaChiNhanh: " + id));
        entity.setTenChiNhanh(dto.getTenChiNhanh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setHinhAnh(dto.getHinhAnh());
        entity.setGhiChu(dto.getGhiChu());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}

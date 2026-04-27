package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.ChiNhanhDTO;
import group6.it.ou.sportfacilitybooking.entity.ChiNhanh;
import group6.it.ou.sportfacilitybooking.mapper.ChiNhanhMapper;
import group6.it.ou.sportfacilitybooking.repository.ChiNhanhRepository;

@Service
public class ChiNhanhService {

    @Autowired
    private ChiNhanhRepository repository;

    @Autowired
    private ChiNhanhMapper mapper;

    // SELECT * FROM ChiNhanh
    public List<ChiNhanhDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaChiNhanh = ?
    public ChiNhanhDTO getById(Integer id) {
        ChiNhanh entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ChiNhanh với MaChiNhanh: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public ChiNhanhDTO create(ChiNhanhDTO dto) {
        ChiNhanh entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public ChiNhanhDTO update(Integer id, ChiNhanhDTO dto) {
        ChiNhanh entity = repository.findById(id)
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

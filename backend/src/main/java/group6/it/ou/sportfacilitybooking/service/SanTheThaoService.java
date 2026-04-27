package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.SanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.entity.SanTheThao;
import group6.it.ou.sportfacilitybooking.mapper.SanTheThaoMapper;
import group6.it.ou.sportfacilitybooking.repository.SanTheThaoRepository;

@Service
public class SanTheThaoService {

    @Autowired
    private SanTheThaoRepository repository;

    @Autowired
    private SanTheThaoMapper mapper;

    // SELECT * FROM SanTheThao
    public List<SanTheThaoDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaSan = ?
    public SanTheThaoDTO getById(Integer id) {
        SanTheThao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SanTheThao với MaSan: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public SanTheThaoDTO create(SanTheThaoDTO dto) {
        SanTheThao entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public SanTheThaoDTO update(Integer id, SanTheThaoDTO dto) {
        SanTheThao entity = repository.findById(id)
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

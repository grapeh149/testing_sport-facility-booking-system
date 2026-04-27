package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.LoaiSanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.entity.LoaiSanTheThao;
import group6.it.ou.sportfacilitybooking.mapper.LoaiSanTheThaoMapper;
import group6.it.ou.sportfacilitybooking.repository.LoaiSanTheThaoRepository;

@Service
public class LoaiSanTheThaoService {

    @Autowired
    private LoaiSanTheThaoRepository repository;

    @Autowired
    private LoaiSanTheThaoMapper mapper;

    // SELECT * FROM LoaiSanTheThao
    public List<LoaiSanTheThaoDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaLoaiSan = ?
    public LoaiSanTheThaoDTO getById(String id) {
        LoaiSanTheThao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LoaiSanTheThao với MaLoaiSan: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public LoaiSanTheThaoDTO create(LoaiSanTheThaoDTO dto) {
        LoaiSanTheThao entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public LoaiSanTheThaoDTO update(String id, LoaiSanTheThaoDTO dto) {
        LoaiSanTheThao entity = repository.findById(id)
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

package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.it.ou.sportfacilitybooking.dto.NhanVienDTO;
import group6.it.ou.sportfacilitybooking.entity.PaymentStatus;
import group6.it.ou.sportfacilitybooking.mapper.PaymentMapper;
import group6.it.ou.sportfacilitybooking.repository.NhanVienRepository;

@Service
public class PaymentService {

    @Autowired
    private NhanVienRepository repository;

    @Autowired
    private PaymentMapper mapper;

    // SELECT * FROM NhanVien
    public List<NhanVienDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaNhanVien = ?
    public NhanVienDTO getById(Integer id) {
        PaymentStatus entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy NhanVien với MaNhanVien: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public NhanVienDTO create(NhanVienDTO dto) {
        PaymentStatus entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public NhanVienDTO update(Integer id, NhanVienDTO dto) {
        PaymentStatus entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy NhanVien với MaNhanVien: " + id));
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setTitle(dto.getTitle());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
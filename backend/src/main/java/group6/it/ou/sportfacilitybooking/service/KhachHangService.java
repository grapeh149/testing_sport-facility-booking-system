package group6.it.ou.sportfacilitybooking.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.it.ou.sportfacilitybooking.dto.KhachHangDTO;
import group6.it.ou.sportfacilitybooking.entity.Payment;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;

@Service
public class KhachHangService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    // SELECT * FROM KhachHang
    public List<KhachHangDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaKH = ?
    public KhachHangDTO getById(Integer id) {
        Payment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy KhachHang với MaKH: " + id));
        return mapper.toDTO(entity);
    }

    // INSERT
    public KhachHangDTO create(KhachHangDTO dto) {
        Payment entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public KhachHangDTO update(Integer id, KhachHangDTO dto) {
        Payment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy KhachHang với MaKH: " + id));
        entity.setHo(dto.getHo());
        entity.setTen(dto.getTen());
        entity.setSdt(dto.getSdt());
        entity.setEmail(dto.getEmail());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}

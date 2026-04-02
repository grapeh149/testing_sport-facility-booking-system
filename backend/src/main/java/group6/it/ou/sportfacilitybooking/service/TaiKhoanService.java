package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.it.ou.sportfacilitybooking.dto.TaiKhoanDTO;
import group6.it.ou.sportfacilitybooking.entity.TaiKhoan;
import group6.it.ou.sportfacilitybooking.mapper.TaiKhoanMapper;
import group6.it.ou.sportfacilitybooking.repository.TaiKhoanRepository;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository repository;

    @Autowired
    private TaiKhoanMapper mapper;

    // SELECT * FROM TaiKhoan
    public List<TaiKhoanDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaTaiKhoan = ?
    public TaiKhoanDTO getById(Integer id) {
        TaiKhoan entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TaiKhoan với MaTaiKhoan: " + id));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE TenTaiKhoan = ?
    public TaiKhoanDTO getByTenTaiKhoan(String tenTaiKhoan) {
        TaiKhoan entity = repository.findByTenTaiKhoan(tenTaiKhoan);
        if (entity == null)
            throw new RuntimeException("Không tìm thấy TaiKhoan: " + tenTaiKhoan);
        return mapper.toDTO(entity);
    }

    // SELECT WHERE Role = ?
    public List<TaiKhoanDTO> getByRole(String role) {
        return repository.findByRole(role)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // INSERT
    public TaiKhoanDTO create(TaiKhoanDTO dto) {
        TaiKhoan entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public TaiKhoanDTO update(Integer id, TaiKhoanDTO dto) {
        TaiKhoan entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TaiKhoan với MaTaiKhoan: " + id));
        entity.setTenTaiKhoan(dto.getTenTaiKhoan());
        entity.setMatKhau(dto.getMatKhau());
        entity.setRole(dto.getRole());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
        // ĐĂNG KÝ
    public TaiKhoanDTO dangKy(TaiKhoanDTO dto) {
        // Kiểm tra tên tài khoản đã tồn tại chưa
        TaiKhoan existing = repository.findByTenTaiKhoan(dto.getTenTaiKhoan());
        if (existing != null)
            throw new RuntimeException("Tên tài khoản đã tồn tại: " + dto.getTenTaiKhoan());

        TaiKhoan entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // ĐĂNG NHẬP
    public TaiKhoanDTO dangNhap(TaiKhoanDTO dto) {
        TaiKhoan entity = repository.findByTenTaiKhoan(dto.getTenTaiKhoan());
        if (entity == null)
            throw new RuntimeException("Tài khoản không tồn tại");
        if (!entity.getMatKhau().equals(dto.getMatKhau()))
            throw new RuntimeException("Mật khẩu không đúng");
        return mapper.toDTO(entity);
    }
}

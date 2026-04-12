package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.TaiKhoanMapper;
import group6.it.ou.sportfacilitybooking.repository.TaiKhoanRepository;

@Service
public class UserService {

    @Autowired
    private TaiKhoanRepository repository;

    @Autowired
    private TaiKhoanMapper mapper;

    // SELECT * FROM TaiKhoan
    public List<UserDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaTaiKhoan = ?
    public UserDTO getById(Integer id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TaiKhoan với MaTaiKhoan: " + id));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE TenTaiKhoan = ?
    public UserDTO getByTenTaiKhoan(String tenTaiKhoan) {
        User entity = repository.findByTenTaiKhoan(tenTaiKhoan);
        if (entity == null)
            throw new RuntimeException("Không tìm thấy TaiKhoan: " + tenTaiKhoan);
        return mapper.toDTO(entity);
    }

    // SELECT WHERE Role = ?
    public List<UserDTO> getByRole(String role) {
        return repository.findByRole(role)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // INSERT
    public UserDTO create(UserDTO dto) {
        User entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public UserDTO update(Integer id, UserDTO dto) {
        User entity = repository.findById(id)
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
    public UserDTO dangKy(UserDTO dto) {
        // Kiểm tra tên tài khoản đã tồn tại chưa
        User existing = repository.findByTenTaiKhoan(dto.getTenTaiKhoan());
        if (existing != null)
            throw new RuntimeException("Tên tài khoản đã tồn tại: " + dto.getTenTaiKhoan());

        User entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // ĐĂNG NHẬP
    public UserDTO dangNhap(UserDTO dto) {
        User entity = repository.findByTenTaiKhoan(dto.getTenTaiKhoan());
        if (entity == null)
            throw new RuntimeException("Tài khoản không tồn tại");
        if (!entity.getMatKhau().equals(dto.getMatKhau()))
            throw new RuntimeException("Mật khẩu không đúng");
        return mapper.toDTO(entity);
    }
}

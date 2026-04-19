package group6.it.ou.sportfacilitybooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.request.UserLoginRequest;
import group6.it.ou.sportfacilitybooking.request.UserRegisterRequest;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SELECT * FROM users
    public List<UserDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE id = ?
    public UserDTO getById(Long id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với id: " + id));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE username = ?
    public UserDTO getByUsername(String username) {
        User entity = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User: " + username));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE email = ?
    public UserDTO getByEmail(String email) {
        User entity = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với email: " + email));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE role = ?
    public List<UserDTO> getByRole(String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return repository.findByRole(userRole)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // INSERT
    public UserDTO create(UserDTO dto) {
        // Kiểm tra username đã tồn tại
        if (repository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại: " + dto.getUsername());
        }
        // Kiểm tra email đã tồn tại
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + dto.getEmail());
        }

        User entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public UserDTO update(Long id, UserDTO dto) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với id: " + id));
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getRole() != null) {
            entity.setRole(UserRole.valueOf(dto.getRole()));
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy User với id: " + id);
        }
        repository.deleteById(id);
    }

    // ĐĂNG KÝ
    public UserDTO register(UserRegisterRequest request) {
        // Kiểm tra username đã tồn tại
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại: " + request.getUsername());
        }
        // Kiểm tra email đã tồn tại
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        User entity = new User();
        entity.setFullName(request.getFullName());
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone());
        entity.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        return mapper.toDTO(repository.save(entity));
    }

    // ĐĂNG NHẬP
    public UserDTO login(UserLoginRequest request) {
        User entity = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // Kiểm tra password (dùng PasswordEncoder)
        if (!passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        // Kiểm tra user có active không
        if (!entity.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        return mapper.toDTO(entity);
    }

    // Kiểm tra username có tồn tại
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    // Kiểm tra email có tồn tại
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}

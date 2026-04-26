package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public UserDTO createUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được đăng ký");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // Set role based on request, default to CUSTOMER
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(UserRole.CUSTOMER);
            }
        } else {
            user.setRole(UserRole.CUSTOMER);
        }
        
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        return userMapper.toDTO(user);
    }
    
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }
    
    public UserDTO updateUserProfile(Long userId, UserDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getRole() != null) {
            try {
                user.setRole(UserRole.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Keep existing role if invalid
            }
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return userMapper.toDTO(user);
    }
    
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toDTO);
    }
    
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group6.it.ou.sportfacilitybooking.config.JwtTokenProvider;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserLoginRequest;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.dto.AuthResponse;
import group6.it.ou.sportfacilitybooking.dto.UpdateProfileRequest;
import group6.it.ou.sportfacilitybooking.dto.UpdatePasswordRequest;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public AuthResponse register(UserRegistrationRequest request) {
        logger.info("[REGISTER] Attempting to register user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("[REGISTER] Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email đã được đăng ký");
        }
        
        logger.debug("[REGISTER] Email is available, creating new user");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        logger.info("[REGISTER] User registered successfully with email: {}", request.getEmail());
        
        // Generate JWT token
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    public AuthResponse login(UserLoginRequest request) {
        logger.info("[LOGIN] Attempting login for email: {}", request.getEmail());
        
        // Step 1: Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (!userOpt.isPresent()) {
            logger.warn("[LOGIN] User not found with email: {}", request.getEmail());
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        User user = userOpt.get();
        logger.debug("[LOGIN] User found: {} (ID: {})", user.getEmail(), user.getId());
        
        // Step 2: Verify password
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        logger.debug("[LOGIN] Password verification result: {}", passwordMatch);
        
        if (!passwordMatch) {
            logger.warn("[LOGIN] Invalid password for user: {}", request.getEmail());
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        // Step 3: Check if user is active
        if (!user.getIsActive()) {
            logger.warn("[LOGIN] Account is disabled for user: {}", request.getEmail());
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa");
        }
        
        logger.info("[LOGIN] Login successful for user: {} (ID: {})", user.getEmail(), user.getId());
        
        // Generate JWT token
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    public UserDTO changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("[CHANGE_PASSWORD] User {} requesting password change", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("[CHANGE_PASSWORD] User not found with ID: {}", userId);
                return new RuntimeException("User không tồn tại");
            });
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            logger.warn("[CHANGE_PASSWORD] Invalid old password for user: {}", userId);
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        
        logger.debug("[CHANGE_PASSWORD] Old password verified, updating to new password");
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        logger.info("[CHANGE_PASSWORD] Password changed successfully for user: {}", userId);
        return userMapper.toDTO(user);
    }
    
    public UserDTO getProfile(Long userId) {
        logger.info("[GET_PROFILE] Fetching profile for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("[GET_PROFILE] User not found with ID: {}", userId);
                return new RuntimeException("User không tồn tại");
            });
        
        logger.debug("[GET_PROFILE] Profile fetched successfully for user: {}", userId);
        return userMapper.toDTO(user);
    }
    
    public UserDTO updateProfile(Long userId, UpdateProfileRequest request) {
        logger.info("[UPDATE_PROFILE] User {} requesting profile update", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("[UPDATE_PROFILE] User not found with ID: {}", userId);
                return new RuntimeException("User không tồn tại");
            });
        
        // Check if username is already taken by another user
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                User existingUser = userRepository.findByUsername(request.getUsername()).get();
                if (!existingUser.getId().equals(userId)) {
                    logger.warn("[UPDATE_PROFILE] Username already taken: {}", request.getUsername());
                    throw new RuntimeException("Tên đăng nhập đã được sử dụng");
                }
            }
            user.setUsername(request.getUsername());
        }
        
        // Update fields
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        logger.info("[UPDATE_PROFILE] Profile updated successfully for user: {}", userId);
        return userMapper.toDTO(user);
    }
    
    public UserDTO updatePassword(Long userId, UpdatePasswordRequest request) {
        logger.info("[UPDATE_PASSWORD] User {} requesting password update", userId);
        
        // Validate new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            logger.warn("[UPDATE_PASSWORD] Confirm password does not match for user: {}", userId);
            throw new RuntimeException("Mật khẩu xác nhận không trùng khớp");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("[UPDATE_PASSWORD] User not found with ID: {}", userId);
                return new RuntimeException("User không tồn tại");
            });
        
        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            logger.warn("[UPDATE_PASSWORD] Invalid old password for user: {}", userId);
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        
        logger.debug("[UPDATE_PASSWORD] Old password verified, updating to new password");
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        logger.info("[UPDATE_PASSWORD] Password updated successfully for user: {}", userId);
        return userMapper.toDTO(user);
    }
}

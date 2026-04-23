package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserLoginRequest;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.dto.AuthResponse;
import group6.it.ou.sportfacilitybooking.dto.UpdateProfileRequest;
import group6.it.ou.sportfacilitybooking.dto.UpdatePasswordRequest;
import group6.it.ou.sportfacilitybooking.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            logger.info("[API] POST /api/auth/register - Email: {}", request.getEmail());
            AuthResponse result = authService.register(request);
            logger.info("[API] Registration successful for: {}", request.getEmail());
            return new ApiResponse<>(true, result, "Đăng ký thành công");
        } catch (Exception e) {
            logger.error("[API] Registration failed: {}", e.getMessage(), e);
            return new ApiResponse<>(false, null, "Đăng ký thất bại: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            logger.info("[API] POST /api/auth/login - Email: {}", request.getEmail());
            AuthResponse result = authService.login(request);
            logger.info("[API] Login successful for: {}", request.getEmail());
            return new ApiResponse<>(true, result, "Đăng nhập thành công");
        } catch (Exception e) {
            logger.error("[API] Login failed for {}: {}", request.getEmail(), e.getMessage());
            return new ApiResponse<>(false, null, "Đăng nhập thất bại: " + e.getMessage());
        }
    }
    
    @PostMapping("/change-password")
    public ApiResponse<UserDTO> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            logger.info("[API] POST /api/auth/change-password - User ID: {}", userId);
            UserDTO result = authService.changePassword(userId, oldPassword, newPassword);
            logger.info("[API] Password changed successfully for user: {}", userId);
            return new ApiResponse<>(true, result, "Đổi mật khẩu thành công");
        } catch (Exception e) {
            logger.error("[API] Change password failed for user {}: {}", userId, e.getMessage());
            return new ApiResponse<>(false, null, "Đổi mật khẩu thất bại: " + e.getMessage());
        }
    }
    
    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            logger.info("[API] GET /api/auth/profile - User ID: {}", userId);
            UserDTO result = authService.getProfile(userId);
            return new ApiResponse<>(true, result, "Lấy thông tin profile thành công");
        } catch (Exception e) {
            logger.error("[API] Get profile failed: {}", e.getMessage());
            return new ApiResponse<>(false, null, "Lấy thông tin profile thất bại: " + e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            logger.info("[API] PUT /api/auth/profile - User ID: {}", userId);
            UserDTO result = authService.updateProfile(userId, request);
            logger.info("[API] Profile updated successfully for user: {}", userId);
            return new ApiResponse<>(true, result, "Cập nhật profile thành công");
        } catch (Exception e) {
            logger.error("[API] Update profile failed for user: {}", e.getMessage());
            return new ApiResponse<>(false, null, "Cập nhật profile thất bại: " + e.getMessage());
        }
    }
    
    @PostMapping("/update-password")
    public ApiResponse<UserDTO> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            logger.info("[API] POST /api/auth/update-password - User ID: {}", userId);
            UserDTO result = authService.updatePassword(userId, request);
            logger.info("[API] Password updated successfully for user: {}", userId);
            return new ApiResponse<>(true, result, "Cập nhật mật khẩu thành công");
        } catch (Exception e) {
            logger.error("[API] Update password failed for user: {}", e.getMessage());
            return new ApiResponse<>(false, null, "Cập nhật mật khẩu thất bại: " + e.getMessage());
        }
    }
}

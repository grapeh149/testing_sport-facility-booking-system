package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ApiResponse<UserDTO> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserDTO result = userService.createUser(request);
            return new ApiResponse<>(true, result, "Tạo người dùng thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getProfile(@PathVariable Long id) {
        try {
            UserDTO result = userService.getUserProfile(id);
            return new ApiResponse<>(true, result, "Lấy thông tin thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateProfile(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            UserDTO result = userService.updateUserProfile(id, dto);
            return new ApiResponse<>(true, result, "Cập nhật thông tin thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping
    public ApiResponse<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> result = userService.getAllUsers(pageable);
            return new ApiResponse<>(true, result, "Lấy danh sách người dùng thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return new ApiResponse<>(true, null, "Tài khoản đã bị vô hiệu hóa");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PutMapping("/{id}/lock")
    public ApiResponse<?> lockUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return new ApiResponse<>(true, null, "Khóa tài khoản thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PutMapping("/{id}/unlock")
    public ApiResponse<?> unlockUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return new ApiResponse<>(true, null, "Mở khóa tài khoản thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}

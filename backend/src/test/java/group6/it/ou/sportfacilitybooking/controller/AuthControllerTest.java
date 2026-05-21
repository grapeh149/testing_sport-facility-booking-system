package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.*;
import group6.it.ou.sportfacilitybooking.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserDTO userDTO;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        authResponse = new AuthResponse("token", 1L, "test@example.com", "Test User", "CUSTOMER", null);
    }

    @Test
    @DisplayName("Should register successfully")
    void testRegisterSuccess() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setRole("CUSTOMER");

        when(authService.register(any(UserRegistrationRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng ký thành công"));
    }

    @Test
    @DisplayName("Should handle register failure")
    void testRegisterFailure() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("Test User");

        when(authService.register(any(UserRegistrationRequest.class))).thenThrow(new RuntimeException("Email exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Đăng ký thất bại: Email exists"));
    }

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.login(any(UserLoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle login failure")
    void testLoginFailure() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.login(any(UserLoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Đăng nhập thất bại: Invalid credentials"));
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword() throws Exception {
        when(authService.changePassword(eq(1L), anyString(), anyString())).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/change-password")
                .param("userId", "1")
                .param("oldPassword", "old")
                .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle change password failure")
    void testChangePasswordFailure() throws Exception {
        when(authService.changePassword(eq(1L), anyString(), anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/auth/change-password")
                .param("userId", "1")
                .param("oldPassword", "old")
                .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get profile")
    void testGetProfile() throws Exception {
        when(authService.getProfile(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/auth/profile")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle get profile failure")
    void testGetProfileFailure() throws Exception {
        when(authService.getProfile(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/auth/profile")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle get profile without userId")
    void testGetProfileNoUserId() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Không tìm thấy thông tin user"));
    }

    @Test
    @DisplayName("Should update profile")
    void testUpdateProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");

        when(authService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/auth/profile")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle update profile failure")
    void testUpdateProfileFailure() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");

        when(authService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/auth/profile")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle update profile without userId")
    void testUpdateProfileNoUserId() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        mockMvc.perform(put("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update password via profile")
    void testUpdatePassword() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(authService.updatePassword(eq(1L), any(UpdatePasswordRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/update-password")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle update password failure")
    void testUpdatePasswordFailure() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(authService.updatePassword(eq(1L), any(UpdatePasswordRequest.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/auth/update-password")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle update password without userId")
    void testUpdatePasswordNoUserId() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        mockMvc.perform(post("/api/auth/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
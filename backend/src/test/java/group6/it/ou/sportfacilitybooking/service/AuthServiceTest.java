package group6.it.ou.sportfacilitybooking.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import group6.it.ou.sportfacilitybooking.config.JwtTokenProvider;
import group6.it.ou.sportfacilitybooking.dto.AuthResponse;
import group6.it.ou.sportfacilitybooking.dto.UpdatePasswordRequest;
import group6.it.ou.sportfacilitybooking.dto.UpdateProfileRequest;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserLoginRequest;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User existingUser;
    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Tạo user đã tồn tại với password đã hash (BCrypt của "password123")
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("user@test.com");
        existingUser.setFullName("Test User");
        existingUser.setPhone("0123456789");
        // BCrypt hash của "password123"
        existingUser.setPasswordHash("$2a$10$dummyhash.thatisnotreal.butlongenough.fortest");
        existingUser.setRole(UserRole.CUSTOMER);
        existingUser.setIsActive(true);

        // Request đăng ký hợp lệ
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setEmail("newuser@test.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFullName("New User");
        registrationRequest.setPhone("0987654321");

        // Request đăng nhập
        loginRequest = new UserLoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("password123");
    }

    // ========== A-01: testRegisterSuccess ==========
    @Test
    @DisplayName("A-01: Đăng ký thành công với thông tin hợp lệ")
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(tokenProvider.generateToken(any(), anyString())).thenReturn("mock-jwt-token");

        // Act
        AuthResponse response = authService.register(registrationRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("newuser@test.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ========== A-02: testRegisterWithDuplicateEmail ==========
    @Test
    @DisplayName("A-02: Đăng ký thất bại khi email đã được đăng ký")
    void testRegisterWithDuplicateEmail() {
        // Arrange: email đã tồn tại
        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registrationRequest));
        assertTrue(exception.getMessage().contains("Email đã được đăng ký"));
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== A-03: testLoginSuccess ==========
    @Test
    @DisplayName("A-03: Đăng nhập thành công với email và mật khẩu đúng")
    void testLoginSuccess() {
        // Dùng BCrypt thực để hash password cho test này
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hashedPw = encoder.encode("password123");
        existingUser.setPasswordHash(hashedPw);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));
        when(tokenProvider.generateToken(any(), anyString())).thenReturn("mock-jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("user@test.com", response.getEmail());
    }

    // ========== A-04: testLoginWithWrongPassword ==========
    @Test
    @DisplayName("A-04: Đăng nhập thất bại khi sai mật khẩu")
    void testLoginWithWrongPassword() {
        // Dùng hash thực với password khác
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        existingUser.setPasswordHash(encoder.encode("correctpassword"));

        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("Email hoặc mật khẩu không đúng"));
    }

    // ========== A-05: testLoginWithDisabledAccount ==========
    @Test
    @DisplayName("A-05: Đăng nhập thất bại khi tài khoản bị vô hiệu hóa")
    void testLoginWithDisabledAccount() {
        // Arrange: tài khoản bị disable
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        existingUser.setPasswordHash(encoder.encode("password123"));
        existingUser.setIsActive(false);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("Tài khoản của bạn đã bị vô hiệu hóa"));
    }

    // ========== A-06: testChangePassword_Success ==========
    @Test
    @DisplayName("A-06: Đổi mật khẩu thành công với mật khẩu cũ đúng")
    void testChangePassword_Success() {
        // Arrange
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        existingUser.setPasswordHash(encoder.encode("oldpassword"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

        // Act
        UserDTO result = authService.changePassword(1L, "oldpassword", "newpassword");

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        // Kiểm tra password mới đã được encode
        assertTrue(encoder.matches("newpassword", existingUser.getPasswordHash()));
    }

    // ========== A-07: testChangePassword_WrongOldPassword ==========
    @Test
    @DisplayName("A-07: Đổi mật khẩu thất bại khi mật khẩu cũ sai")
    void testChangePassword_WrongOldPassword() {
        // Arrange
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        existingUser.setPasswordHash(encoder.encode("correctoldpassword"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.changePassword(1L, "wrongoldpassword", "newpassword"));
        assertTrue(exception.getMessage().contains("Mật khẩu cũ không đúng"));
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== A-08: testUpdateProfile_Success ==========
    @Test
    @DisplayName("A-08: Cập nhật thông tin cá nhân thành công")
    void testUpdateProfile_Success() {
        // Arrange
        UpdateProfileRequest profileRequest = new UpdateProfileRequest();
        profileRequest.setFullName("Updated Name");
        profileRequest.setPhone("0999999999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

        // Act
        UserDTO result = authService.updateProfile(1L, profileRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", existingUser.getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ========== A-09: testUpdateProfile_UsernameTaken ==========
    @Test
    @DisplayName("A-09: Cập nhật thông tin thất bại khi username đã bị dùng bởi người khác")
    void testUpdateProfile_UsernameTaken() {
        // Arrange
        UpdateProfileRequest profileRequest = new UpdateProfileRequest();
        profileRequest.setUsername("takenusername");

        User anotherUser = new User();
        anotherUser.setId(99L); // ID khác với user đang update (ID=1)
        anotherUser.setEmail("other@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("takenusername")).thenReturn(Optional.of(anotherUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.updateProfile(1L, profileRequest));
        assertTrue(exception.getMessage().contains("Tên đăng nhập đã được sử dụng"));
        verify(userRepository, never()).save(any(User.class));
    }
}

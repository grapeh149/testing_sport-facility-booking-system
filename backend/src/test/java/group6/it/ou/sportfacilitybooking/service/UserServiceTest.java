package group6.it.ou.sportfacilitybooking.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveNewCustomer_whenEmailIsUnused() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhone("0123456789");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO dto = new UserDTO();
            dto.setEmail(user.getEmail());
            dto.setFullName(user.getFullName());
            dto.setPhone(user.getPhone());
            dto.setRole(user.getRole().name());
            return dto;
        });

        UserDTO result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertEquals("0123456789", result.getPhone());
        assertEquals(UserRole.CUSTOMER.name(), result.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrow_whenEmailAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhone("0123456789");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(request));

        assertEquals("Email đã được đăng ký", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deactivateAndActivateUser_shouldToggleActiveFlag() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUser(1L);
        assertFalse(user.getIsActive());
        verify(userRepository, times(1)).save(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.activateUser(1L);
        assertTrue(user.getIsActive());
        verify(userRepository, times(2)).save(user);
    }

    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhone("0123456789");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(java.util.List.of(user)));
        when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserDTO dto = new UserDTO();
            dto.setEmail(u.getEmail());
            dto.setFullName(u.getFullName());
            dto.setPhone(u.getPhone());
            return dto;
        });

        Page<UserDTO> page = userService.getAllUsers(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("test@example.com", page.getContent().get(0).getEmail());
    }

    // [MỚI] - bổ sung độ phủ--------------------------------

    @Test
    void createUser_withInvalidRole_shouldDefaultToCustomer() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test2@example.com");
        request.setPassword("password");
        request.setFullName("Test");
        request.setPhone("0123");
        request.setRole("INVALID_ROLE");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userMapper.toDTO(any(User.class))).thenAnswer(inv -> {
            UserDTO dto = new UserDTO();
            dto.setRole(((User) inv.getArgument(0)).getRole().name());
            return dto;
        });

        UserDTO result = userService.createUser(request);
        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    void createUser_withNullRole_shouldDefaultToCustomer() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test3@example.com");
        request.setPassword("password");
        request.setFullName("Test");
        request.setPhone("0123");
        request.setRole(null);

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userMapper.toDTO(any(User.class))).thenAnswer(inv -> {
            UserDTO dto = new UserDTO();
            dto.setRole(((User) inv.getArgument(0)).getRole().name());
            return dto;
        });

        UserDTO result = userService.createUser(request);
        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    void getUserProfile_shouldReturnDto_whenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        assertNotNull(userService.getUserProfile(1L));
    }

    @Test
    void getUserProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserProfile(1L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateUserProfile_shouldUpdateFields_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.CUSTOMER);

        UserDTO dto = new UserDTO();
        dto.setFullName("New Name");
        dto.setPhone("0999");
        dto.setAvatarUrl("https://avatar.url");
        dto.setRole("OWNER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        userService.updateUserProfile(1L, dto);

        assertEquals("New Name", user.getFullName());
        assertEquals("0999", user.getPhone());
        assertEquals(UserRole.OWNER, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserProfile_withInvalidRole_shouldKeepExistingRole() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.CUSTOMER);

        UserDTO dto = new UserDTO();
        dto.setRole("INVALID");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.updateUserProfile(1L, dto);

        assertEquals(UserRole.CUSTOMER, user.getRole());
    }

    @Test
    void updateUserProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUserProfile(1L, new UserDTO()));
        assertEquals("User not found", ex.getMessage());
    }
}
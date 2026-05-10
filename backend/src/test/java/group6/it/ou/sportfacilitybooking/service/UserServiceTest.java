package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
<<<<<<< HEAD
import group6.it.ou.sportfacilitybooking.entity.UserRole;
=======
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
<<<<<<< HEAD
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
=======
import group6.it.ou.sportfacilitybooking.service.UserService;
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
<<<<<<< HEAD
@DisplayName("UserService Unit Tests")
=======
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

<<<<<<< HEAD
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(UserRole.CUSTOMER);
        user.setIsActive(true);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setFullName("Test User");
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("new@example.com");
        request.setFullName("New User");
        request.setPassword("password");
        request.setRole("CUSTOMER");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);
=======
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
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b

        UserDTO result = userService.createUser(request);

        assertNotNull(result);
<<<<<<< HEAD
=======
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertEquals("0123456789", result.getPhone());
        assertEquals(UserRole.CUSTOMER.name(), result.getRole());

>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
        verify(userRepository).save(any(User.class));
    }

    @Test
<<<<<<< HEAD
    @DisplayName("Should reject create user if email exists")
    void testCreateUserEmailExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user profile")
    void testGetUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserProfile(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should update user profile")
    void testUpdateUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO updateDto = new UserDTO();
        updateDto.setFullName("Updated Name");
        updateDto.setRole("ADMIN");

        UserDTO result = userService.updateUserProfile(1L, updateDto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Page<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should deactivate user")
    void testDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should activate user")
    void testActivateUser() {
        user.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.activateUser(1L);

        assertTrue(user.getIsActive());
        verify(userRepository).save(user);
=======
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
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
    }
}

package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;
import group6.it.ou.sportfacilitybooking.mapper.UserMapper;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;
import group6.it.ou.sportfacilitybooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
}

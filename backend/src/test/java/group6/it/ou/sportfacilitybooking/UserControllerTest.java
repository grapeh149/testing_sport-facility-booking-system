package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.controller.UserController;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_shouldReturnSuccessResponse() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        request.setPhone("0123456789");

        UserDTO dto = new UserDTO();
        dto.setEmail("test@example.com");
        dto.setFullName("Test User");

        when(userService.createUser(request)).thenReturn(dto);

        ApiResponse<UserDTO> response = userController.createUser(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("test@example.com", response.getData().getEmail());
        assertEquals("Tạo người dùng thành công", response.getMessage());
    }

    @Test
    void createUser_shouldReturnFailureResponseOnException() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("duplicate@example.com");

        when(userService.createUser(request)).thenThrow(new RuntimeException("Email đã được đăng ký"));

        ApiResponse<UserDTO> response = userController.createUser(request);

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertEquals("Email đã được đăng ký", response.getMessage());
    }
}

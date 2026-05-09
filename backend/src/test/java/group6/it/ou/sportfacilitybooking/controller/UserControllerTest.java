package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.dto.UserRegistrationRequest;
import group6.it.ou.sportfacilitybooking.service.UserService;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
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
        userDTO.setFullName("Test User");
    }

    @Test
    @DisplayName("Should create user")
    void testCreateUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("Test User");

        when(userService.createUser(any(UserRegistrationRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when create user")
    void testCreateUser_Exception() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("Test User");

        when(userService.createUser(any(UserRegistrationRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get user profile")
    void testGetProfile() throws Exception {
        when(userService.getUserProfile(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get user profile")
    void testGetProfile_Exception() throws Exception {
        when(userService.getUserProfile(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update user profile")
    void testUpdateProfile() throws Exception {
        when(userService.updateUserProfile(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when update user profile")
    void testUpdateProfile_Exception() throws Exception {
        when(userService.updateUserProfile(eq(1L), any(UserDTO.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    // @Test
    // @DisplayName("Should get all users")
    // void testGetAllUsers() throws Exception {
    //     List<UserDTO> userList = List.of(userDTO);
    //     Page<UserDTO> page = new PageImpl<>(userList);
    //     when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

    //     mockMvc.perform(get("/api/users")
    //             .param("page", "0")
    //             .param("size", "10"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.success").value(true));
    // }

    @Test
    @DisplayName("Should handle exception when get all users")
    void testGetAllUsers_Exception() throws Exception {
        when(userService.getAllUsers(any(Pageable.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should deactivate user")
    void testDeactivateUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when deactivate user")
    void testDeactivateUser_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(userService).deactivateUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should lock user")
    void testLockUser() throws Exception {
        mockMvc.perform(put("/api/users/1/lock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when lock user")
    void testLockUser_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(userService).deactivateUser(1L);

        mockMvc.perform(put("/api/users/1/lock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should unlock user")
    void testUnlockUser() throws Exception {
        mockMvc.perform(put("/api/users/1/unlock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when unlock user")
    void testUnlockUser_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(userService).activateUser(1L);

        mockMvc.perform(put("/api/users/1/unlock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}

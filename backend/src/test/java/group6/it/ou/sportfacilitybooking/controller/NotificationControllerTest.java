package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.service.NotificationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationController Unit Tests")
class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    private NotificationDTO notificationDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setValidator(new org.springframework.validation.Validator() {
                    public boolean supports(Class<?> c) {
                        return true;
                    }

                    public void validate(Object o, org.springframework.validation.Errors e) {
                    }
                }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .build();
        notificationDTO = new NotificationDTO();
        notificationDTO.setId(1L);
        notificationDTO.setMessage("Test message");
    }

    @Test
    @DisplayName("Should get notifications")
    void testGetNotifications() throws Exception {
        Page<NotificationDTO> page = new PageImpl<>(List.of(notificationDTO));
        when(notificationService.getNotifications(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/notifications")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get notifications")
    void testGetNotifications_Exception() throws Exception {
        when(notificationService.getNotifications(eq(1L), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/notifications")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get latest notifications")
    void testGetLatestNotifications() throws Exception {
        when(notificationService.getLatestNotifications(1L)).thenReturn(List.of(notificationDTO));

        mockMvc.perform(get("/api/notifications/latest")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get latest notifications")
    void testGetLatestNotifications_Exception() throws Exception {
        when(notificationService.getLatestNotifications(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/notifications/latest")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get unread count")
    void testGetUnreadCount() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread-count")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @DisplayName("Should handle exception when get unread count")
    void testGetUnreadCount_Exception() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/notifications/unread-count")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should mark as read")
    void testMarkAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when mark as read")
    void testMarkAsRead_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(notificationService).markAsRead(1L, 1L);

        mockMvc.perform(put("/api/notifications/1/read")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should mark all as read")
    void testMarkAllAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when mark all as read")
    void testMarkAllAsRead_Exception() throws Exception {
        doThrow(new RuntimeException("Error")).when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/notifications/read-all")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return error if user id not found")
    void testMissingUserId() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
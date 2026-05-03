package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.controller.NotificationController;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void getNotifications_shouldReturnPayload() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);

        NotificationDTO dto = new NotificationDTO();
        when(notificationService.getNotifications(eq(1L), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        ApiResponse<Map<String, Object>> response = notificationController.getNotifications(0, 10, request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1, ((List<?>) response.getData().get("content")).size());
    }

    @Test
    void getUnreadCount_shouldReturnCount() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 2L);

        when(notificationService.getUnreadCount(2L)).thenReturn(5L);

        ApiResponse<Long> response = notificationController.getUnreadCount(request);

        assertTrue(response.isSuccess());
        assertEquals(5L, response.getData());
    }

    @Test
    void markAsRead_shouldReturnSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 3L);

        ApiResponse<?> response = notificationController.markAsRead(10L, request);

        assertTrue(response.isSuccess());
        verify(notificationService).markAsRead(10L, 3L);
    }
}

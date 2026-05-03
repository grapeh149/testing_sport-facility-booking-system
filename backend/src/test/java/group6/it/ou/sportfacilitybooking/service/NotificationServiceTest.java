package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.NotificationMapper;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationDTO notificationDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setIsRead(false);

        notificationDTO = new NotificationDTO();
        notificationDTO.setId(1L);
        notificationDTO.setIsRead(false);
    }

    @Test
    @DisplayName("Should get notifications")
    void testGetNotifications() {
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(page);
        when(notificationMapper.toDTO(notification)).thenReturn(notificationDTO);

        Page<NotificationDTO> result = notificationService.getNotifications(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should get latest notifications")
    void testGetLatestNotifications() {
        when(notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(notification));
        when(notificationMapper.toDTO(notification)).thenReturn(notificationDTO);

        List<NotificationDTO> result = notificationService.getLatestNotifications(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get unread count")
    void testGetUnreadCount() {
        when(notificationRepository.countByUserIdAndIsRead(1L, false)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Should mark as read")
    void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L, 1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should throw exception if mark as read for different user")
    void testMarkAsReadPermissionDenied() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, 99L));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should mark all as read")
    void testMarkAllAsRead() {
        when(notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(notification));

        notificationService.markAllAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).saveAll(anyList());
    }
}

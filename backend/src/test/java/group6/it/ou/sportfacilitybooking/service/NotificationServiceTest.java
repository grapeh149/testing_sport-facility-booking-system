package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.NotificationMapper;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
<<<<<<< HEAD
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
=======
import group6.it.ou.sportfacilitybooking.service.NotificationService;
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
<<<<<<< HEAD
import org.springframework.data.domain.Page;
=======
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
=======
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

<<<<<<< HEAD
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
=======
    @Test
    void getNotifications_shouldReturnPagedNotificationDTOs() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Hello");

        when(notificationRepository.findByUserId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationMapper.toDTO(notification)).thenReturn(new NotificationDTO());

        var page = notificationService.getNotifications(1L, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        verify(notificationRepository).findByUserId(1L, PageRequest.of(0, 10));
    }

    @Test
    void markAsRead_shouldUpdateNotificationWhenOwnerMatches() {
        User user = new User();
        user.setId(1L);
        Notification notification = new Notification();
        notification.setId(2L);
        notification.setUser(user);
        notification.setIsRead(false);

        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(2L, 1L);
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
<<<<<<< HEAD
    @DisplayName("Should throw exception if mark as read for different user")
    void testMarkAsReadPermissionDenied() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, 99L));
=======
    void markAsRead_shouldThrowWhenUserDoesNotOwnNotification() {
        User owner = new User();
        owner.setId(1L);
        Notification notification = new Notification();
        notification.setId(3L);
        notification.setUser(owner);
        notification.setIsRead(false);

        when(notificationRepository.findById(3L)).thenReturn(Optional.of(notification));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(3L, 2L));

        assertEquals("Permission denied", exception.getMessage());
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
        verify(notificationRepository, never()).save(any());
    }

    @Test
<<<<<<< HEAD
    @DisplayName("Should mark all as read")
    void testMarkAllAsRead() {
        when(notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(notification));

        notificationService.markAllAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).saveAll(anyList());
=======
    void markAllAsRead_shouldUpdateUnreadNotifications() {
        User user = new User();
        user.setId(1L);

        Notification first = new Notification();
        first.setIsRead(false);
        Notification second = new Notification();
        second.setIsRead(false);

        when(notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(first, second));

        notificationService.markAllAsRead(1L);

        assertTrue(first.getIsRead());
        assertTrue(second.getIsRead());
        verify(notificationRepository).saveAll(List.of(first, second));
>>>>>>> 961fbb9dbae68a517579a9650f62da364314536b
    }
}

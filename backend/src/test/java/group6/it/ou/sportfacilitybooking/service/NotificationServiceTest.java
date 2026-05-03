package group6.it.ou.sportfacilitybooking.service;

import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.mapper.NotificationMapper;
import group6.it.ou.sportfacilitybooking.repository.NotificationRepository;
import group6.it.ou.sportfacilitybooking.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

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

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
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
        verify(notificationRepository, never()).save(any());
    }

    @Test
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
    }
}

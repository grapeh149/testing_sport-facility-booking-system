import apiClient from './api';

const notificationService = {
  getLatestNotifications: () => apiClient.get('/api/notifications/latest'),
  getUnreadCount: () => apiClient.get('/api/notifications/unread-count'),
  markAsRead: (id) => apiClient.put(`/api/notifications/${id}/read`),
  markAllAsRead: () => apiClient.put('/api/notifications/read-all'),
};

export default notificationService;

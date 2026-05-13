import notificationService from '../notificationService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    put: jest.fn(),
  },
}));

describe('notificationService', () => {
  test('maps latest and read endpoints', () => {
    notificationService.getLatestNotifications();
    notificationService.getUnreadCount();
    notificationService.markAsRead(4);
    notificationService.markAllAsRead();

    expect(apiClient.get).toHaveBeenCalledWith('/api/notifications/latest');
    expect(apiClient.get).toHaveBeenCalledWith('/api/notifications/unread-count');
    expect(apiClient.put).toHaveBeenCalledWith('/api/notifications/4/read');
    expect(apiClient.put).toHaveBeenCalledWith('/api/notifications/read-all');
  });
});
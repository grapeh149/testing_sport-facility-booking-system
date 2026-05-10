import userService from '../userService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
  },
}));

describe('userService', () => {
  test('uses profile and password endpoints', () => {
    userService.getProfile();
    userService.updateProfile({ fullName: 'Updated' });
    userService.updatePassword({ oldPassword: 'old', newPassword: 'new' });

    expect(apiClient.get).toHaveBeenCalledWith('/api/auth/profile');
    expect(apiClient.put).toHaveBeenCalledWith('/api/auth/profile', { fullName: 'Updated' });
    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/update-password', { oldPassword: 'old', newPassword: 'new' });
  });
});
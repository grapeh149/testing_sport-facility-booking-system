import authService from '../authService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    post: jest.fn(),
  },
}));

describe('authService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('forwards auth requests to the expected endpoints', () => {
    authService.register({ email: 'a@test.com' });
    authService.login('a@test.com', '123456');
    authService.refreshToken();
    authService.forgotPassword('a@test.com');
    authService.resetPassword('token', 'newpass');

    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/register', { email: 'a@test.com' });
    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/login', { email: 'a@test.com', password: '123456' });
    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/refresh-token');
    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/forgot-password', { email: 'a@test.com' });
    expect(apiClient.post).toHaveBeenCalledWith('/api/auth/reset-password', { token: 'token', password: 'newpass' });
  });

  test('logout clears persisted auth data', () => {
    localStorage.setItem('token', 'abc');
    localStorage.setItem('user', '{}');

    authService.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
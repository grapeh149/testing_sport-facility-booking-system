import apiClient from './api';

const authService = {
  // Đăng nhập
  login: (email, password) => {
    return apiClient.post('/api/auth/login', { email, password });
  },

  // Đăng ký
  register: (data) => {
    return apiClient.post('/api/auth/register', data);
  },

  // Đăng xuất
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  // Làm mới token
  refreshToken: () => {
    return apiClient.post('/api/auth/refresh-token');
  },

  // Quên mật khẩu
  forgotPassword: (email) => {
    return apiClient.post('/api/auth/forgot-password', { email });
  },

  // Đặt lại mật khẩu
  resetPassword: (token, password) => {
    return apiClient.post('/api/auth/reset-password', { token, password });
  },
};

export default authService;

import apiClient from './api';

const authService = {
  // Đăng ký
  register: (data) => {
    return apiClient.post('/api/auth/register', data);
  },

  // Đăng nhập
  login: (email, password) => {
    return apiClient.post('/api/auth/login', { email, password });
  },
};

export default authService;
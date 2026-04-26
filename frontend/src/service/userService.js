import apiClient from './api';

const userService = {
  // Lấy thông tin profile
  getProfile: () => {
    return apiClient.get('/api/auth/profile');
  },

  // Cập nhật profile
  updateProfile: (data) => {
    return apiClient.put('/api/auth/profile', data);
  },

  // Cập nhật mật khẩu
  updatePassword: (data) => {
    return apiClient.post('/api/auth/update-password', data);
  },
};

export default userService;

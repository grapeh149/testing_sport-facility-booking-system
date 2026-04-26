import apiClient from './api';

const timeslotService = {
  // Lấy danh sách khung giờ của sân
  getTimeslotsByCourt: (courtId) => {
    return apiClient.get(`/api/timeslots/court/${courtId}`);
  },

  // Lấy chi tiết khung giờ
  getTimeslotDetail: (id) => {
    return apiClient.get(`/api/timeslots/${id}`);
  },

  // Tạo khung giờ (yêu cầu authentication)
  createTimeslot: (data) => {
    return apiClient.post('/api/timeslots', data);
  },

  // Cập nhật khung giờ (yêu cầu authentication)
  updateTimeslot: (id, data) => {
    return apiClient.put(`/api/timeslots/${id}`, data);
  },

  // Xóa khung giờ (yêu cầu authentication)
  deleteTimeslot: (id) => {
    return apiClient.delete(`/api/timeslots/${id}`);
  },
};

export default timeslotService;

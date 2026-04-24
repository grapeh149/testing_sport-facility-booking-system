import apiClient from './api';

const bookingService = {
  // Lấy danh sách booking của user (yêu cầu authentication)
  getMyBookings: (page = 0, size = 10) => {
    return apiClient.get('/api/bookings/my-bookings', {
      params: { page, size },
    });
  },

  // Lấy danh sách tất cả booking của owner (yêu cầu authentication)
  getOwnerAllBookings: (page = 0, size = 100) => {
    return apiClient.get('/api/bookings/owner/all-bookings', {
      params: { page, size },
    });
  },

  // Lấy chi tiết booking (yêu cầu authentication)
  getBookingDetail: (id) => {
    return apiClient.get(`/api/bookings/${id}`);
  },

  // Lấy lịch đặt sân công khai theo courtId
  getCourtBookings: (courtId) => {
    return apiClient.get(`/api/bookings/court/${courtId}`);
  },

  // Tạo booking (yêu cầu authentication)
  createBooking: (data) => {
    return apiClient.post('/api/bookings', data);
  },

  // Cập nhật booking (yêu cầu authentication)
  updateBooking: (id, data) => {
    return apiClient.put(`/api/bookings/${id}`, data);
  },

  // Xác nhận booking
  confirmBooking: (id, ownerId) => {
    return apiClient.post(`/api/bookings/${id}/confirm`, {}, {
      params: { ownerId }
    });
  },

  // Hủy booking với lý do
  cancelBookingWithReason: (id, reason) => {
    return apiClient.post(`/api/bookings/${id}/cancel`, {}, {
      params: { reason }
    });
  },

  // Hủy booking (yêu cầu authentication)
  cancelBooking: (id) => {
    return apiClient.delete(`/api/bookings/${id}`);
  },
};

export default bookingService;

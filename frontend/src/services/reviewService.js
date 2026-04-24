import apiClient from './api';

const reviewService = {
  // Lấy danh sách review của sân
  getReviewsByFacility: (facilityId, page = 0, size = 10) => {
    return apiClient.get(`/api/reviews/facility/${facilityId}`, {
      params: { page, size },
    });
  },

  // Lấy chi tiết review (yêu cầu authentication)
  getReviewDetail: (id) => {
    return apiClient.get(`/api/reviews/${id}`);
  },

  // Tạo review (yêu cầu authentication)
  createReview: (bookingId, data) => {
    return apiClient.post('/api/reviews', data, {
      params: { bookingId },
    });
  },

  // Cập nhật review (yêu cầu authentication)
  updateReview: (id, data) => {
    return apiClient.put(`/api/reviews/${id}`, data);
  },

  // Xóa review (yêu cầu authentication)
  deleteReview: (id) => {
    return apiClient.delete(`/api/reviews/${id}`);
  },

  // Phản hồi review (cho owner)
  replyToReview: (reviewId, reply) => {
    return apiClient.put(`/api/reviews/${reviewId}/reply`, {}, {
      params: { reply },
    });
  },
};

export default reviewService;

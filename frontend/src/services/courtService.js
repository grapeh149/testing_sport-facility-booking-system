import apiClient from './api';

const courtService = {
  // Lấy danh sách sân
  getCourts: (page = 0, size = 10, facilityId = null) => {
    const params = { page, size };
    if (facilityId) {
      params.facilityId = facilityId;
    }
    return apiClient.get('/api/courts', { params });
  },

  // Lấy chi tiết sân
  getCourtDetail: (id) => {
    return apiClient.get(`/api/courts/${id}`);
  },

  // Lấy danh sách sân của một sở thể thao
  getCourtsByFacility: (facilityId) => {
    return apiClient.get(`/api/courts/facility/${facilityId}`);
  },

  // Lấy sân theo sport type
  getCourtsBySportType: (sportTypeId, page = 0, size = 10) => {
    return apiClient.get(`/api/courts/sport-type/${sportTypeId}`, {
      params: { page, size },
    });
  },

  // Tìm kiếm sân
  searchCourts: (query, page = 0, size = 10) => {
    return apiClient.get('/api/courts/search', {
      params: { q: query, page, size },
    });
  },

  // Tạo sân (yêu cầu authentication)
  createCourt: (data) => {
    return apiClient.post('/api/courts', data);
  },

  // Cập nhật sân (yêu cầu authentication)
  updateCourt: (id, data) => {
    return apiClient.put(`/api/courts/${id}`, data);
  },

  // Xóa sân (yêu cầu authentication)
  deleteCourt: (id) => {
    return apiClient.delete(`/api/courts/${id}`);
  },

  // Lấy giá sân
  getCourtPrice: (id) => {
    return apiClient.get(`/api/courts/${id}/price`);
  },

  // Cập nhật giá sân
  updateCourtPrice: (id, price) => {
    return apiClient.put(`/api/courts/${id}/price`, { price });
  },
};

export default courtService;

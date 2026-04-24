import apiClient from './api';

const courtService = {
  // Lấy danh sách sân của một cơ sở
  getCourtsByFacility: (facilityId) => {
    return apiClient.get(`/api/courts/facility/${facilityId}`);
  },

  // Lấy chi tiết sân
  getCourtDetail: (id) => {
    return apiClient.get(`/api/courts/${id}`);
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

  // Tìm kiếm sân với bộ lọc (địa chỉ, loại thể thao)
  searchCourts: (address = '', sportTypeId = null) => {
    return apiClient.get('/api/courts/search', {
      params: { 
        address: address || null, 
        sportTypeId: sportTypeId || null 
      },
    });
  },
};

export default courtService;

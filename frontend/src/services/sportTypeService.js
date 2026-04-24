import apiClient from './api';

const sportTypeService = {
  // Lấy danh sách loại thể thao (chỉ những loại đang hoạt động)
  getActiveSportTypes: () => {
    return apiClient.get('/api/sport-types');
  },

  // Lấy toàn bộ loại thể thao
  getAllSportTypes: () => {
    return apiClient.get('/api/sport-types/all');
  },

  // Lấy chi tiết loại thể thao
  getSportTypeById: (id) => {
    return apiClient.get(`/api/sport-types/${id}`);
  },

  // Tạo loại thể thao mới
  createSportType: (sportTypeData) => {
    return apiClient.post('/api/sport-types', sportTypeData);
  },

  // Cập nhật loại thể thao
  updateSportType: (id, sportTypeData) => {
    return apiClient.put(`/api/sport-types/${id}`, sportTypeData);
  },
};

export default sportTypeService;

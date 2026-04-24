import apiClient from './api';

const facilityService = {
  // Lấy danh sách sân
  getFacilities: (page = 0, size = 10, search = '') => {
    return apiClient.get('/api/facilities', {
      params: { page, size, search },
    });
  },

  // Lấy chi tiết sân
  getFacilityDetail: (id) => {
    return apiClient.get(`/api/facilities/${id}`);
  },

  // Lấy danh sách sân theo chủ sân
  getOwnerFacilities: (ownerId) => {
    return apiClient.get(`/api/facilities/owner/${ownerId}`);
  },

  // Tạo sân (yêu cầu authentication)
  createFacility: (data, ownerId) => {
    return apiClient.post('/api/facilities', data, {
      params: { ownerId }
    });
  },

  // Cập nhật sân (yêu cầu authentication)
  updateFacility: (id, data) => {
    return apiClient.put(`/api/facilities/${id}`, data);
  },

  // Cập nhật ảnh bìa sân
  updateFacilityCoverImage: (id, coverImageUrl) => {
    return apiClient.patch(`/api/facilities/${id}/cover-image`, {
      coverImageUrl,
    });
  },

  // Xóa sân (yêu cầu authentication)
  deleteFacility: (id) => {
    return apiClient.delete(`/api/facilities/${id}`);
  },

  // Lấy danh sách ảnh cho một sân
  getFacilityImages: (facilityId) => {
    return apiClient.get(`/api/facility-images/facility/${facilityId}`);
  },

  // Thêm ảnh cho sân
  addFacilityImage: (facilityId, imageUrl, sortOrder) => {
    return apiClient.post(`/api/facility-images/facility/${facilityId}`, {
      imageUrl,
      sortOrder
    });
  },

  // Thêm nhiều ảnh cho sân
  addFacilityImages: (facilityId, imageUrls) => {
    return apiClient.post(`/api/facility-images/facility/${facilityId}/bulk`, {
      imageUrls
    });
  },

  // Xóa ảnh
  deleteFacilityImage: (imageId) => {
    return apiClient.delete(`/api/facility-images/${imageId}`);
  },

  // Xóa tất cả ảnh của một sân
  deleteAllFacilityImages: (facilityId) => {
    return apiClient.delete(`/api/facility-images/facility/${facilityId}/all`);
  },
};

export default facilityService;

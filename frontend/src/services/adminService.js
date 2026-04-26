import apiClient from './api';

const adminService = {
  // Facility Approval - Using correct backend endpoints
  approveFacility: (facilityId, adminId) => {
    // POST method instead of PUT, /api/facilities instead of /api/admin/facilities
    return apiClient.post(`/api/facilities/${facilityId}/approve?adminId=${adminId}`);
  },

  rejectFacility: (facilityId, reason, adminId) => {
    // POST method, /api/facilities endpoint, with reason and adminId parameters
    return apiClient.post(`/api/facilities/${facilityId}/reject?adminId=${adminId}&reason=${encodeURIComponent(reason)}`);
  },

  getPendingFacilities: () => {
    // Get all facilities - backend doesn't have pending filter yet, will show all
    return apiClient.get('/api/facilities', { params: { page: 0, size: 100 } });
  },

  getAllFacilities: (page = 0, size = 10) => {
    return apiClient.get('/api/facilities/admin/all', { params: { page, size } });
  },

  // User Management - Use /api/users endpoint (with authentication)
  getAllUsers: (page = 0, size = 10) => {
    return apiClient.get('/api/users', { params: { page, size } });
  },

  createUser: (userData) => {
    return apiClient.post('/api/users', userData);
  },

  updateUser: (userId, userData) => {
    return apiClient.put(`/api/users/${userId}`, userData);
  },

  deleteUser: (userId) => {
    return apiClient.delete(`/api/users/${userId}`);
  },

  lockUser: (userId) => {
    return apiClient.put(`/api/users/${userId}/lock`);
  },

  unlockUser: (userId) => {
    return apiClient.put(`/api/users/${userId}/unlock`);
  },

  // Reports
  getAllReports: (page = 0, size = 10) => {
    return apiClient.get('/api/admin/reports', { params: { page, size } });
  },

  getReportDetail: (reportId) => {
    return apiClient.get(`/api/admin/reports/${reportId}`);
  },

  updateReportStatus: (reportId, status) => {
    return apiClient.put(`/api/admin/reports/${reportId}`, { status });
  },

  // Dashboard Stats
  getDashboardStats: () => {
    return apiClient.get('/api/admin/stats');
  },
};

export default adminService;

import adminService from '../adminService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

describe('adminService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('calls approval and rejection endpoints with expected query params', () => {
    adminService.approveFacility(7, 3);
    adminService.rejectFacility(7, 'Sai quy định', 3);

    expect(apiClient.post).toHaveBeenCalledWith('/api/facilities/7/approve?adminId=3');
    expect(apiClient.post).toHaveBeenCalledWith(
      '/api/facilities/7/reject?adminId=3&reason=Sai%20quy%20%C4%91%E1%BB%8Bnh'
    );
  });

  test('calls user management endpoints', () => {
    adminService.getAllUsers(2, 20);
    adminService.lockUser(9);
    adminService.unlockUser(9);

    expect(apiClient.get).toHaveBeenCalledWith('/api/users', { params: { page: 2, size: 20 } });
    expect(apiClient.put).toHaveBeenCalledWith('/api/users/9/lock');
    expect(apiClient.put).toHaveBeenCalledWith('/api/users/9/unlock');
  });

  test('calls getPendingFacilities endpoint', () => {
    adminService.getPendingFacilities();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities', { params: { page: 0, size: 100 } });
  });

  test('calls getAllFacilities endpoint with pagination', () => {
    adminService.getAllFacilities(1, 15);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities/admin/all', { params: { page: 1, size: 15 } });
  });

  test('calls getAllFacilities endpoint with default pagination', () => {
    adminService.getAllFacilities();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities/admin/all', { params: { page: 0, size: 10 } });
  });

  test('calls createUser endpoint', () => {
    const userData = { email: 'newuser@test.com', password: '123456', fullName: 'New User' };
    adminService.createUser(userData);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/users', userData);
  });

  test('calls updateUser endpoint', () => {
    const userData = { fullName: 'Updated User', email: 'updated@test.com' };
    adminService.updateUser(5, userData);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/users/5', userData);
  });

  test('calls deleteUser endpoint', () => {
    adminService.deleteUser(10);
    
    expect(apiClient.delete).toHaveBeenCalledWith('/api/users/10');
  });

  test('calls getAllReports endpoint with pagination', () => {
    adminService.getAllReports(2, 15);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/admin/reports', { params: { page: 2, size: 15 } });
  });

  test('calls getAllReports endpoint with default pagination', () => {
    adminService.getAllReports();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/admin/reports', { params: { page: 0, size: 10 } });
  });

  test('calls getReportDetail endpoint', () => {
    adminService.getReportDetail(25);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/admin/reports/25');
  });

  test('calls updateReportStatus endpoint', () => {
    adminService.updateReportStatus(25, 'RESOLVED');
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/admin/reports/25', { status: 'RESOLVED' });
  });

  test('calls getDashboardStats endpoint', () => {
    adminService.getDashboardStats();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/admin/stats');
  });

  test('approveFacility with different adminId', () => {
    adminService.approveFacility(42, 99);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/facilities/42/approve?adminId=99');
  });

  test('rejectFacility with special characters in reason', () => {
    const reason = 'Not compliant with &legal requirements';
    adminService.rejectFacility(15, reason, 5);
    
    expect(apiClient.post).toHaveBeenCalledWith(
      `/api/facilities/15/reject?adminId=5&reason=${encodeURIComponent(reason)}`
    );
  });

  test('lockUser endpoint with different userId', () => {
    adminService.lockUser(42);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/users/42/lock');
  });

  test('unlockUser endpoint with different userId', () => {
    adminService.unlockUser(42);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/users/42/unlock');
  });
});
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';

// Tạo Axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

// Request interceptor - Thêm JWT token vào header
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');

    // Let browser set multipart boundaries automatically for FormData requests.
    if (config.data instanceof FormData) {
      if (config.headers) {
        delete config.headers['Content-Type'];
        delete config.headers['content-type'];
      }
    } else {
      config.headers = config.headers || {};
      if (!config.headers['Content-Type'] && !config.headers['content-type']) {
        config.headers['Content-Type'] = 'application/json';
      }
    }
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Xử lý lỗi 401
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token hết hạn hoặc không hợp lệ
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;

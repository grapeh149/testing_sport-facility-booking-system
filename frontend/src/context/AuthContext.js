import React, { createContext, useState, useEffect } from 'react';
import authService from '../services/authService';
import userService from '../services/userService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Khôi phục thông tin user từ localStorage khi app load
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      const parsedUser = JSON.parse(storedUser);
      setUser(parsedUser);
      
      // Fetch fresh profile data to ensure everything (especially avatar) is up to date
      refreshUser(storedToken);
    }

    setLoading(false);
  }, []);

  const refreshUser = async (explicitToken) => {
    const currentToken = explicitToken || token || localStorage.getItem('token');
    if (!currentToken) return;

    try {
      const response = await userService.getProfile();
      if (response.data.success) {
        const freshData = response.data.data;
        // Đảm bảo mapping cả 'id' từ DTO sang 'userId' mà app đang dùng
        const updatedUser = { ...freshData, userId: freshData.id };
        // Sync with localStorage
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
      }
    } catch (error) {
      console.error('Failed to refresh user profile:', error);
    }
  };

  // Đăng ký
  const register = async (email, password, fullName, phone) => {
    try {
      const response = await authService.register({
        email,
        password,
        fullName,
        phone,
      });

      const { token, userId, fullName: name, role, avatarUrl } = response.data.data;
      const userData = { userId, email, fullName: name, phone, role, avatarUrl };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      setToken(token);
      setUser(userData);

      return { success: true, user: userData };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.data || 'Đăng ký thất bại',
        message: error.response?.data?.message || 'Đã xảy ra lỗi',
      };
    }
  };

  // Đăng nhập
  const login = async (email, password) => {
    try {
      const response = await authService.login(email, password);

      const { token, userId, fullName, role, avatarUrl } = response.data.data;
      const userData = { userId, email, fullName, role, avatarUrl };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      setToken(token);
      setUser(userData);

      return { success: true, user: userData };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.data || 'Đăng nhập thất bại',
        message: error.response?.data?.message || 'Email hoặc mật khẩu không đúng',
      };
    }
  };

  // Đăng xuất
  const logout = () => {
    authService.logout();
    setToken(null);
    setUser(null);
  };

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!token,
    register,
    login,
    logout,
    refreshUser,
  };

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
};

// Hook để sử dụng AuthContext
export const useAuth = () => {
  const context = React.useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth phải được sử dụng trong AuthProvider');
  }
  return context;
};

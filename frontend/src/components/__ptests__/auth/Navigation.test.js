import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Navigation from '../../Navigation';
import notificationService from '../../../services/notificationService';

jest.mock('../../../services/notificationService');

jest.mock('../../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: jest.fn(),
}));

const { useAuth } = require('../../../context/AuthContext');
const { useNavigate } = require('react-router-dom');

const mockNavigate = jest.fn();

const setupAuthenticated = (user = { role: 'ADMIN', fullName: 'Admin User' }) => {
  const logout = jest.fn();
  useAuth.mockReturnValue({
    isAuthenticated: true,
    user,
    logout,
  });
  useNavigate.mockReturnValue(mockNavigate);

  notificationService.getLatestNotifications.mockResolvedValue({
    data: { success: true, data: [] },
  });
  notificationService.getUnreadCount.mockResolvedValue({
    data: { success: true, data: 2 },
  });

  render(
    <MemoryRouter>
      <Navigation />
    </MemoryRouter>
  );

  return { logout };
};

describe('Navigation', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    useAuth.mockReset();
    useNavigate.mockReset();
    notificationService.getLatestNotifications.mockReset();
    notificationService.getUnreadCount.mockReset();
    notificationService.markAsRead.mockReset();
    notificationService.markAllAsRead.mockReset();
  });

  test('shows login and register links for anonymous user', () => {
    useAuth.mockReturnValue({ isAuthenticated: false, user: null, logout: jest.fn() });
    useNavigate.mockReturnValue(mockNavigate);

    render(
      <MemoryRouter>
        <Navigation />
      </MemoryRouter>
    );

    expect(screen.getByText('Đăng nhập')).toBeInTheDocument();
    expect(screen.getByText('Đăng ký')).toBeInTheDocument();
  });

  test('loads notifications and shows admin link for authenticated user', async () => {
    setupAuthenticated();

    await waitFor(() => {
      expect(notificationService.getLatestNotifications).toHaveBeenCalled();
      expect(notificationService.getUnreadCount).toHaveBeenCalled();
    });

    expect(screen.getByText('Admin')).toBeInTheDocument();
    expect(screen.getByText('Đặt sân của tôi')).toBeInTheDocument();
  });

  test('logout button clears auth state and navigates to login', async () => {
    const { logout } = setupAuthenticated();

    await waitFor(() => expect(notificationService.getUnreadCount).toHaveBeenCalled());

    fireEvent.click(screen.getByRole('button', { name: /Admin User/i }));
    const logoutItem = await screen.findByText('Đăng xuất');
    fireEvent.click(logoutItem);

    expect(logout).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});
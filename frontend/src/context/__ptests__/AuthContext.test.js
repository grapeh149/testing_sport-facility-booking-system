import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import { AuthProvider, useAuth } from '../AuthContext';

jest.mock('../../services/authService', () => ({
  register: jest.fn(),
  login: jest.fn(),
  logout: jest.fn(),
}));

jest.mock('../../services/userService', () => ({
  getProfile: jest.fn(),
}));

const authService = require('../../services/authService');
const userService = require('../../services/userService');

const renderWithProvider = () => {
  let contextValue;

  const Consumer = () => {
    contextValue = useAuth();
    return (
      <div>
        <div data-testid="loading">{contextValue.loading ? 'loading' : 'ready'}</div>
        <div data-testid="authenticated">{contextValue.isAuthenticated ? 'yes' : 'no'}</div>
        <div data-testid="name">{contextValue.user?.fullName || ''}</div>
      </div>
    );
  };

  render(
    <AuthProvider>
      <Consumer />
    </AuthProvider>
  );

  return { contextValue };
};

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
    authService.logout.mockImplementation(() => {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    });
  });

  test('restores stored session and refreshes profile', async () => {
    localStorage.setItem('token', 'stored-token');
    localStorage.setItem(
      'user',
      JSON.stringify({ userId: 1, fullName: 'Stored User', role: 'CUSTOMER' })
    );

    userService.getProfile.mockResolvedValue({
      data: {
        success: true,
        data: { id: 77, fullName: 'Fresh User', email: 'fresh@test.com' },
      },
    });

    renderWithProvider();

    await waitFor(() => expect(userService.getProfile).toHaveBeenCalled());
    await waitFor(() => expect(screen.getByTestId('loading')).toHaveTextContent('ready'));
    expect(screen.getByTestId('authenticated')).toHaveTextContent('yes');
    await waitFor(() => expect(screen.getByTestId('name')).toHaveTextContent('Fresh User'));
    expect(JSON.parse(localStorage.getItem('user')).userId).toBe(77);
  });

  test('login stores token and user data', async () => {
    authService.login.mockResolvedValue({
      data: {
        data: {
          token: 'token-123',
          userId: 9,
          fullName: 'Logged In',
          role: 'ADMIN',
        },
      },
    });

    const { contextValue } = renderWithProvider();

    await act(async () => {
      await contextValue.login('admin@test.com', '123456');
    });

    expect(authService.login).toHaveBeenCalledWith('admin@test.com', '123456');
    expect(localStorage.getItem('token')).toBe('token-123');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('yes');
  });

  test('logout clears storage and auth state', async () => {
    authService.login.mockResolvedValue({
      data: {
        data: {
          token: 'token-123',
          userId: 9,
          fullName: 'Logged In',
          role: 'ADMIN',
        },
      },
    });

    const { contextValue } = renderWithProvider();

    await act(async () => {
      await contextValue.login('admin@test.com', '123456');
    });

    act(() => {
      contextValue.logout();
    });

    expect(authService.logout).toHaveBeenCalled();
    expect(localStorage.getItem('token')).toBeNull();
    expect(screen.getByTestId('authenticated')).toHaveTextContent('no');
  });
});
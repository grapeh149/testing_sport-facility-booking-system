import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Login from '../../Login';

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

const renderLogin = (loginResult = { success: true, user: { role: 'CUSTOMER' } }) => {
  const login = jest.fn().mockResolvedValue(loginResult);
  useAuth.mockReturnValue({ login });
  useNavigate.mockReturnValue(mockNavigate);

  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  return { login };
};

describe('Login', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    useAuth.mockReset();
    useNavigate.mockReset();
  });

  test('shows validation errors when submitting empty form', async () => {
    renderLogin();

    fireEvent.click(screen.getByRole('button', { name: 'Sign In' }));

    expect(await screen.findByText('Email không được để trống')).toBeInTheDocument();
    expect(screen.getAllByText('Mật khẩu không được để trống').length).toBeGreaterThan(0);
  });

  test('submits credentials and navigates by role', async () => {
    const { login } = renderLogin({ success: true, user: { role: 'ADMIN' } });

    fireEvent.change(screen.getByPlaceholderText('Nhập email'), {
      target: { name: 'email', value: 'admin@test.com' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), {
      target: { name: 'password', value: '123456' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith('admin@test.com', '123456');
    });
    expect(mockNavigate).toHaveBeenCalledWith('/admin/dashboard');
  });

  test('toggles password visibility', () => {
    renderLogin();

    const passwordInput = screen.getByPlaceholderText('Nhập mật khẩu');
    expect(passwordInput).toHaveAttribute('type', 'password');

    fireEvent.click(screen.getByRole('button', { name: 'Hiện mật khẩu' }));
    expect(screen.getByPlaceholderText('Nhập mật khẩu')).toHaveAttribute('type', 'text');
  });
});
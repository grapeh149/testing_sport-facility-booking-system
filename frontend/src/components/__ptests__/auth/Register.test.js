import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Register from '../../Register';

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

const renderRegister = (registerResult = { success: true }) => {
  const register = jest.fn().mockResolvedValue(registerResult);
  useAuth.mockReturnValue({ register });
  useNavigate.mockReturnValue(mockNavigate);

  render(
    <MemoryRouter>
      <Register />
    </MemoryRouter>
  );

  return { register };
};

describe('Register', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    useAuth.mockReset();
    useNavigate.mockReset();
  });

  test('shows validation errors when submitting empty form', async () => {
    renderRegister();

    fireEvent.click(screen.getByRole('button', { name: 'Đăng ký tài khoản' }));

    expect(await screen.findByText('Email không được để trống')).toBeInTheDocument();
    expect(screen.getAllByText('Vui lòng xác nhận mật khẩu').length).toBeGreaterThan(0);
    expect(screen.getByText('Tên đầy đủ không được để trống')).toBeInTheDocument();
    expect(screen.getByText('Số điện thoại không được để trống')).toBeInTheDocument();
  });

  test('submits registration data and navigates home on success', async () => {
    const { register } = renderRegister({ success: true });

    fireEvent.change(screen.getByPlaceholderText('Nhập email'), {
      target: { name: 'email', value: 'new@test.com' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập tên đầy đủ'), {
      target: { name: 'fullName', value: 'New User' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập số điện thoại'), {
      target: { name: 'phone', value: '0123456789' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), {
      target: { name: 'password', value: '123456' },
    });
    fireEvent.change(screen.getByPlaceholderText('Xác nhận mật khẩu'), {
      target: { name: 'confirmPassword', value: '123456' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Đăng ký tài khoản' }));

    await waitFor(() => {
      expect(register).toHaveBeenCalledWith(
        'new@test.com',
        '123456',
        'New User',
        '0123456789'
      );
    });
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('shows password mismatch error', async () => {
    renderRegister();

    fireEvent.change(screen.getByPlaceholderText('Nhập email'), {
      target: { name: 'email', value: 'new@test.com' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập tên đầy đủ'), {
      target: { name: 'fullName', value: 'New User' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập số điện thoại'), {
      target: { name: 'phone', value: '0123456789' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), {
      target: { name: 'password', value: '123456' },
    });
    fireEvent.change(screen.getByPlaceholderText('Xác nhận mật khẩu'), {
      target: { name: 'confirmPassword', value: '654321' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Đăng ký tài khoản' }));

    expect(await screen.findAllByText('Mật khẩu không trùng khớp')).toHaveLength(2);
  });
});
import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ChangePassword from '../../ChangePassword';
import userService from '../../../services/userService';

jest.mock('../../../services/userService');

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

const renderChangePassword = () => {
  const logout = jest.fn();
  useAuth.mockReturnValue({ logout });
  useNavigate.mockReturnValue(mockNavigate);

  render(
    <MemoryRouter>
      <ChangePassword />
    </MemoryRouter>
  );

  return { logout };
};

describe('ChangePassword', () => {
  beforeEach(() => {
    jest.useFakeTimers();
    mockNavigate.mockClear();
    useAuth.mockReset();
    useNavigate.mockReset();
    userService.updatePassword.mockReset();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
    jest.clearAllMocks();
  });

  test('shows validation error when submitting empty form', () => {
    renderChangePassword();

    fireEvent.click(screen.getByRole('button', { name: 'Đổi mật khẩu' }));

    expect(screen.getByText('Mật khẩu cũ không được để trống')).toBeInTheDocument();
  });

  test('updates password and navigates to login on success', async () => {
    const { logout } = renderChangePassword();
    userService.updatePassword.mockResolvedValue({ data: { success: true } });

    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu hiện tại'), {
      target: { name: 'oldPassword', value: 'oldpass' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu mới'), {
      target: { name: 'newPassword', value: 'newpass1' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập lại mật khẩu mới'), {
      target: { name: 'confirmPassword', value: 'newpass1' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Đổi mật khẩu' }));

    await waitFor(() => {
      expect(userService.updatePassword).toHaveBeenCalledWith({
        oldPassword: 'oldpass',
        newPassword: 'newpass1',
        confirmPassword: 'newpass1',
      });
    });

    expect(
      await screen.findByText('Đổi mật khẩu thành công! Vui lòng đăng nhập lại.')
    ).toBeInTheDocument();

    act(() => {
      jest.runAllTimers();
    });

    expect(logout).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  test('shows backend error message when update fails', async () => {
    renderChangePassword();
    userService.updatePassword.mockResolvedValue({ data: { success: false, message: 'Sai mật khẩu cũ' } });

    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu hiện tại'), {
      target: { name: 'oldPassword', value: 'wrong' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu mới'), {
      target: { name: 'newPassword', value: 'newpass1' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập lại mật khẩu mới'), {
      target: { name: 'confirmPassword', value: 'newpass1' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Đổi mật khẩu' }));

    expect(await screen.findByText('Sai mật khẩu cũ')).toBeInTheDocument();
  });
});
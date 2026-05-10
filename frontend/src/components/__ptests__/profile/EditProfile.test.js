import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EditProfile from '../../EditProfile';
import userService from '../../../services/userService';
import cloudinaryService from '../../../services/cloudinaryService';

jest.mock('../../../services/userService');
jest.mock('../../../services/cloudinaryService');

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

const renderEditProfile = (user = { userId: 1, fullName: 'Test User' }) => {
  const refreshUser = jest.fn();
  useAuth.mockReturnValue({ user, refreshUser });
  useNavigate.mockReturnValue(mockNavigate);

  render(
    <MemoryRouter>
      <EditProfile />
    </MemoryRouter>
  );

  return { refreshUser };
};

describe('EditProfile', () => {
  beforeEach(() => {
    jest.useFakeTimers();
    mockNavigate.mockClear();
    useAuth.mockReset();
    useNavigate.mockReset();
    userService.getProfile.mockReset();
    userService.updateProfile.mockReset();
    cloudinaryService.uploadFacilityImage.mockReset();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
    jest.clearAllMocks();
  });

  test('redirects to login when user is missing', () => {
    renderEditProfile(null);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  test('loads profile data into the form', async () => {
    userService.getProfile.mockResolvedValue({
      data: {
        success: true,
        data: {
          fullName: 'Test User',
          username: 'testuser',
          phone: '0123456789',
          avatarUrl: 'https://example.com/avatar.jpg',
        },
      },
    });

    renderEditProfile();

    await waitFor(() => expect(screen.getByDisplayValue('Test User')).toBeInTheDocument());
    expect(screen.getByDisplayValue('testuser')).toBeInTheDocument();
    expect(screen.getByDisplayValue('0123456789')).toBeInTheDocument();
  });

  test('saves profile and navigates back after success', async () => {
    userService.getProfile.mockResolvedValue({
      data: {
        success: true,
        data: {
          fullName: 'Test User',
          username: 'testuser',
          phone: '0123456789',
          avatarUrl: '',
        },
      },
    });
    userService.updateProfile.mockResolvedValue({ data: { success: true } });

    const { refreshUser } = renderEditProfile();

    await waitFor(() => expect(screen.getByDisplayValue('Test User')).toBeInTheDocument());

    fireEvent.change(screen.getByPlaceholderText('Nhập tên đầy đủ'), {
      target: { name: 'fullName', value: 'Updated User' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Lưu thay đổi' }));

    await waitFor(() => {
      expect(userService.updateProfile).toHaveBeenCalledWith(
        expect.objectContaining({ fullName: 'Updated User' })
      );
    });
    expect(await screen.findByText('Cập nhật profile thành công!')).toBeInTheDocument();

    act(() => {
      jest.runAllTimers();
    });

    expect(refreshUser).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith('/profile');
  });
});
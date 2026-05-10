import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EditProfile from './EditProfile';

jest.mock('../services/userService', () => ({
  getProfile: jest.fn(),
  updateProfile: jest.fn()
}));

jest.mock('../services/cloudinaryService', () => ({
  uploadFacilityImage: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1 },
    refreshUser: jest.fn()
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

describe('EditProfile Component', () => {
  test('loads and displays profile form data', async () => {
    const userService = require('../services/userService');
    userService.getProfile.mockImplementation(() => 
      Promise.resolve({
        data: {
          success: true,
          data: {
            fullName: 'Test User',
            username: 'testuser',
            phone: '0123456789',
            avatarUrl: 'https://example.com/avatar.jpg'
          }
        }
      })
    );

    render(
      <MemoryRouter>
        <EditProfile />
      </MemoryRouter>
    );

    // Wait for loading to disappear
    await waitFor(() => expect(screen.queryByRole('status')).not.toBeInTheDocument());

    // Then check for the form
    expect(screen.getByText('Tên đầy đủ')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Test User')).toBeInTheDocument();
    expect(screen.getByDisplayValue('testuser')).toBeInTheDocument();
  });
});

import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import UserProfile from './UserProfile';

const mockedNavigate = jest.fn();

jest.mock('../services/api', () => ({
  __esModule: true,
  default: {
    get: jest.fn()
  }
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1 },
    isAuthenticated: true
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate
}));

describe('UserProfile Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders profile information after loading', async () => {
    const apiClient = require('../services/api').default;
    apiClient.get.mockResolvedValue({
      data: {
        success: true,
        data: {
          fullName: 'Test User',
          username: 'testuser',
          email: 'test@example.com',
          phone: '0123456789',
          role: 'CUSTOMER'
        }
      }
    });

    render(
      <MemoryRouter>
        <UserProfile />
      </MemoryRouter>
    );

    await waitFor(() => expect(apiClient.get).toHaveBeenCalled());
    const userTexts = await screen.findAllByText(/Test User/i);
    expect(userTexts.length).toBeGreaterThan(0);
  });
});

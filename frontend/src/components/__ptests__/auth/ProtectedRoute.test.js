import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ProtectedRoute from '../../ProtectedRoute';

jest.mock('../../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  Navigate: ({ to }) => <div data-testid="navigate" data-to={to} />,
}));

const { useAuth } = require('../../../context/AuthContext');

describe('ProtectedRoute', () => {
  beforeEach(() => {
    useAuth.mockReset();
  });

  test('shows loading spinner while auth is loading', () => {
    useAuth.mockReturnValue({ loading: true, isAuthenticated: false, user: null });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Secret</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  test('redirects to login when not authenticated', () => {
    useAuth.mockReturnValue({ loading: false, isAuthenticated: false, user: null });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Secret</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByTestId('navigate')).toHaveAttribute('data-to', '/login');
  });

  test('redirects to home when required role does not match', () => {
    useAuth.mockReturnValue({
      loading: false,
      isAuthenticated: true,
      user: { role: 'CUSTOMER' },
    });

    render(
      <MemoryRouter>
        <ProtectedRoute requiredRole="ADMIN">
          <div>Secret</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByTestId('navigate')).toHaveAttribute('data-to', '/');
  });

  test('renders children when authenticated and role matches', () => {
    useAuth.mockReturnValue({
      loading: false,
      isAuthenticated: true,
      user: { role: 'ADMIN' },
    });

    render(
      <MemoryRouter>
        <ProtectedRoute requiredRole="ADMIN">
          <div>Secret</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByText('Secret')).toBeInTheDocument();
  });
});
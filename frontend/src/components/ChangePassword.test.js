import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ChangePassword from './ChangePassword';

jest.mock('../services/userService', () => ({
  updatePassword: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    logout: jest.fn()
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

describe('ChangePassword Component', () => {
  test('shows validation error when submitting empty form', () => {
    render(
      <MemoryRouter>
        <ChangePassword />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole('button', { name: /Đổi mật khẩu/i }));

    expect(screen.getByText(/Mật khẩu cũ không được để trống/i)).toBeInTheDocument();
  });
});

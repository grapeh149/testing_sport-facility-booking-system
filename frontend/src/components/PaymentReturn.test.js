import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import PaymentReturn from '../components/PaymentReturn';

jest.mock('../services/bookingService', () => ({
  confirmPayment: jest.fn(),
  getBookingDetail: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1 },
    isAuthenticated: true
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
  useSearchParams: () => [new URLSearchParams({})]
}));

describe('PaymentReturn Component', () => {
  test('should be importable', () => {
    expect(PaymentReturn).toBeDefined();
  });
});

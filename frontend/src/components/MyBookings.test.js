import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import MyBookings from './MyBookings';

jest.mock('../services/bookingService', () => ({
  getMyBookings: jest.fn(),
  cancelBooking: jest.fn()
}));

jest.mock('../services/paymentService', () => ({
  createVNPayPaymentUrl: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { fullName: 'Test User' },
    isAuthenticated: true
  })
}));

describe('MyBookings Component', () => {
  test('renders booking list when bookings are available', async () => {
    const bookingService = require('../services/bookingService');

    bookingService.getMyBookings.mockResolvedValue({
      data: {
        success: true,
        data: {
          content: [
            {
              id: 1,
              bookingCode: 'ABC123',
              facilityName: 'Test Facility',
              courtName: 'Test Court',
              bookingDate: '2025-01-01',
              startTime: '08:00',
              endTime: '09:00',
              totalPrice: 100000,
              depositAmount: 20000,
              status: 'PENDING'
            }
          ],
          totalPages: 1
        }
      }
    });

    render(<MyBookings />);

    await waitFor(() => expect(screen.getByText(/Mã đơn/i)).toBeInTheDocument());
    expect(screen.getByText(/ABC123/i)).toBeInTheDocument();
    expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
  });
});

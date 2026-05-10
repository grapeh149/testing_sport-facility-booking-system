import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import MyBookings from '../../MyBookings';
import bookingService from '../../../services/bookingService';
import paymentService from '../../../services/paymentService';

jest.mock('../../../services/bookingService');
jest.mock('../../../services/paymentService');
jest.mock('../../ReviewModal', () => () => <div data-testid="review-modal" />);

jest.mock('../../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

const { useAuth } = require('../../../context/AuthContext');

const futureDate = new Date();
futureDate.setDate(futureDate.getDate() + 1);
const futureDateStr = futureDate.toISOString().split('T')[0];

const renderBookings = () => {
  useAuth.mockReturnValue({
    user: { fullName: 'Test User' },
    isAuthenticated: true,
  });

  render(
    <MemoryRouter>
      <MyBookings />
    </MemoryRouter>
  );
};

describe('MyBookings', () => {
  beforeEach(() => {
    bookingService.getMyBookings.mockReset();
    bookingService.cancelBooking.mockReset();
    paymentService.createVNPayPaymentUrl.mockReset();
    useAuth.mockReset();
    window.confirm = jest.fn();
    window.alert = jest.fn();
    delete window.location;
    window.location = { origin: 'http://localhost:3000', href: '' };
  });

  test('renders booking information and cancel action', async () => {
    window.confirm.mockReturnValue(true);
    bookingService.getMyBookings.mockResolvedValue({
      data: {
        success: true,
        data: {
          content: [
            {
              id: 1,
              bookingCode: 'ABC123',
              facilityName: 'Test Facility',
              courtName: 'Court A',
              bookingDate: futureDateStr,
              startTime: '08:00',
              endTime: '09:00',
              totalPrice: 100000,
              depositAmount: 20000,
              status: 'PENDING',
            },
          ],
          totalPages: 1,
        },
      },
    });
    bookingService.cancelBooking.mockResolvedValue({ data: { success: true } });

    renderBookings();

    await waitFor(() => expect(screen.getByText(/ABC123/)).toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: 'Hủy đặt sân' }));

    await waitFor(() => expect(bookingService.cancelBooking).toHaveBeenCalledWith(1));
    expect(window.alert).toHaveBeenCalledWith('Hủy đặt sân thành công');
  });

  test('creates VNPay payment URL and redirects', async () => {
    bookingService.getMyBookings.mockResolvedValue({
      data: {
        success: true,
        data: {
          content: [
            {
              id: 2,
              bookingCode: 'PAY01',
              facilityName: 'Test Facility',
              courtName: 'Court B',
              bookingDate: futureDateStr,
              startTime: '10:00',
              endTime: '11:00',
              totalPrice: 120000,
              depositAmount: 24000,
              status: 'PENDING_PAYMENT',
            },
          ],
          totalPages: 1,
        },
      },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValue({
      data: { success: true, data: 'https://pay.test/url' },
    });

    renderBookings();

    await waitFor(() => expect(screen.getByText(/PAY01/)).toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: 'Thanh toán VNPay' }));

    await waitFor(() => {
      expect(paymentService.createVNPayPaymentUrl).toHaveBeenCalledWith(
        2,
        'http://localhost:3000/payment/vnpay-return'
      );
    });
    expect(window.location.href).toBe('https://pay.test/url');
  });
});
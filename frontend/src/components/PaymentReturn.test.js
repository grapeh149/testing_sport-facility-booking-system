import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import PaymentReturn from '../components/PaymentReturn';
import * as bookingService from '../services/bookingService';

// Mock services
jest.mock('../services/bookingService');

// Mock useAuth hook
jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1 },
    isAuthenticated: true
  })
}));

// Mock useSearchParams and useNavigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
  useSearchParams: () => [
    new URLSearchParams({
      vnp_Amount: '30000000',
      vnp_BankCode: 'NCB',
      vnp_BankTranNo: '21331838',
      vnp_CardType: 'ATM',
      vnp_OrderInfo: 'Thanh+toan+dat+san+BOOKING001',
      vnp_PayDate: '20240411144538',
      vnp_ResponseCode: '00',
      vnp_TmnCode: 'TMNCODE',
      vnp_TransactionNo: '13956027',
      vnp_TxnRef: 'BOOKING001'
    })
  ]
}));

describe('PaymentReturn Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should display success message when payment is successful', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true, message: 'Payment confirmed' }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Thanh toán thành công/i)).toBeInTheDocument();
    });
  });

  test('should display booking code after successful payment', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: {
        success: true,
        data: { bookingCode: 'BOOKING001' }
      }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/BOOKING001/i)).toBeInTheDocument();
    });
  });

  test('should display error message when payment fails', async () => {
    // Mock failed response code
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useSearchParams: () => [
        new URLSearchParams({
          vnp_ResponseCode: '24', // Transaction cancelled
          vnp_TxnRef: 'BOOKING001'
        })
      ]
    }));

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Thanh toán thất bại/i)).toBeInTheDocument();
    });
  });

  test('should call confirmPayment API with transaction reference', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(bookingService.confirmPayment).toHaveBeenCalledWith(
        expect.objectContaining({
          vnpTxnRef: 'BOOKING001'
        })
      );
    });
  });

  test('should display payment amount formatted correctly', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      // Amount should be formatted: 30000000 (in hundredths of VND) = 300,000 VND
      expect(screen.getByText(/300,000/i)).toBeInTheDocument();
    });
  });

  test('should display payment timestamp', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      // Payment date: 20240411144538 = 2024-04-11 14:45:38
      expect(screen.getByText(/2024-04-11/i)).toBeInTheDocument();
    });
  });

  test('should display transaction details including bank', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/NCB/i)).toBeInTheDocument();
      expect(screen.getByText(/21331838/i)).toBeInTheDocument();
    });
  });

  test('should show loading state while confirming payment', async () => {
    bookingService.confirmPayment.mockImplementation(
      () => new Promise(resolve => setTimeout(() => resolve({
        data: { success: true }
      }), 100))
    );

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    // Should show loading indicator
    expect(screen.getByRole('status')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
    });
  });

  test('should handle API error when confirming payment', async () => {
    bookingService.confirmPayment.mockRejectedValue({
      response: {
        data: { message: 'Booking not found' }
      }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Lỗi xác nhận thanh toán/i)).toBeInTheDocument();
    });
  });

  test('should provide return to home button on success', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: { success: true }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    const homeButton = await screen.findByRole('button', { name: /Quay về trang chủ/i });
    expect(homeButton).toBeInTheDocument();

    homeButton.click();
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('should provide retry button on payment failure', async () => {
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useSearchParams: () => [
        new URLSearchParams({
          vnp_ResponseCode: '01',
          vnp_TxnRef: 'BOOKING001'
        })
      ]
    }));

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    const retryButton = await screen.findByRole('button', { name: /Thử lại/i });
    expect(retryButton).toBeInTheDocument();
  });

  test('should validate required query parameters', async () => {
    jest.mock('react-router-dom', () => ({
      ...jest.requireActual('react-router-dom'),
      useSearchParams: () => [new URLSearchParams({})]
    }));

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Tham số không hợp lệ/i)).toBeInTheDocument();
    });
  });

  test('should display server receipt after successful confirmation', async () => {
    bookingService.confirmPayment.mockResolvedValue({
      data: {
        success: true,
        data: {
          bookingCode: 'BOOKING001',
          facilityName: 'Sports Complex',
          courtName: 'Court 1',
          bookingDate: '2024-04-12',
          timeSlot: '14:00 - 15:00'
        }
      }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Sports Complex/i)).toBeInTheDocument();
      expect(screen.getByText(/Court 1/i)).toBeInTheDocument();
      expect(screen.getByText(/14:00 - 15:00/i)).toBeInTheDocument();
    });
  });

  test('should handle HTTP error responses gracefully', async () => {
    bookingService.confirmPayment.mockRejectedValue({
      response: {
        status: 500,
        data: { message: 'Server error' }
      }
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Lỗi máy chủ/i)).toBeInTheDocument();
    });
  });

  test('should handle network timeout errors', async () => {
    bookingService.confirmPayment.mockRejectedValue({
      message: 'Network timeout',
      code: 'ECONNABORTED'
    });

    render(
      <BrowserRouter>
        <PaymentReturn />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Kết nối bị gián đoạn/i)).toBeInTheDocument();
    });
  });
});

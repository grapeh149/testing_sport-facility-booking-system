import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import PaymentReturn from '../PaymentReturn';
import paymentService from '../../services/paymentService';

jest.mock('../../services/paymentService');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useLocation: jest.fn(),
}));

const { useLocation } = require('react-router-dom');

const renderWithSearch = (search = '') => {
  useLocation.mockReturnValue({ search });
  return render(
    <MemoryRouter>
      <PaymentReturn />
    </MemoryRouter>
  );
};

describe('PaymentReturn – Loading State', () => {
  test('shows spinner while verifying payment', () => {
    useLocation.mockReturnValue({ search: '?vnp_TxnRef=123' });
    paymentService.verifyVNPayReturn.mockReturnValueOnce(new Promise(() => {}));

    renderWithSearch('?vnp_TxnRef=123');
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  test('hides spinner after loading completes', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: true, data: { status: 'SUCCESS', amount: 100000 } },
    });

    renderWithSearch('?vnp_TxnRef=123');
    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
    });
  });
});

describe('PaymentReturn – Missing vnp_TxnRef', () => {
  test('shows error when vnp_TxnRef is missing from URL', async () => {
    renderWithSearch('?other=abc');

    await waitFor(() => {
      expect(screen.getByText(/Thiếu thông tin giao dịch VNPay/)).toBeInTheDocument();
    });
    expect(paymentService.verifyVNPayReturn).not.toHaveBeenCalled();
  });

  test('shows error when search string is empty', async () => {
    renderWithSearch('');

    await waitFor(() => {
      expect(screen.getByText(/Thiếu thông tin giao dịch VNPay/)).toBeInTheDocument();
    });
  });
});

describe('PaymentReturn – Success Result', () => {
  const successPayload = {
    status: 'SUCCESS',
    vnpayTxnRef: 'TXN001',
    amount: 150000,
    bankCode: 'NCB',
    bookingId: 42,
  };

  beforeEach(() => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: true, data: successPayload },
    });
  });

  test('shows success alert', async () => {
    renderWithSearch('?vnp_TxnRef=TXN001');

    await waitFor(() => {
      expect(screen.getByText('Thanh toán thành công!')).toBeInTheDocument();
    });
  });

  test('displays transaction details', async () => {
    renderWithSearch('?vnp_TxnRef=TXN001');

    await waitFor(() => {
      expect(screen.getByText(/TXN001/)).toBeInTheDocument();
      expect(screen.getByText(/SUCCESS/)).toBeInTheDocument();
      expect(screen.getByText(/150\.000/)).toBeInTheDocument();
      expect(screen.getByText(/NCB/)).toBeInTheDocument();
      expect(screen.getByText(/42/)).toBeInTheDocument();
    });
  });

  test('shows navigation buttons', async () => {
    renderWithSearch('?vnp_TxnRef=TXN001');

    await waitFor(() => {
      expect(screen.getByText('Về Đặt Sân Của Tôi')).toBeInTheDocument();
      expect(screen.getByText('Về Trang Chủ')).toBeInTheDocument();
    });
  });
});

describe('PaymentReturn – Failed Result', () => {
  test('shows failure alert when status is not SUCCESS', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: true, data: { status: 'FAILED', amount: 0 } },
    });

    renderWithSearch('?vnp_TxnRef=TXN002');

    await waitFor(() => {
      expect(screen.getByText('Thanh toán không thành công.')).toBeInTheDocument();
    });
  });

  test('shows N/A for missing fields in result', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: true, data: { status: 'FAILED' } },
    });

    renderWithSearch('?vnp_TxnRef=TXN003');

    await waitFor(() => {
      const naElements = screen.getAllByText('N/A');
      expect(naElements.length).toBeGreaterThan(0);
    });
  });
});

describe('PaymentReturn – API Error', () => {
  test('shows error from API response message', async () => {
    paymentService.verifyVNPayReturn.mockRejectedValueOnce({
      response: { data: { message: 'Giao dịch không hợp lệ' } },
    });

    renderWithSearch('?vnp_TxnRef=TXN004');

    await waitFor(() => {
      expect(screen.getByText('Giao dịch không hợp lệ')).toBeInTheDocument();
    });
  });

  test('shows generic error when no response message', async () => {
    paymentService.verifyVNPayReturn.mockRejectedValueOnce(new Error('Network Error'));

    renderWithSearch('?vnp_TxnRef=TXN005');

    await waitFor(() => {
      expect(screen.getByText('Network Error')).toBeInTheDocument();
    });
  });

  test('shows fallback error when error has no message', async () => {
    paymentService.verifyVNPayReturn.mockRejectedValueOnce({});

    renderWithSearch('?vnp_TxnRef=TXN006');

    await waitFor(() => {
      expect(
        screen.getByText('Không thể xác minh kết quả thanh toán')
      ).toBeInTheDocument();
    });
  });
});

describe('PaymentReturn – unwrapApiResponse', () => {
  test('throws error when success=false', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: false, message: 'Thanh toán bị từ chối' },
    });

    renderWithSearch('?vnp_TxnRef=TXN007');

    await waitFor(() => {
      expect(screen.getByText('Thanh toán bị từ chối')).toBeInTheDocument();
    });
  });

  test('returns body directly when no success boolean', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { status: 'SUCCESS', vnpayTxnRef: 'TXN008', amount: 0 },
    });

    renderWithSearch('?vnp_TxnRef=TXN008');

    await waitFor(() => {
      expect(screen.getByText(/TXN008/)).toBeInTheDocument();
    });
  });
});

describe('PaymentReturn – Heading', () => {
  test('renders page heading', async () => {
    paymentService.verifyVNPayReturn.mockResolvedValueOnce({
      data: { success: true, data: { status: 'SUCCESS' } },
    });

    renderWithSearch('?vnp_TxnRef=TXN009');

    await waitFor(() => {
      expect(screen.getByText('Kết Quả Thanh Toán VNPay')).toBeInTheDocument();
    });
  });
});

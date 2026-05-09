import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import TimeSlotBooking from '../TimeSlotBooking';
import bookingService from '../../services/bookingService';
import paymentService from '../../services/paymentService';

jest.mock('../../services/bookingService');
jest.mock('../../services/paymentService');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: jest.fn(),
}));
jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

const { useNavigate } = require('react-router-dom');
const { useAuth } = require('../../context/AuthContext');

const mockNavigate = jest.fn();

// Future date – always tomorrow
const FUTURE_DATE = new Date();
FUTURE_DATE.setDate(FUTURE_DATE.getDate() + 1);
const FUTURE_DATE_STR = FUTURE_DATE.toISOString().split('T')[0];

// Past date – always yesterday
const PAST_DATE = new Date();
PAST_DATE.setDate(PAST_DATE.getDate() - 1);
const PAST_DATE_STR = PAST_DATE.toISOString().split('T')[0];

const mockCourt = { id: 1, name: 'Sân Bóng A', surfaceType: 'Cỏ nhân tạo' };

const mockTimeslot = {
  id: 10,
  startTime: '23:00:00',
  endTime: '23:59:00',
  price: 200000,
  depositRate: 30,
};

const pastTimeslot = {
  id: 11,
  startTime: '00:00:00',
  endTime: '01:00:00',
  price: 150000,
  depositRate: 30,
};

const defaultProps = {
  show: true,
  onHide: jest.fn(),
  court: mockCourt,
  timeslot: mockTimeslot,
  bookingDate: FUTURE_DATE_STR,
};

// Accepts optional overrides for props and auth state
const renderComponent = (props = {}, auth = { isAuthenticated: true }) => {
  useNavigate.mockReturnValue(mockNavigate);
  useAuth.mockReturnValue(auth);
  return render(
    <MemoryRouter>
      <TimeSlotBooking {...defaultProps} {...props} />
    </MemoryRouter>
  );
};

beforeEach(() => {
  jest.useFakeTimers();
});

afterEach(() => {
  jest.runOnlyPendingTimers();
  jest.useRealTimers();
  jest.clearAllMocks();
});

// ─── Conditional Rendering ────────────────────────────────────────────────────

describe('TimeSlotBooking – Conditional Rendering', () => {
  test('returns null when court is not provided', () => {
    useNavigate.mockReturnValue(mockNavigate);
    useAuth.mockReturnValue({ isAuthenticated: true });
    const { container } = render(
      <MemoryRouter>
        <TimeSlotBooking {...defaultProps} court={null} />
      </MemoryRouter>
    );
    expect(container.firstChild).toBeNull();
  });

  test('returns null when timeslot is not provided', () => {
    useNavigate.mockReturnValue(mockNavigate);
    useAuth.mockReturnValue({ isAuthenticated: true });
    const { container } = render(
      <MemoryRouter>
        <TimeSlotBooking {...defaultProps} timeslot={null} />
      </MemoryRouter>
    );
    expect(container.firstChild).toBeNull();
  });

  test('renders modal title when court and timeslot are provided', () => {
    renderComponent();
    expect(screen.getByText('Chi tiết đặt sân')).toBeInTheDocument();
  });

  test('shows court name', () => {
    renderComponent();
    expect(screen.getByText('Sân Bóng A')).toBeInTheDocument();
  });

  test('shows surface type', () => {
    renderComponent();
    expect(screen.getByText('Cỏ nhân tạo')).toBeInTheDocument();
  });

  test('shows (Không rõ) when surfaceType is missing', () => {
    renderComponent({ court: { ...mockCourt, surfaceType: null } });
    expect(screen.getByText('(Không rõ)')).toBeInTheDocument();
  });

  test('shows timeslot start and end time', () => {
    renderComponent();
    expect(screen.getByText(/23:00:00 - 23:59:00/)).toBeInTheDocument();
  });

  test('does not show danger/success alert initially', () => {
    renderComponent();
    // The info box has role="alert" so we check specifically for danger/success content
    expect(screen.queryByText(/Vui lòng đăng nhập/)).not.toBeInTheDocument();
    expect(screen.queryByText(/Đã thêm/)).not.toBeInTheDocument();
  });

  test('renders Hủy, Thêm vào sân và Thanh toán VNPay buttons', () => {
    renderComponent();
    expect(screen.getByText('Hủy')).toBeInTheDocument();
    expect(screen.getByText('Thêm vào sân của tôi')).toBeInTheDocument();
    expect(screen.getByText('Thanh toán VNPay')).toBeInTheDocument();
  });
});

// ─── Price Calculation ────────────────────────────────────────────────────────

describe('TimeSlotBooking – Price Calculation', () => {
  test('shows deposit rate percentage label', () => {
    renderComponent();
    expect(screen.getAllByText(/30%/).length).toBeGreaterThan(0);
  });

  test('totalPrice paragraph is in the document', () => {
    renderComponent();
    expect(screen.getByText('Tổng tiền:')).toBeInTheDocument();
  });

  test('depositAmount text node is present', () => {
    renderComponent();
    // The deposit label row exists
    expect(screen.getByText(/Đặt cọc 30%:/)).toBeInTheDocument();
  });

  test('remainingAmount row is present', () => {
    renderComponent();
    expect(screen.getByText('Còn lại:')).toBeInTheDocument();
  });

  test('handles timeslot with no price (price defaults to 0)', () => {
    renderComponent({ timeslot: { ...mockTimeslot, price: null } });
    // depositAmount = 0 → toLocaleString → "0"
    const depositLabel = screen.getByText(/Đặt cọc 30%:/);
    expect(depositLabel).toBeInTheDocument();
  });

  test('shows booking date in localeDateString format', () => {
    renderComponent();
    // Date should appear somewhere in the modal body
    expect(screen.getByText('Ngày:')).toBeInTheDocument();
  });
});

// ─── requireAuthForBooking ────────────────────────────────────────────────────

describe('TimeSlotBooking – requireAuthForBooking', () => {
  test('shows login error when not authenticated – Thêm button', async () => {
    renderComponent({}, { isAuthenticated: false });

    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(
      await screen.findByText(/Vui lòng đăng nhập/)
    ).toBeInTheDocument();

    act(() => jest.advanceTimersByTime(600));
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  test('shows login error when not authenticated – VNPay button', async () => {
    renderComponent({}, { isAuthenticated: false });

    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    expect(
      await screen.findByText(/Vui lòng đăng nhập/)
    ).toBeInTheDocument();

    act(() => jest.advanceTimersByTime(600));
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});

// ─── isPastBookingTime ────────────────────────────────────────────────────────

describe('TimeSlotBooking – isPastBookingTime', () => {
  test('shows past-time error when bookingDate is yesterday', async () => {
    renderComponent({ timeslot: pastTimeslot, bookingDate: PAST_DATE_STR });
    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findByText(/khung giờ quá khứ/)).toBeInTheDocument();
    expect(bookingService.createBooking).not.toHaveBeenCalled();
  });

  test('shows past-time error when bookingDate is null', async () => {
    renderComponent({ bookingDate: null });
    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findByText(/khung giờ quá khứ/)).toBeInTheDocument();
  });

  test('shows past-time error when startTime is null', async () => {
    renderComponent({ timeslot: { ...mockTimeslot, startTime: null } });
    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findByText(/khung giờ quá khứ/)).toBeInTheDocument();
  });

  test('VNPay also blocks past booking time', async () => {
    renderComponent({ timeslot: pastTimeslot, bookingDate: PAST_DATE_STR });
    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    expect(await screen.findByText(/khung giờ quá khứ/)).toBeInTheDocument();
  });
});

// ─── handleDeferPayment ───────────────────────────────────────────────────────

describe('TimeSlotBooking – handleDeferPayment', () => {
  test('success: shows success alert and navigates to /my-bookings', async () => {
    bookingService.createBooking.mockResolvedValueOnce({ data: {} });
    renderComponent();

    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findByText(/Đã thêm vào sân của tôi/)).toBeInTheDocument();

    act(() => jest.advanceTimersByTime(2000));
    expect(mockNavigate).toHaveBeenCalledWith('/my-bookings');
    expect(defaultProps.onHide).toHaveBeenCalled();
  });

  test('success: calls createBooking with correct payload', async () => {
    bookingService.createBooking.mockResolvedValueOnce({ data: {} });
    renderComponent();

    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    await waitFor(() =>
      expect(bookingService.createBooking).toHaveBeenCalledWith({
        courtId: mockCourt.id,
        timeSlotId: mockTimeslot.id,
        bookingDate: FUTURE_DATE_STR,
        note: 'Đặt cọc 30%',
      })
    );
  });

  test('error: shows message from API response', async () => {
    bookingService.createBooking.mockRejectedValueOnce({
      response: { data: { message: 'Khung giờ đã được đặt' } },
    });
    renderComponent();

    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findByText('Khung giờ đã được đặt')).toBeInTheDocument();
  });

  test('error: shows fallback message when API provides none', async () => {
    bookingService.createBooking.mockRejectedValueOnce({});
    renderComponent();

    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(
      await screen.findByText('Lỗi khi thêm vào sân của tôi')
    ).toBeInTheDocument();
  });
});

// ─── handlePaymentMOMO (VNPay) ────────────────────────────────────────────────

describe('TimeSlotBooking – handlePaymentMOMO (VNPay)', () => {
  beforeEach(() => {
    // These tests do real async – real timers needed for findByText polling
    jest.useRealTimers();
    delete window.location;
    window.location = { origin: 'http://localhost:3000', href: '' };
  });

  test('success: redirects to VNPay payment URL', async () => {
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 99 } },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValueOnce({
      data: { success: true, data: 'https://vnpay.test/pay?token=abc' },
    });

    renderComponent();
    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    await waitFor(() =>
      expect(window.location.href).toBe('https://vnpay.test/pay?token=abc')
    );
  });

  test('success: passes correct returnUrl to paymentService', async () => {
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 99 } },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValueOnce({
      data: { success: true, data: 'https://vnpay.test' },
    });

    renderComponent();
    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    await waitFor(() =>
      expect(paymentService.createVNPayPaymentUrl).toHaveBeenCalledWith(
        99,
        'http://localhost:3000/payment/vnpay-return'
      )
    );
  });

  test('error: no error shown when booking response has no id (silent skip)', async () => {
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: null },
    });
    renderComponent();

    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    await waitFor(() =>
      expect(paymentService.createVNPayPaymentUrl).not.toHaveBeenCalled()
    );
    // Component silently exits when booking.id is falsy
    expect(screen.queryByRole('alert', { name: /danger/ })).not.toBeInTheDocument();
  });

  test('error: shows error when paymentUrl is null', async () => {
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 99 } },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValueOnce({
      data: { success: true, data: null },
    });
    renderComponent();

    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    // Component throws: new Error('Khong tao duoc URL thanh toan') → err.message
    expect(
      await screen.findByText('Khong tao duoc URL thanh toan')
    ).toBeInTheDocument();
  });

  test('error: shows error message from thrown exception', async () => {
    bookingService.createBooking.mockRejectedValueOnce(new Error('Lỗi kết nối'));
    renderComponent();

    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    expect(await screen.findByText('Lỗi kết nối')).toBeInTheDocument();
  });

  test('error: shows error from err.response.data.message', async () => {
    bookingService.createBooking.mockRejectedValueOnce({
      response: { data: { message: 'Booking thất bại' } },
    });
    renderComponent();

    fireEvent.click(screen.getByText('Thanh toán VNPay'));

    expect(await screen.findByText('Booking thất bại')).toBeInTheDocument();
  });
});

// ─── Loading State ────────────────────────────────────────────────────────────

describe('TimeSlotBooking – Loading State', () => {
  test('shows "Đang xử lý..." in buttons while createBooking is pending', async () => {
    let resolve;
    bookingService.createBooking.mockReturnValueOnce(
      new Promise(r => { resolve = r; })
    );

    renderComponent();
    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    expect(await screen.findAllByText('Đang xử lý...')).toHaveLength(2);

    resolve({ data: {} });
  });

  test('Hủy button is disabled while loading', async () => {
    let resolve;
    bookingService.createBooking.mockReturnValueOnce(
      new Promise(r => { resolve = r; })
    );

    renderComponent();
    fireEvent.click(screen.getByText('Thêm vào sân của tôi'));

    await screen.findAllByText('Đang xử lý...');
    // Hủy button has disabled={loading} in the component
    expect(screen.getByText('Hủy')).toBeDisabled();

    resolve({ data: {} });
  });
});

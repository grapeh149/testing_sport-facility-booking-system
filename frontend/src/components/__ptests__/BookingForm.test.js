import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import BookingForm from '../BookingForm';
import courtService from '../../services/courtService';
import timeslotService from '../../services/timeslotService';
import bookingService from '../../services/bookingService';
import paymentService from '../../services/paymentService';
import facilityService from '../../services/facilityService';

jest.mock('../../services/courtService');
jest.mock('../../services/timeslotService');
jest.mock('../../services/bookingService');
jest.mock('../../services/paymentService');
jest.mock('../../services/facilityService');

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: jest.fn(),
  useNavigate: jest.fn(),
  useLocation: jest.fn(),
}));

jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

const { useParams, useNavigate, useLocation } = require('react-router-dom');
const { useAuth } = require('../../context/AuthContext');

const mockNavigate = jest.fn();

const mockCourt = { id: 1, name: 'Sân A', price: 100000, facilityId: 5 };
const mockFacility = { id: 5, name: 'Cơ sở XYZ' };

const TOMORROW = new Date();
TOMORROW.setDate(TOMORROW.getDate() + 1);
const TOMORROW_STR = TOMORROW.toISOString().split('T')[0];

const TODAY_STR = new Date().toISOString().split('T')[0];

const YESTERDAY = new Date();
YESTERDAY.setDate(YESTERDAY.getDate() - 1);
const YESTERDAY_STR = YESTERDAY.toISOString().split('T')[0];

const mockTimeslots = [
  { id: 10, startTime: '08:00:00', endTime: '10:00:00' },
  { id: 11, startTime: '10:00:00', endTime: '12:00:00' },
];

const setupMocks = ({ authenticated = true, search = '' } = {}) => {
  useParams.mockReturnValue({ courtId: '1' });
  useNavigate.mockReturnValue(mockNavigate);
  useLocation.mockReturnValue({ search });
  useAuth.mockReturnValue({ isAuthenticated: authenticated });

  courtService.getCourtDetail.mockResolvedValue({
    data: { data: mockCourt },
  });
  facilityService.getFacilityDetail.mockResolvedValue({
    data: { data: mockFacility },
  });
  timeslotService.getTimeslotsByCourt.mockResolvedValue({
    data: { data: mockTimeslots },
  });
};

const renderComponent = (options = {}) => {
  setupMocks(options);
  return render(<MemoryRouter><BookingForm /></MemoryRouter>);
};

// helpers: find form fields by name attribute (React Bootstrap has no auto htmlFor)
const getDateInput = () => document.querySelector('input[name="bookingDate"]');
const getTimeslotSelect = () => document.querySelector('select[name="timeslotId"]');

const waitForForm = () =>
  waitFor(() => expect(screen.getByText('Complete Your Booking')).toBeInTheDocument());

// ─── Auth Guard ───────────────────────────────────────────────────────────────

describe('BookingForm – Auth Guard', () => {
  test('redirects to /login when not authenticated', () => {
    setupMocks({ authenticated: false });
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  test('does not redirect when authenticated', async () => {
    renderComponent();
    await waitForForm();
    expect(mockNavigate).not.toHaveBeenCalled();
  });
});

// ─── Loading State ────────────────────────────────────────────────────────────

describe('BookingForm – Loading State', () => {
  test('shows spinner while loading court data', () => {
    setupMocks();
    courtService.getCourtDetail.mockReturnValueOnce(new Promise(() => {}));
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  test('hides spinner after data loads', async () => {
    renderComponent();
    await waitFor(() =>
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    );
  });
});

// ─── Error State ──────────────────────────────────────────────────────────────

describe('BookingForm – Error State', () => {
  test('shows error and back button when court fails to load', async () => {
    setupMocks();
    courtService.getCourtDetail.mockRejectedValueOnce(new Error('Not found'));
    render(<MemoryRouter><BookingForm /></MemoryRouter>);

    await waitFor(() => {
      expect(screen.getByText('Không thể tải thông tin sân')).toBeInTheDocument();
      expect(screen.getByText('Quay lại danh sách')).toBeInTheDocument();
    });
  });
});

// ─── Data Loading ─────────────────────────────────────────────────────────────

describe('BookingForm – Data Loading', () => {
  test('renders court name after loading', async () => {
    renderComponent();
    await waitFor(() => expect(screen.getByText(/Sân A/)).toBeInTheDocument());
  });

  test('renders facility name after loading', async () => {
    renderComponent();
    await waitFor(() => expect(screen.getByText(/Cơ sở XYZ/)).toBeInTheDocument());
  });

  test('renders timeslot options in the select', async () => {
    renderComponent();
    await waitFor(() => {
      expect(screen.getByText('08:00:00 - 10:00:00')).toBeInTheDocument();
      expect(screen.getByText('10:00:00 - 12:00:00')).toBeInTheDocument();
    });
  });

  test('handles court without facilityId gracefully', async () => {
    setupMocks();
    courtService.getCourtDetail.mockResolvedValueOnce({
      data: { data: { ...mockCourt, facilityId: null } },
    });
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitFor(() =>
      expect(screen.queryByRole('status')).not.toBeInTheDocument()
    );
    expect(facilityService.getFacilityDetail).not.toHaveBeenCalled();
  });

  test('handles facility loading error gracefully – court still shows', async () => {
    setupMocks();
    facilityService.getFacilityDetail.mockRejectedValueOnce(new Error('fail'));
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitFor(() => expect(screen.getByText(/Sân A/)).toBeInTheDocument());
  });

  test('shows "Đang tải..." for facility name while loading', async () => {
    setupMocks();
    facilityService.getFacilityDetail.mockReturnValueOnce(new Promise(() => {}));
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitFor(() =>
      expect(screen.getByText('Đang tải...')).toBeInTheDocument()
    );
  });
});

// ─── Form Validation ──────────────────────────────────────────────────────────

describe('BookingForm – Form Validation', () => {
  test('shows error when bookingDate is empty on submit', async () => {
    renderComponent();
    await waitForForm();

    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));
    expect(await screen.findByText('Vui lòng chọn ngày')).toBeInTheDocument();
  });

  test('shows error when bookingDate is in the past', async () => {
    renderComponent();
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: YESTERDAY_STR },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(
      await screen.findByText('Không thể chọn ngày trong quá khứ')
    ).toBeInTheDocument();
  });

  test('shows error when timeslotId is empty', async () => {
    renderComponent();
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(await screen.findByText('Vui lòng chọn khung giờ')).toBeInTheDocument();
  });

  test('clears bookingDate error when user picks a new date', async () => {
    renderComponent();
    await waitForForm();

    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));
    await screen.findByText('Vui lòng chọn ngày');

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    expect(screen.queryByText('Vui lòng chọn ngày')).not.toBeInTheDocument();
  });

  test('shows back link on form', async () => {
    renderComponent();
    await waitForForm();
    expect(screen.getByText('Quay lại')).toBeInTheDocument();
  });
});

// ─── Price Calculation ────────────────────────────────────────────────────────

describe('BookingForm – Price Calculation', () => {
  test('shows price label after loading', async () => {
    renderComponent();
    await waitFor(() =>
      expect(screen.getByText('Giá:')).toBeInTheDocument()
    );
  });

  test('calculates price when timeslot is selected (2h × 100000 = 200000)', async () => {
    renderComponent();
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });

    // 08:00 – 10:00 = 2h × 100,000 = 200,000
    await waitFor(() => {
      // price is rendered via toLocaleString – match both dot and comma separators
      const priceEl = screen.getByText(/200[.,]000/);
      expect(priceEl).toBeInTheDocument();
    });
  });

  test('clears timeslotId error when user selects a slot', async () => {
    renderComponent();
    await waitForForm();

    // Trigger validation error first
    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));
    await screen.findByText('Vui lòng chọn khung giờ');

    // Now pick a timeslot – error should disappear
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    expect(screen.queryByText('Vui lòng chọn khung giờ')).not.toBeInTheDocument();
  });
});

// ─── Submit: deferPayment mode ────────────────────────────────────────────────

describe('BookingForm – Submit: deferPayment mode', () => {
  test('shows "Thêm vào sân của tôi" button when deferPayment=1', async () => {
    setupMocks({ search: '?deferPayment=1' });
    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitFor(() =>
      expect(
        screen.getByRole('button', { name: /Thêm vào sân/ })
      ).toBeInTheDocument()
    );
  });

  test('shows success message after deferred booking', async () => {
    setupMocks({ search: '?deferPayment=1' });
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 55 } },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Thêm vào sân/ }));

    expect(
      await screen.findByText(/Đã thêm vào sân của tôi/)
    ).toBeInTheDocument();
  });

  test('navigates to /my-bookings after deferred booking', async () => {
    setupMocks({ search: '?deferPayment=1' });
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 55 } },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Thêm vào sân/ }));

    await waitFor(
      () => expect(mockNavigate).toHaveBeenCalledWith('/my-bookings'),
      { timeout: 3000 }
    );
  }, 5000);
});

// ─── Submit: VNPay payment mode ───────────────────────────────────────────────

describe('BookingForm – Submit: VNPay payment mode', () => {
  beforeEach(() => {
    delete window.location;
    window.location = { origin: 'http://localhost:3000', href: '' };
  });

  test('redirects to VNPay URL on successful payment creation', async () => {
    setupMocks();
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 77 } },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValueOnce({
      data: { success: true, data: 'https://vnpay.test/pay' },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    await waitFor(() =>
      expect(window.location.href).toBe('https://vnpay.test/pay')
    );
  });

  test('shows success message before redirect', async () => {
    setupMocks();
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: { id: 77 } },
    });
    paymentService.createVNPayPaymentUrl.mockResolvedValueOnce({
      data: { success: true, data: 'https://vnpay.test/pay' },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(await screen.findByText(/Đặt sân thành công/)).toBeInTheDocument();
  });

  test('shows error when bookingId is missing from response', async () => {
    setupMocks();
    bookingService.createBooking.mockResolvedValueOnce({
      data: { success: true, data: null },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(await screen.findByText('Không thể tạo đặt sân')).toBeInTheDocument();
  });

  test('shows error from API when booking call fails', async () => {
    setupMocks();
    bookingService.createBooking.mockRejectedValueOnce({
      response: { data: { message: 'Khung giờ đã đầy' } },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TOMORROW_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '10' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(await screen.findByText('Khung giờ đã đầy')).toBeInTheDocument();
  });
});

// ─── validateBookingDateTime ──────────────────────────────────────────────────

describe('BookingForm – validateBookingDateTime', () => {
  test('shows time-already-passed error for today + past timeslot on submit', async () => {
    setupMocks();
    timeslotService.getTimeslotsByCourt.mockResolvedValue({
      data: { data: [{ id: 99, startTime: '00:01:00', endTime: '01:00:00' }] },
    });

    render(<MemoryRouter><BookingForm /></MemoryRouter>);
    await waitForForm();

    fireEvent.change(getDateInput(), {
      target: { name: 'bookingDate', value: TODAY_STR },
    });
    fireEvent.change(getTimeslotSelect(), {
      target: { name: 'timeslotId', value: '99' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Đặt Sân/ }));

    expect(
      await screen.findByText(/Thời gian hiện tại đã vượt quá/)
    ).toBeInTheDocument();
  });
});

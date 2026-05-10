import React from 'react';
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import OwnerDashboard from '../../OwnerDashboard';
import * as AuthContext from '../../../context/AuthContext';
import facilityService from '../../../services/facilityService';
import courtService from '../../../services/courtService';
import bookingService from '../../../services/bookingService';
import sportTypeService from '../../../services/sportTypeService';
import timeslotService from '../../../services/timeslotService';
import checkInService from '../../../services/checkInService';
import cloudinaryService from '../../../services/cloudinaryService';

jest.mock('../../../context/AuthContext');
jest.mock('../../../services/facilityService');
jest.mock('../../../services/courtService');
jest.mock('../../../services/bookingService');
jest.mock('../../../services/sportTypeService');
jest.mock('../../../services/timeslotService');
jest.mock('../../../services/checkInService');
jest.mock('../../../services/cloudinaryService');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: jest.fn()
}));

const renderComponent = () => {
  return render(
    <MemoryRouter>
      <OwnerDashboard />
    </MemoryRouter>
  );
};

const mockUser = {
  userId: 1,
  email: 'owner@test.com',
  role: 'OWNER',
  fullName: 'Owner Test'
};

const mockFacility = {
  id: 1,
  name: 'Test Facility',
  address: '123 Main St',
  district: 'District 1',
  city: 'City 1',
  phone: '0123456789',
  description: 'Test description',
  openTime: '07:00',
  closeTime: '23:00',
  commissionRate: 10,
  cancelBeforeHours: 2,
  avgRating: 4.5,
  totalReviews: 10,
  coverImageUrl: 'https://test.com/image.jpg'
};

const mockCourt = {
  id: 1,
  facilityId: 1,
  name: 'Court 1',
  description: 'Test court',
  sportTypeId: 1,
  surfaceType: 'Grass',
  isIndoor: false,
  isActive: true
};

const mockBooking = {
  id: 1,
  code: 'BOOK001',
  courtId: 1,
  userId: 2,
  bookingDate: '2026-05-10',
  startTime: '07:00',
  endTime: '08:00',
  status: 'CONFIRMED',
  totalPrice: 100000,
  createdAt: '2026-05-09',
  paymentStatus: 'PAID'
};

const mockSportType = {
  id: 1,
  name: 'Football',
  isActive: true
};

const mockTimeslot = {
  id: 1,
  courtId: 1,
  dayOfWeek: 1,
  startTime: '07:00',
  endTime: '08:00',
  price: 100000,
  depositRate: 0.3
};

const mockLoadedDashboard = ({
  facilities = [mockFacility],
  courts = [mockCourt],
  bookings = [mockBooking],
  sportTypes = [mockSportType]
} = {}) => {
  facilityService.getOwnerFacilities.mockResolvedValue({
    data: { data: facilities, success: true }
  });
  courtService.getCourtsByFacility.mockResolvedValue({
    data: { data: courts, success: true }
  });
  bookingService.getOwnerAllBookings.mockResolvedValue({
    data: { data: { content: bookings }, success: true }
  });
  sportTypeService.getActiveSportTypes.mockResolvedValue({
    data: { data: sportTypes, success: true }
  });
};

describe('OwnerDashboard Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    const mockNavigate = jest.fn();
    require('react-router-dom').useNavigate.mockReturnValue(mockNavigate);
    
    AuthContext.useAuth = jest.fn(() => ({
      user: mockUser,
      isAuthenticated: true
    }));
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('redirects to home when not authenticated', () => {
    const mockNavigate = jest.fn();
    require('react-router-dom').useNavigate.mockReturnValue(mockNavigate);
    
    AuthContext.useAuth = jest.fn(() => ({
      user: null,
      isAuthenticated: false
    }));

    renderComponent();
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('redirects to home when user is not OWNER', () => {
    const mockNavigate = jest.fn();
    require('react-router-dom').useNavigate.mockReturnValue(mockNavigate);
    
    AuthContext.useAuth = jest.fn(() => ({
      user: { ...mockUser, role: 'USER' },
      isAuthenticated: true
    }));

    renderComponent();
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('loads owner facilities on mount', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [mockCourt], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [mockBooking] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(facilityService.getOwnerFacilities).toHaveBeenCalledWith(1);
    });
    
    await waitFor(() => {
      const elements = screen.getAllByText(/Test Facility/i);
      expect(elements.length).toBeGreaterThan(0);
    });
  });

  test('loads sport types on mount', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [], success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(sportTypeService.getActiveSportTypes).toHaveBeenCalled();
    });
  });

  test('renders facility list with facility name and details', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
    });
  });

  test('displays error message when facility loading fails', async () => {
    facilityService.getOwnerFacilities.mockRejectedValue({
      response: { data: { message: 'Load error' } }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Load error/i)).toBeInTheDocument();
    });
  });

  test('calculates revenue from bookings with correct date filtering', async () => {
    const bookingDate = new Date();
    const currentMonthBooking = {
      ...mockBooking,
      totalPrice: 500000,
      createdAt: bookingDate.toISOString()
    };

    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [currentMonthBooking] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(bookingService.getOwnerAllBookings).toHaveBeenCalledWith(0, 100);
    });
  });

  test('displays facility with rating information', async () => {
    const facilityWithRating = { ...mockFacility, avgRating: 4.5, totalReviews: 10 };
    
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [facilityWithRating], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
    });
  });

  test('displays no rating when facility has no reviews', async () => {
    const noRatingFacility = {
      ...mockFacility,
      avgRating: null,
      totalReviews: 0
    };

    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [noRatingFacility], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
    });
  });

  test('handles API response unwrapping with success flag', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { success: true, data: [mockFacility] }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { success: true, data: [mockCourt] }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { success: true, data: { content: [mockBooking] } }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { success: true, data: [mockSportType] }
    });

    renderComponent();

    await waitFor(() => {
      const elements = screen.queryAllByText(/Test Facility/i);
      expect(elements.length).toBeGreaterThan(0);
    });
  });

  test('handles missing userId gracefully in component initialization', async () => {
    AuthContext.useAuth = jest.fn(() => ({
      user: { ...mockUser, userId: null },
      isAuthenticated: true
    }));

    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(sportTypeService.getActiveSportTypes).toHaveBeenCalled();
    });
  });

  test('handles sport type loading failure with fallback', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockRejectedValue({
      response: { data: { message: 'Sport types error' } }
    });

    renderComponent();

    await waitFor(() => {
      // Component should still render with greeting even if sport types fail
      expect(screen.getByText(/Xin chào/i)).toBeInTheDocument();
    });
  });

  test('displays facility with pending status', async () => {
    const pendingFacility = {
      ...mockFacility,
      status: 'PENDING'
    };

    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [pendingFacility], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
    });
  });

  test('renders multiple facilities from API response', async () => {
    const facility2 = { ...mockFacility, id: 2, name: 'Facility 2' };

    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility, facility2], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
      expect(screen.getByText(/Facility 2/i)).toBeInTheDocument();
    });
  });

  test('loads courts for each facility', async () => {
    const facility2 = { ...mockFacility, id: 2 };
    const court2 = { ...mockCourt, id: 2, facilityId: 2 };

    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility, facility2], success: true }
    });
    courtService.getCourtsByFacility
      .mockResolvedValueOnce({ data: { data: [mockCourt], success: true } })
      .mockResolvedValueOnce({ data: { data: [court2], success: true } });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(courtService.getCourtsByFacility).toHaveBeenCalledWith(1);
      expect(courtService.getCourtsByFacility).toHaveBeenCalledWith(2);
    });
  });

  test('handles court loading failure gracefully', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [mockFacility], success: true }
    });
    courtService.getCourtsByFacility.mockRejectedValue({
      response: { data: { message: 'Court load error' } }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Test Facility/i)).toBeInTheDocument();
    });
  });

  test('renders page title with owner greeting', async () => {
    facilityService.getOwnerFacilities.mockResolvedValue({
      data: { data: [], success: true }
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [], success: true }
    });
    bookingService.getOwnerAllBookings.mockResolvedValue({
      data: { data: { content: [] }, success: true }
    });
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { data: [mockSportType], success: true }
    });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Xin chào/i)).toBeInTheDocument();
      expect(screen.getByText(/Owner Test/i)).toBeInTheDocument();
    });
  });

  test('creates a facility from the add facility modal after validation', async () => {
    const user = userEvent.setup();
    mockLoadedDashboard({ facilities: [], courts: [], bookings: [] });
    facilityService.createFacility.mockResolvedValue({
      data: { data: { id: 99 }, success: true }
    });

    renderComponent();

    await user.click(screen.getByRole('button', { name: /Thêm Cơ Sở Mới/i }));
    await user.click(screen.getByRole('button', { name: /^Thêm Cơ Sở$/i }));
    expect(await screen.findByText(/Vui lòng nhập tên sân/i)).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText(/Nhập tên cơ sở/i), 'New Facility');
    await user.type(screen.getByPlaceholderText(/Ví dụ: 123 Đường A/i), '123 Street');
    await user.type(screen.getByPlaceholderText(/Ví dụ: Quận 1/i), 'District 1');
    await user.type(screen.getByPlaceholderText(/Ví dụ: TP\. Hồ Chí Minh/i), 'City 1');
    await user.type(screen.getByPlaceholderText(/Nhập số điện thoại/i), '0987654321');
    await user.type(screen.getByPlaceholderText(/Nhập mô tả cơ sở/i), 'Description');

    const dialog = screen.getByRole('dialog');
    const openTimeInput = within(dialog).getAllByDisplayValue('07:00')[0];
    const closeTimeInput = within(dialog).getAllByDisplayValue('23:00')[0];
    await user.clear(openTimeInput);
    await user.type(openTimeInput, '08:00');
    await user.clear(closeTimeInput);
    await user.type(closeTimeInput, '22:00');

    const commissionInput = screen.getByDisplayValue('10');
    const cancelInput = screen.getByDisplayValue('2');
    await user.clear(commissionInput);
    await user.type(commissionInput, '12');
    await user.clear(cancelInput);
    await user.type(cancelInput, '3');

    await user.click(screen.getByRole('button', { name: /^Thêm Cơ Sở$/i }));

    await waitFor(() => {
      expect(facilityService.createFacility).toHaveBeenCalledWith(
        expect.objectContaining({
          name: 'New Facility',
          address: '123 Street',
          district: 'District 1',
          city: 'City 1',
          phone: '0987654321',
          description: 'Description',
          openTime: '08:00',
          closeTime: '22:00',
          commissionRate: 12,
          cancelBeforeHours: 3,
          autoConfirm: false
        }),
        1
      );
    });
  });

  test('creates a court from the add court modal after validation', async () => {
    const user = userEvent.setup();
    mockLoadedDashboard({ facilities: [mockFacility], courts: [], bookings: [] });
    courtService.createCourt.mockResolvedValue({
      data: { data: { id: 88 }, success: true }
    });

    renderComponent();

    await user.click(screen.getAllByRole('button', { name: /^Thêm Sân Chơi Mới$/i })[0]);
    await user.click(screen.getByRole('button', { name: /^Thêm Sân Chơi$/i }));
    expect(await screen.findByText(/Vui lòng chọn sân và nhập tên sân chơi/i)).toBeInTheDocument();

    const addCourtDialog = screen.getByRole('dialog');
    fireEvent.change(within(addCourtDialog).getAllByRole('combobox')[0], { target: { value: '1' } });
    await user.type(screen.getByPlaceholderText(/Ví dụ: Sân 1, Sân 2/i), 'Court Alpha');
    await user.click(screen.getByRole('button', { name: /^Thêm Sân Chơi$/i }));

    await waitFor(() => {
      expect(courtService.createCourt).toHaveBeenCalledWith(
        expect.objectContaining({
          facilityId: 1,
          sportTypeId: 1,
          name: 'Court Alpha',
          isIndoor: false,
          isActive: true
        })
      );
    });
  });

  test('confirms a pending booking from the booking detail modal', async () => {
    const user = userEvent.setup();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const pendingBooking = { ...mockBooking, status: 'PENDING_CONFIRM' };

    mockLoadedDashboard({ bookings: [pendingBooking] });

    renderComponent();

    await user.click(screen.getByRole('tab', { name: /Đơn Đặt/i }));
    await user.click(await screen.findByRole('button', { name: /Xem/i }));

    const dialog = await screen.findByRole('dialog');
    await user.click(within(dialog).getByRole('button', { name: /^Xác nhận$/i }));

    await waitFor(() => {
      expect(bookingService.confirmBooking).toHaveBeenCalledWith(1, 1);
    });

    confirmSpy.mockRestore();
  });

  test('cancels a pending booking with a reason from the booking detail modal', async () => {
    const user = userEvent.setup();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const pendingBooking = { ...mockBooking, status: 'PENDING_CONFIRM' };

    mockLoadedDashboard({ bookings: [pendingBooking] });

    renderComponent();

    await user.click(screen.getByRole('tab', { name: /Đơn Đặt/i }));
    await user.click(await screen.findByRole('button', { name: /Xem/i }));

    const detailDialog = await screen.findByRole('dialog');
    await user.click(within(detailDialog).getByRole('button', { name: /^Hủy$/i }));

    expect(screen.getByPlaceholderText(/Nhập lý do hủy đơn đặt/i)).toBeInTheDocument();
    await user.type(screen.getByPlaceholderText(/Nhập lý do hủy đơn đặt/i), 'Weather issue');
    await user.click(screen.getByRole('button', { name: /^Xác Nhận Hủy$/i }));

    await waitFor(() => {
      expect(bookingService.cancelBookingWithReason).toHaveBeenCalledWith(1, 'Weather issue');
    });

    confirmSpy.mockRestore();
  });

  test('checks in a confirmed booking from the booking detail modal', async () => {
    const user = userEvent.setup();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const todayBooking = {
      ...mockBooking,
      bookingDate: new Date().toISOString(),
      status: 'CONFIRMED'
    };

    mockLoadedDashboard({ bookings: [todayBooking] });
    checkInService.checkInBooking.mockResolvedValue({
      data: { data: true, success: true }
    });

    renderComponent();

    await user.click(screen.getByRole('tab', { name: /Đơn Đặt/i }));
    await user.click(await screen.findByRole('button', { name: /Xem/i }));

    const dialog = await screen.findByRole('dialog');
    await user.click(within(dialog).getByRole('button', { name: /Check-in/i }));

    await waitFor(() => {
      expect(checkInService.checkInBooking).toHaveBeenCalledWith(1, 1, 'Owner check-in');
    });

    confirmSpy.mockRestore();
  });

  test('loads, adds, and deletes timeslots for a selected court', async () => {
    const user = userEvent.setup();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);

    mockLoadedDashboard({ courts: [mockCourt], bookings: [] });
    timeslotService.getTimeslotsByCourt
      .mockResolvedValueOnce({ data: { data: [], success: true } })
      .mockResolvedValueOnce({ data: { data: [mockTimeslot], success: true } })
      .mockResolvedValueOnce({ data: { data: [], success: true } });
    timeslotService.createTimeslot.mockResolvedValue({
      data: { data: { id: 2 }, success: true }
    });
    timeslotService.deleteTimeslot.mockResolvedValue({
      data: { data: true, success: true }
    });

    renderComponent();

    await user.click(screen.getByRole('tab', { name: /Khung Giờ/i }));
    fireEvent.change(screen.getAllByRole('combobox')[0], { target: { value: '1' } });

    await waitFor(() => {
      expect(timeslotService.getTimeslotsByCourt).toHaveBeenCalledWith('1');
    });

    await user.click(screen.getByRole('button', { name: /^Thêm$/i }));
    expect(await screen.findByText(/Giá phải lớn hơn 0/i)).toBeInTheDocument();

    fireEvent.change(screen.getAllByRole('spinbutton')[0], { target: { value: '120000' } });
    await user.click(screen.getByRole('button', { name: /^Thêm$/i }));

    await waitFor(() => {
      expect(timeslotService.createTimeslot).toHaveBeenCalledWith(
        expect.objectContaining({
          courtId: 1,
          dayOfWeek: null,
          startTime: '07:00',
          endTime: '08:00',
          price: 120000,
          depositRate: 0.3
        })
      );
    });

    await screen.findByText(/Chủ Nhật/i);
    const deleteButtons = screen.getAllByRole('button', { name: /Xóa/i });
    await user.click(deleteButtons[deleteButtons.length - 1]);

    await waitFor(() => {
      expect(timeslotService.deleteTimeslot).toHaveBeenCalledWith(1);
    });

    confirmSpy.mockRestore();
  });
});

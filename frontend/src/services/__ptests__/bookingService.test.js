import bookingService from '../bookingService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

describe('bookingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('calls booking endpoints with correct paths', () => {
    bookingService.getMyBookings(1, 10);
    bookingService.getCourtBookings(5);
    bookingService.createBooking({ courtId: 1 });
    bookingService.cancelBooking(9);

    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/my-bookings', { params: { page: 1, size: 10 } });
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/court/5');
    expect(apiClient.post).toHaveBeenCalledWith('/api/bookings', { courtId: 1 });
    expect(apiClient.delete).toHaveBeenCalledWith('/api/bookings/9');
  });

  test('calls getMyBookings with default pagination', () => {
    bookingService.getMyBookings();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/my-bookings', { params: { page: 0, size: 10 } });
  });

  test('calls getOwnerAllBookings endpoint', () => {
    bookingService.getOwnerAllBookings(2, 50);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/owner/all-bookings', { params: { page: 2, size: 50 } });
  });

  test('calls getOwnerAllBookings with default pagination', () => {
    bookingService.getOwnerAllBookings();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/owner/all-bookings', { params: { page: 0, size: 100 } });
  });

  test('calls getBookingDetail endpoint', () => {
    bookingService.getBookingDetail(15);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/15');
  });

  test('calls getCourtBookings with different courtId', () => {
    bookingService.getCourtBookings(25);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/bookings/court/25');
  });

  test('calls createBooking with booking data', () => {
    const bookingData = { courtId: 5, timeslotId: 10, totalPrice: 100000 };
    bookingService.createBooking(bookingData);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/bookings', bookingData);
  });

  test('calls updateBooking endpoint', () => {
    const updateData = { status: 'CONFIRMED', totalPrice: 110000 };
    bookingService.updateBooking(12, updateData);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/bookings/12', updateData);
  });

  test('calls confirmBooking endpoint with ownerId', () => {
    bookingService.confirmBooking(20, 3);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/bookings/20/confirm', {}, { params: { ownerId: 3 } });
  });

  test('calls cancelBookingWithReason endpoint', () => {
    bookingService.cancelBookingWithReason(18, 'Customer requested');
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/bookings/18/cancel', {}, { params: { reason: 'Customer requested' } });
  });

  test('calls cancelBooking endpoint with id', () => {
    bookingService.cancelBooking(18);
    
    expect(apiClient.delete).toHaveBeenCalledWith('/api/bookings/18');
  });

  test('calls confirmPayment endpoint with vnpTxnRef', () => {
    bookingService.confirmPayment('VNP20260509123456');
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/payments/vnpay/confirm', {}, { params: { vnpTxnRef: 'VNP20260509123456' } });
  });

  test('calls cancelBookingWithReason with different reasons', () => {
    const reasons = ['Illness', 'Schedule conflict', 'Weather cancellation'];
    
    reasons.forEach(reason => {
      bookingService.cancelBookingWithReason(100, reason);
    });
    
    reasons.forEach((reason, idx) => {
      expect(apiClient.post).toHaveBeenNthCalledWith(
        idx + 1,
        '/api/bookings/100/cancel',
        {},
        { params: { reason } }
      );
    });
  });

  test('creates booking with complex data structure', () => {
    const complexBookingData = {
      courtId: 8,
      timeslotId: 12,
      bookingDate: '2026-05-15',
      totalPrice: 250000,
      depositAmount: 75000,
      paymentMethod: 'VNPAY',
      notes: 'Special request'
    };
    
    bookingService.createBooking(complexBookingData);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/bookings', complexBookingData);
  });

  test('updates booking with multiple fields', () => {
    const updateData = {
      status: 'CHECKED_IN',
      totalPrice: 120000,
      depositAmount: 36000,
      notes: 'Updated by admin'
    };
    
    bookingService.updateBooking(25, updateData);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/bookings/25', updateData);
  });

  test('getMyBookings with various pagination values', () => {
    const paginationCases = [
      { page: 0, size: 10 },
      { page: 5, size: 20 },
      { page: 10, size: 50 }
    ];

    paginationCases.forEach(({ page, size }) => {
      bookingService.getMyBookings(page, size);
    });

    paginationCases.forEach(({ page, size }, idx) => {
      expect(apiClient.get).toHaveBeenNthCalledWith(
        idx + 1,
        '/api/bookings/my-bookings',
        { params: { page, size } }
      );
    });
  });

  test('confirmBooking with multiple ownerId values', () => {
    const ownerIds = [1, 5, 10];

    ownerIds.forEach(ownerId => {
      bookingService.confirmBooking(30, ownerId);
    });

    ownerIds.forEach((ownerId, idx) => {
      expect(apiClient.post).toHaveBeenNthCalledWith(
        idx + 1,
        '/api/bookings/30/confirm',
        {},
        { params: { ownerId } }
      );
    });
  });
});
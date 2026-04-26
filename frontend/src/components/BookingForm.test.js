import BookingForm from '../components/BookingForm';

jest.mock('../services/bookingService', () => ({
  createBooking: jest.fn(),
  getMyBookings: jest.fn()
}));
jest.mock('../services/courtService', () => ({
  getCourtDetail: jest.fn()
}));
jest.mock('../services/timeslotService', () => ({
  getTimeslotsByCourt: jest.fn()
}));
jest.mock('../services/facilityService', () => ({
  getFacilityDetail: jest.fn()
}));
jest.mock('../services/paymentService', () => ({
  createVNPayPaymentUrl: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1, role: 'CUSTOMER' },
    isAuthenticated: true
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
  useParams: () => ({ courtId: '1' })
}));

describe('BookingForm Component', () => {
  test('should be importable', () => {
    expect(BookingForm).toBeDefined();
  });
});


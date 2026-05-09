import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CourtDetail from '../../CourtDetail';

jest.useFakeTimers().setSystemTime(new Date('2026-05-09T08:00:00'));

jest.mock('../../../services/courtService', () => ({
  getCourtDetail: jest.fn(),
}));

jest.mock('../../../services/timeslotService', () => ({
  getTimeslotsByCourt: jest.fn(),
}));

jest.mock('../../../services/bookingService', () => ({
  getCourtBookings: jest.fn(),
}));

jest.mock('../../TimeSlotBooking', () => () => <div data-testid="timeslot-booking" />);

jest.mock('@fullcalendar/react', () => {
  const React = require('react');
  return {
    __esModule: true,
    default: React.forwardRef((props, ref) => {
      React.useImperativeHandle(ref, () => ({}));
      const availableEvent = (props.events || []).find((event) => event.extendedProps?.status === 'available');

      return (
        <div>
          <div data-testid="calendar">{(props.events || []).length}</div>
          <button
            type="button"
            onClick={() => availableEvent && props.eventClick({ event: availableEvent })}
          >
            Chọn khung giờ
          </button>
        </div>
      );
    }),
  };
});

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ courtId: '1' }),
  useNavigate: () => jest.fn(),
}));

jest.mock('../../../context/AuthContext', () => ({
  useAuth: () => ({ isAuthenticated: true }),
}));

const courtService = require('../../../services/courtService');
const timeslotService = require('../../../services/timeslotService');
const bookingService = require('../../../services/bookingService');

describe('CourtDetail', () => {
  beforeEach(() => {
    courtService.getCourtDetail.mockReset();
    timeslotService.getTimeslotsByCourt.mockReset();
    bookingService.getCourtBookings.mockReset();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders court detail and opens booking modal from calendar event', async () => {
    courtService.getCourtDetail.mockResolvedValue({
      data: { data: { id: 1, name: 'Court 1', description: 'Test court', surfaceType: 'Cỏ nhân tạo', isIndoor: true, isActive: true } },
    });
    timeslotService.getTimeslotsByCourt.mockResolvedValue({
      data: { data: [{ id: 1, startTime: '23:00:00', endTime: '23:59:00', price: 100000, dayOfWeek: null }] },
    });
    bookingService.getCourtBookings.mockResolvedValue({ data: { data: [] } });

    render(
      <MemoryRouter>
        <CourtDetail />
      </MemoryRouter>
    );

    expect(await screen.findByText('Court 1')).toBeInTheDocument();
    expect(screen.getByTestId('calendar')).toHaveTextContent('30');

    fireEvent.click(screen.getByRole('button', { name: 'Chọn khung giờ' }));
    expect(await screen.findByTestId('timeslot-booking')).toBeInTheDocument();
  });

  test('shows empty-state message when no timeslots exist', async () => {
    courtService.getCourtDetail.mockResolvedValue({
      data: { data: { id: 1, name: 'Court 1', description: 'Test court' } },
    });
    timeslotService.getTimeslotsByCourt.mockResolvedValue({ data: { data: [] } });
    bookingService.getCourtBookings.mockResolvedValue({ data: { data: [] } });

    render(
      <MemoryRouter>
        <CourtDetail />
      </MemoryRouter>
    );

    expect(await screen.findByText('Không có khung giờ nào cho sân này')).toBeInTheDocument();
  });
});
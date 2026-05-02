import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CourtDetail from './CourtDetail';

jest.mock('../services/courtService', () => ({
  getCourtDetail: jest.fn()
}));

jest.mock('../services/timeslotService', () => ({
  getTimeslotsByCourt: jest.fn()
}));

jest.mock('../services/bookingService', () => ({
  getCourtBookings: jest.fn()
}));

jest.mock('./TimeSlotBooking', () => () => <div data-testid="timeslot-booking" />);

jest.mock('@fullcalendar/react', () => ({
  __esModule: true,
  default: () => <div data-testid="full-calendar" />
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ courtId: '1' }),
  useNavigate: () => jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({ isAuthenticated: true })
}));

describe('CourtDetail Component', () => {
  test('renders court detail after loading', async () => {
    const courtService = require('../services/courtService');
    const timeslotService = require('../services/timeslotService');
    const bookingService = require('../services/bookingService');

    courtService.getCourtDetail.mockResolvedValue({ data: { data: { id: 1, name: 'Court 1', description: 'Test court' } } });
    timeslotService.getTimeslotsByCourt.mockResolvedValue({ data: { data: [
      { id: 1, startTime: '08:00:00', endTime: '09:00:00', price: 100000, dayOfWeek: 1 }
    ] } });
    bookingService.getCourtBookings.mockResolvedValue({ data: { data: [] } });

    render(
      <MemoryRouter>
        <CourtDetail />
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText(/Court 1/i)).toBeInTheDocument());
    expect(screen.getByTestId('full-calendar')).toBeInTheDocument();
  });
});

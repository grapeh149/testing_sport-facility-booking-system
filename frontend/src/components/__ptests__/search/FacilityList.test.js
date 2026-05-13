import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import FacilityList from '../../FacilityList';
import facilityService from '../../../services/facilityService';

jest.mock('../../../services/facilityService');

describe('FacilityList', () => {
  beforeEach(() => {
    facilityService.getFacilities.mockReset();
  });

  test('renders facility cards and paginates to the next page', async () => {
    facilityService.getFacilities
      .mockResolvedValueOnce({
        data: {
          success: true,
          data: {
            content: [
              {
                id: 1,
                name: 'Facility One',
                address: 'Address 1',
                phone: '0900000001',
                avgRating: 4.7,
              },
            ],
            totalPages: 2,
          },
        },
      })
      .mockResolvedValueOnce({
        data: {
          success: true,
          data: {
            content: [
              {
                id: 2,
                name: 'Facility Two',
                address: 'Address 2',
                phone: '0900000002',
                avgRating: 4.2,
              },
            ],
            totalPages: 2,
          },
        },
      });

    render(
      <MemoryRouter>
        <FacilityList />
      </MemoryRouter>
    );

    expect(await screen.findByText('Facility One')).toBeInTheDocument();
    expect(screen.getByText('Trang 1 / 2')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Tiếp' }));

    await waitFor(() => expect(facilityService.getFacilities).toHaveBeenLastCalledWith(1, 6));
    expect(await screen.findByText('Facility Two')).toBeInTheDocument();
    expect(screen.getByText('Trang 2 / 2')).toBeInTheDocument();
  });
});
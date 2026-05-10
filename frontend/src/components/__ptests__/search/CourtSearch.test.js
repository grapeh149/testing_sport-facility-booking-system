import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CourtSearch from '../../CourtSearch';
import courtService from '../../../services/courtService';
import sportTypeService from '../../../services/sportTypeService';

jest.mock('../../../services/courtService');
jest.mock('../../../services/sportTypeService');

describe('CourtSearch', () => {
  beforeEach(() => {
    courtService.searchCourts.mockReset();
    sportTypeService.getActiveSportTypes.mockReset();
    sportTypeService.getAllSportTypes.mockReset();
  });

  test('loads sport types and shows search form', async () => {
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { success: true, data: [{ id: 1, name: 'Bóng đá' }] },
    });

    render(
      <MemoryRouter>
        <CourtSearch />
      </MemoryRouter>
    );

    expect(await screen.findByText('Tìm sân thể thao')).toBeInTheDocument();
    expect(await screen.findByRole('option', { name: 'Bóng đá' })).toBeInTheDocument();
  });

  test('searches and renders matching court cards', async () => {
    sportTypeService.getActiveSportTypes.mockResolvedValue({
      data: { success: true, data: [{ id: 1, name: 'Bóng đá' }] },
    });
    courtService.searchCourts.mockResolvedValue({
      data: {
        success: true,
        data: [
          {
            id: 5,
            name: 'Sân A',
            facility: {
              name: 'Cơ sở ABC',
              address: 'Quận 1',
              avgRating: 4.5,
              totalReviews: 12,
            },
            sportType: { name: 'Bóng đá' },
            surfaceType: 'Cỏ nhân tạo',
            isIndoor: true,
          },
        ],
      },
    });

    render(
      <MemoryRouter>
        <CourtSearch />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Nhập địa chỉ hoặc quận...'), {
      target: { value: 'Quận 1' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Tìm kiếm' }));

    await waitFor(() => expect(courtService.searchCourts).toHaveBeenCalledWith('Quận 1', null));
    expect(await screen.findByText('Sân A')).toBeInTheDocument();
    expect(screen.getByText('Xem chi tiết và đặt sân')).toBeInTheDocument();
  });
});
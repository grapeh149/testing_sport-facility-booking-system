import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import FacilityDetail from '../../FacilityDetail';

jest.useFakeTimers();

jest.mock('../../../services/facilityService', () => ({
  getFacilityDetail: jest.fn(),
  getFacilityImages: jest.fn(),
}));

jest.mock('../../../services/courtService', () => ({
  getCourtsByFacility: jest.fn(),
}));

jest.mock('../../../services/reviewService', () => ({
  getReviewsByFacility: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ id: '1' }),
}));

const facilityService = require('../../../services/facilityService');
const courtService = require('../../../services/courtService');
const reviewService = require('../../../services/reviewService');

describe('FacilityDetail', () => {
  beforeEach(() => {
    facilityService.getFacilityDetail.mockReset();
    facilityService.getFacilityImages.mockReset();
    courtService.getCourtsByFacility.mockReset();
    reviewService.getReviewsByFacility.mockReset();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.clearAllTimers();
  });

  test('renders facility info, courts, reviews, and image slider', async () => {
    facilityService.getFacilityDetail.mockResolvedValue({
      data: { data: { id: 1, name: 'Facility A', address: '123 Street', district: 'District 1', city: 'HCM', phone: '0900', status: 'APPROVED', avgRating: 4.5, totalReviews: 3, description: 'Nice place' } },
    });
    courtService.getCourtsByFacility.mockResolvedValue({
      data: { data: [
        { id: 10, name: 'Court Active', surfaceType: 'Cỏ nhân tạo', isIndoor: true, isActive: true, sportType: { iconUrl: '' }, description: 'Main court' },
        { id: 11, name: 'Court Closed', surfaceType: 'Sàn gỗ', isIndoor: false, isActive: false, sportType: { iconUrl: '' }, description: 'Secondary' },
      ] },
    });
    reviewService.getReviewsByFacility.mockResolvedValue({
      data: { data: { content: [{ id: 1, customerName: 'User 1', rating: 5, comment: 'Great', createdAt: '2024-01-01' }] } },
    });
    facilityService.getFacilityImages.mockResolvedValue({
      data: { data: [{ imageUrl: 'https://example.com/a.jpg' }, { imageUrl: 'https://example.com/b.jpg' }] },
    });

    render(
      <MemoryRouter>
        <FacilityDetail />
      </MemoryRouter>
    );

    expect(await screen.findByText('Facility A')).toBeInTheDocument();
    expect(screen.getByText('Court Active')).toBeInTheDocument();
    expect(screen.getByText('Court Closed')).toBeInTheDocument();
    expect(screen.getByText('Great')).toBeInTheDocument();
    expect(screen.getByText('1/2')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: '>' }));
    expect(screen.getByText('2/2')).toBeInTheDocument();
  });

  test('shows empty-state alerts when no courts or reviews exist', async () => {
    facilityService.getFacilityDetail.mockResolvedValue({
      data: { data: { id: 1, name: 'Facility A', address: '123 Street', district: 'District 1', city: 'HCM', phone: '0900', status: 'PENDING' } },
    });
    courtService.getCourtsByFacility.mockResolvedValue({ data: { data: [] } });
    reviewService.getReviewsByFacility.mockResolvedValue({ data: { data: { content: [] } } });
    facilityService.getFacilityImages.mockResolvedValue({ data: { data: [] } });

    render(
      <MemoryRouter>
        <FacilityDetail />
      </MemoryRouter>
    );

    expect(await screen.findByText('Không có sân nào trong cơ sở này')).toBeInTheDocument();
    expect(screen.getByText('Chưa có đánh giá nào')).toBeInTheDocument();
  });
});
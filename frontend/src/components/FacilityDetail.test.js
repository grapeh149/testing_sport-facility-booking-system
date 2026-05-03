import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import FacilityDetail from './FacilityDetail';

jest.mock('../services/facilityService', () => ({
  getFacilityDetail: jest.fn(),
  getFacilityImages: jest.fn()
}));

jest.mock('../services/courtService', () => ({
  getCourtsByFacility: jest.fn()
}));

jest.mock('../services/reviewService', () => ({
  getReviewsByFacility: jest.fn()
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ id: '1' })
}));

describe('FacilityDetail Component', () => {
  test('renders facility details after loading', async () => {
    const facilityData = {
      id: 1,
      name: 'Test Facility',
      address: '123 Test Street',
      district: 'Test District',
      city: 'Test City',
      phone: '0123456789',
      status: 'APPROVED',
      avgRating: 4.5,
      totalReviews: 12,
      description: 'Test description'
    };

    const facilityService = require('../services/facilityService');
    const courtService = require('../services/courtService');
    const reviewService = require('../services/reviewService');

    facilityService.getFacilityDetail.mockResolvedValue({ data: { data: facilityData } });
    facilityService.getFacilityImages.mockResolvedValue({ data: { data: [{ imageUrl: 'https://example.com/image1.jpg' }] } });
    courtService.getCourtsByFacility.mockResolvedValue({ data: { data: [] } });
    reviewService.getReviewsByFacility.mockResolvedValue({ data: { data: { content: [] } } });

    render(
      <MemoryRouter>
        <FacilityDetail />
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText(/Test Facility/i)).toBeInTheDocument());
    expect(screen.getByText(/123 Test Street/i)).toBeInTheDocument();
    expect(screen.getByText(/Đã duyệt/i)).toBeInTheDocument();
  });
});

import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import OwnerDashboard from '../components/OwnerDashboard';

jest.mock('../services/facilityService', () => ({
  getAllFacilitiesByOwner: jest.fn()
}));
jest.mock('../services/cloudinaryService', () => ({
  uploadImage: jest.fn()
}));

jest.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { userId: 1, role: 'OWNER' },
    isAuthenticated: true
  })
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn()
}));

describe('OwnerDashboard Component', () => {
  test('should be importable', () => {
    expect(OwnerDashboard).toBeDefined();
  });
});

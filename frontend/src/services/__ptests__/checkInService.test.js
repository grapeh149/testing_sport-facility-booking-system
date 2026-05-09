import checkInService from '../checkInService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    post: jest.fn(),
  },
}));

describe('checkInService', () => {
  test('posts booking and user ids with note', () => {
    checkInService.checkInBooking(11, 22, 'Arrived');

    expect(apiClient.post).toHaveBeenCalledWith(
      '/api/checkins',
      { bookingId: 11, note: 'Arrived' },
      { params: { bookingId: 11, checkedByUserId: 22 } }
    );
  });
});
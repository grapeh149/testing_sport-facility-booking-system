import reviewService from '../reviewService';
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

describe('reviewService', () => {
  test('uses the expected review endpoints', () => {
    reviewService.getReviewsByFacility(5, 0, 10);
    reviewService.createReview(11, { rating: 5 });
    reviewService.replyToReview(3, 'Cảm ơn');

    expect(apiClient.get).toHaveBeenCalledWith('/api/reviews/facility/5', { params: { page: 0, size: 10 } });
    expect(apiClient.post).toHaveBeenCalledWith('/api/reviews', { rating: 5 }, { params: { bookingId: 11 } });
    expect(apiClient.put).toHaveBeenCalledWith('/api/reviews/3/reply', {}, { params: { reply: 'Cảm ơn' } });
  });
});
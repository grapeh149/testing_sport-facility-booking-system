import courtService from '../courtService';
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

describe('courtService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('searches courts with trimmed address and optional sport type', () => {
    courtService.searchCourts('  Quận 1  ', 4);
    courtService.searchCourts('', null);

    expect(apiClient.get).toHaveBeenCalledWith('/api/courts/search', {
      params: { address: 'Quận 1', sportTypeId: 4 },
    });
    expect(apiClient.get).toHaveBeenCalledWith('/api/courts/search', { params: {} });
  });
});
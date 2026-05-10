import sportTypeService from '../sportTypeService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
  },
}));

describe('sportTypeService', () => {
  test('calls active, detail and write endpoints', () => {
    sportTypeService.getActiveSportTypes();
    sportTypeService.getSportTypeById(2);
    sportTypeService.createSportType({ name: 'Bóng rổ' });
    sportTypeService.updateSportType(2, { name: 'Bóng chuyền' });

    expect(apiClient.get).toHaveBeenCalledWith('/api/sport-types');
    expect(apiClient.get).toHaveBeenCalledWith('/api/sport-types/2');
    expect(apiClient.post).toHaveBeenCalledWith('/api/sport-types', { name: 'Bóng rổ' });
    expect(apiClient.put).toHaveBeenCalledWith('/api/sport-types/2', { name: 'Bóng chuyền' });
  });
});
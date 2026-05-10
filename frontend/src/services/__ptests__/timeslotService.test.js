import timeslotService from '../timeslotService';
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

describe('timeslotService', () => {
  test('uses the expected timeslot endpoints', () => {
    timeslotService.getTimeslotsByCourt(6);
    timeslotService.getTimeslotDetail(7);
    timeslotService.createTimeslot({ courtId: 1 });
    timeslotService.updateTimeslot(8, { startTime: '08:00:00' });
    timeslotService.deleteTimeslot(9);

    expect(apiClient.get).toHaveBeenCalledWith('/api/timeslots/court/6');
    expect(apiClient.get).toHaveBeenCalledWith('/api/timeslots/7');
    expect(apiClient.post).toHaveBeenCalledWith('/api/timeslots', { courtId: 1 });
    expect(apiClient.put).toHaveBeenCalledWith('/api/timeslots/8', { startTime: '08:00:00' });
    expect(apiClient.delete).toHaveBeenCalledWith('/api/timeslots/9');
  });
});
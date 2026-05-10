import apiClient from './api';

const checkInService = {
  checkInBooking: (bookingId, checkedByUserId, note = '') => {
    return apiClient.post('/api/checkins', {
      bookingId,
      note,
    }, {
      params: {
        bookingId,
        checkedByUserId,
      },
    });
  },
};

export default checkInService;

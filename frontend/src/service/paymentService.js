import apiClient from './api';

const paymentService = {
  createVNPayPaymentUrl: (bookingId, returnUrl, bankCode) => {
    return apiClient.post(`/api/payments/${bookingId}/vnpay`, null, {
      params: {
        returnUrl,
        ...(bankCode ? { bankCode } : {}),
      },
    });
  },

  verifyVNPayReturn: (queryParams) => {
    return apiClient.get('/api/payments/vnpay/return', {
      params: queryParams,
    });
  },

  getPaymentsByBooking: (bookingId) => {
    return apiClient.get(`/api/payments/booking/${bookingId}`);
  },
};

export default paymentService;

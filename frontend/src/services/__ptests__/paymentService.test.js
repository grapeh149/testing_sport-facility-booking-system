import paymentService from '../paymentService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
  },
}));

describe('paymentService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('builds VNPay requests with params', () => {
    paymentService.createVNPayPaymentUrl(9, 'http://return.test', 'NCB');
    paymentService.verifyVNPayReturn({ vnp_TxnRef: 'TXN' });
    paymentService.getPaymentsByBooking(3);

    expect(apiClient.post).toHaveBeenCalledWith('/api/payments/9/vnpay', null, {
      params: { returnUrl: 'http://return.test', bankCode: 'NCB' },
    });
    expect(apiClient.get).toHaveBeenCalledWith('/api/payments/vnpay/return', {
      params: { vnp_TxnRef: 'TXN' },
    });
    expect(apiClient.get).toHaveBeenCalledWith('/api/payments/booking/3');
  });
});
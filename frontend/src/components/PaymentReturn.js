import React, { useEffect, useState } from 'react';
import { Container, Card, Alert, Button, Spinner } from 'react-bootstrap';
import { Link, useLocation } from 'react-router-dom';
import paymentService from '../services/paymentService';

const PaymentReturn = () => {
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  const unwrapApiResponse = (response, fallbackMessage) => {
    const body = response?.data;
    if (body && typeof body.success === 'boolean') {
      if (!body.success) {
        throw new Error(body.message || fallbackMessage || 'Yeu cau that bai');
      }
      return body.data;
    }
    return body?.data || body;
  };

  useEffect(() => {
    const verifyPayment = async () => {
      try {
        setLoading(true);
        const params = Object.fromEntries(new URLSearchParams(location.search));
        if (!params.vnp_TxnRef) {
          throw new Error('Thiếu thông tin giao dịch VNPay');
        }

        const response = await paymentService.verifyVNPayReturn(params);
        const payload = unwrapApiResponse(response, 'Xac minh ket qua thanh toan that bai');
        setResult(payload);
      } catch (err) {
        setError(err.response?.data?.message || err.message || 'Không thể xác minh kết quả thanh toán');
      } finally {
        setLoading(false);
      }
    };

    verifyPayment();
  }, [location.search]);

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang xác minh thanh toán...</span>
        </Spinner>
      </Container>
    );
  }

  const isSuccess = result?.status === 'SUCCESS';

  return (
    <Container className="py-5 d-flex justify-content-center">
      <Card style={{ width: '100%', maxWidth: '640px' }} className="shadow-sm">
        <Card.Body>
          <h3 className="mb-3">Kết Quả Thanh Toán VNPay</h3>

          {error && <Alert variant="danger">{error}</Alert>}

          {!error && (
            <Alert variant={isSuccess ? 'success' : 'danger'}>
              {isSuccess ? 'Thanh toán thành công!' : 'Thanh toán không thành công.'}
            </Alert>
          )}

          {result && (
            <div className="mb-3">
              <p className="mb-1"><strong>Mã giao dịch:</strong> {result.vnpayTxnRef || 'N/A'}</p>
              <p className="mb-1"><strong>Trạng thái:</strong> {result.status || 'N/A'}</p>
              <p className="mb-1"><strong>Số tiền:</strong> {Number(result.amount || 0).toLocaleString('vi-VN')} VNĐ</p>
              <p className="mb-1"><strong>Mã ngân hàng:</strong> {result.bankCode || 'N/A'}</p>
              <p className="mb-0"><strong>Booking ID:</strong> {result.bookingId || 'N/A'}</p>
            </div>
          )}

          <div className="d-flex gap-2">
            <Link to="/my-bookings" className="w-100">
              <Button variant="primary" className="w-100">Về Đặt Sân Của Tôi</Button>
            </Link>
            <Link to="/" className="w-100">
              <Button variant="outline-secondary" className="w-100">Về Trang Chủ</Button>
            </Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PaymentReturn;

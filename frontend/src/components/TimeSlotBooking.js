import React, { useState } from 'react';
import { Modal, Button, Card, Row, Col, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import bookingService from '../services/bookingService';
import paymentService from '../services/paymentService';

const TimeSlotBooking = ({ show, onHide, court, timeslot, bookingDate }) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

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

  if (!court || !timeslot) {
    return null;
  }

  const totalPrice = parseInt(timeslot.price) || 0;
  const depositRate = timeslot.depositRate || 30;
  const depositAmount = Math.round(totalPrice * depositRate / 100);
  const remainingAmount = totalPrice - depositAmount;

  const isPastBookingTime = () => {
    if (!bookingDate || !timeslot?.startTime) {
      return true;
    }

    const [startHour, startMin, startSec] = timeslot.startTime.split(':').map(Number);
    const bookingStart = new Date(bookingDate);
    bookingStart.setHours(startHour, startMin, startSec || 0, 0);

    return new Date() >= bookingStart;
  };

  const requireAuthForBooking = () => {
    if (isAuthenticated) {
      return true;
    }

    setError('Vui lòng đăng nhập để đặt sân hoặc thêm vào sân của tôi.');
    setTimeout(() => {
      navigate('/login');
    }, 600);
    return false;
  };

  const handleDeferPayment = async () => {
    if (!requireAuthForBooking()) {
      return;
    }

    if (isPastBookingTime()) {
      setError('Đặt sân vào khung giờ quá khứ không thành công. Vui lòng chọn khung giờ khác.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      // Create booking with PENDING_PAYMENT status
      const bookingData = {
        courtId: court.id,
        timeSlotId: timeslot.id,
        bookingDate: bookingDate,
        note: `Đặt cọc ${depositRate}%`,
      };

      await bookingService.createBooking(bookingData);

      setSuccess('Đã thêm vào sân của tôi! Bạn có thể thanh toán sau.');
      
      // Close modal after 2 seconds
      setTimeout(() => {
        onHide();
        navigate('/my-bookings');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Lỗi khi thêm vào sân của tôi');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentMOMO = async () => {
    if (!requireAuthForBooking()) {
      return;
    }

    if (isPastBookingTime()) {
      setError('Đặt sân vào khung giờ quá khứ không thành công. Vui lòng chọn khung giờ khác.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      // Create booking with PENDING_PAYMENT status
      const bookingData = {
        courtId: court.id,
        timeSlotId: timeslot.id,
        bookingDate: bookingDate,
        note: `Thanh toán qua VNPay`,
      };

      const response = await bookingService.createBooking(bookingData);
      const booking = unwrapApiResponse(response, 'Tao booking that bai');

      // Redirect to payment
      if (booking?.id) {
        // Create VNPay payment URL
        const returnUrl = `${window.location.origin}/payment/vnpay-return`;
        const paymentRes = await paymentService.createVNPayPaymentUrl(booking.id, returnUrl);
        const paymentUrl = unwrapApiResponse(paymentRes, 'Khong tao duoc URL thanh toan');
        if (!paymentUrl) {
          throw new Error('Khong tao duoc URL thanh toan');
        }
        window.location.href = paymentUrl;
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể tạo link thanh toán');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered size="md">
      <Modal.Header closeButton>
        <Modal.Title>Chi tiết đặt sân</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Card className="mb-4 border-light">
          <Card.Body>
            <Row className="mb-3">
              <Col xs={6}>
                <p className="mb-2">
                  <strong>Sân:</strong>
                </p>
                <p className="text-muted mb-0">{court.name}</p>
              </Col>
              <Col xs={6}>
                <p className="mb-2">
                  <strong>Ngày:</strong>
                </p>
                <p className="text-muted mb-0">
                  {new Date(bookingDate).toLocaleDateString('vi-VN')}
                </p>
              </Col>
            </Row>

            <Row className="mb-3">
              <Col xs={6}>
                <p className="mb-2">
                  <strong>Giờ:</strong>
                </p>
                <p className="text-muted mb-0">
                  {timeslot.startTime} - {timeslot.endTime}
                </p>
              </Col>
              <Col xs={6}>
                <p className="mb-2">
                  <strong>Mặt sân:</strong>
                </p>
                <p className="text-muted mb-0">{court.surfaceType || '(Không rõ)'}</p>
              </Col>
            </Row>
          </Card.Body>
        </Card>

        <Card className="border-success mb-4">
          <Card.Body>
            <h6 className="mb-3" style={{ color: '#198754' }}>
              💰 Chi tiết thanh toán
            </h6>

            <Row className="mb-2">
              <Col xs={6}>
                <p className="mb-0">Tổng tiền:</p>
              </Col>
              <Col xs={6} className="text-end">
                <p className="mb-0">
                  <strong>{totalPrice.toLocaleString('vi-VN')} đ</strong>
                </p>
              </Col>
            </Row>

            <Row className="mb-3 pb-3" style={{ borderBottom: '1px solid #dee2e6' }}>
              <Col xs={6}>
                <p className="mb-0" style={{ color: '#dc3545', fontWeight: 'bold' }}>
                  Đặt cọc {depositRate}%:
                </p>
              </Col>
              <Col xs={6} className="text-end">
                <p className="mb-0" style={{ color: '#dc3545', fontWeight: 'bold' }}>
                  {depositAmount.toLocaleString('vi-VN')} đ
                </p>
              </Col>
            </Row>

            <Row>
              <Col xs={6}>
                <p className="mb-0" style={{ color: '#6c757d' }}>
                  Còn lại:
                </p>
              </Col>
              <Col xs={6} className="text-end">
                <p className="mb-0" style={{ color: '#6c757d' }}>
                  {remainingAmount.toLocaleString('vi-VN')} đ
                </p>
              </Col>
            </Row>
          </Card.Body>
        </Card>

        <div className="alert alert-info mb-3" role="alert">
          <strong>ℹ️ Lưu ý:</strong>
          <ul className="mb-0 mt-2">
            <li>Bạn cần thanh toán đặt cọc {depositRate}% = <strong>{depositAmount.toLocaleString('vi-VN')} đ</strong></li>
            <li>Phần còn lại thanh toán tại sân</li>
            <li>Hoặc thêm vào "sân của tôi" để thanh toán sau</li>
          </ul>
        </div>
      </Modal.Body>

      <Modal.Footer>
        <Button variant="outline-secondary" onClick={onHide} disabled={loading}>
          Hủy
        </Button>
        <Button
          variant="outline-primary"
          onClick={handleDeferPayment}
          disabled={loading}
        >
          {loading ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" />
              Đang xử lý...
            </>
          ) : (
            'Thêm vào sân của tôi'
          )}
        </Button>
        <Button
          variant="success"
          onClick={handlePaymentMOMO}
          disabled={loading}
        >
          {loading ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" />
              Đang xử lý...
            </>
          ) : (
            'Thanh toán VNPay'
          )}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default TimeSlotBooking;

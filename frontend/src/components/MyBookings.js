import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import bookingService from '../services/bookingService';
import paymentService from '../services/paymentService';
import ReviewModal from './ReviewModal';

const MyBookings = () => {
  const { user, isAuthenticated } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState(null);

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

  const isBookingExpired = (booking) => {
    if (!booking?.bookingDate || !booking?.endTime) {
      return false;
    }

    const bookingEnd = new Date(`${booking.bookingDate}T${booking.endTime}`);
    if (Number.isNaN(bookingEnd.getTime())) {
      return false;
    }

    return new Date() > bookingEnd;
  };

  useEffect(() => {
    if (isAuthenticated) {
      loadBookings();
    } else {
      setBookings([]);
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, isAuthenticated]);

  const loadBookings = async () => {
    try {
      setLoading(true);
      const response = await bookingService.getMyBookings(page, 10);
      const payload = unwrapApiResponse(response, 'Khong the tai danh sach dat san') || {};
      const bookingList = payload.content || payload || [];
      setBookings(Array.isArray(bookingList) ? bookingList : []);
      setTotalPages(payload.totalPages || 0);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách đặt sân');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (bookingId) => {
    if (window.confirm('Bạn chắc chắn muốn hủy đặt sân này?')) {
      try {
        await bookingService.cancelBooking(bookingId);
        setError('');
        loadBookings();
        alert('Hủy đặt sân thành công');
      } catch (err) {
        setError('Không thể hủy đặt sân');
      }
    }
  };

  const handlePayNow = async (bookingId) => {
    try {
      setLoading(true);
      const returnUrl = `${window.location.origin}/payment/vnpay-return`;
      const response = await paymentService.createVNPayPaymentUrl(bookingId, returnUrl);
      const paymentUrl = unwrapApiResponse(response, 'Khong tao duoc URL thanh toan');
      if (!paymentUrl) {
        throw new Error('Khong tao duoc URL thanh toan');
      }
      window.location.href = paymentUrl;
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể khởi tạo thanh toán');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenReviewModal = (booking) => {
    setSelectedBooking(booking);
    setShowReviewModal(true);
  };

  const handleReviewSuccess = () => {
    setShowReviewModal(false);
    setSelectedBooking(null);
    loadBookings(); // Tải lại danh sách đặt sân
  };

  const getDisplayStatus = (booking) => {
    if (isBookingExpired(booking) && (booking.status === 'PENDING_PAYMENT' || booking.status === 'PENDING_CONFIRM' || booking.status === 'PENDING')) {
      return { variant: 'dark', label: 'Đã hết hạn' };
    }

    const map = {
      PENDING: { variant: 'warning', label: 'Chờ xác nhận' },
      PENDING_PAYMENT: { variant: 'warning', label: 'Chờ thanh toán' },
      PENDING_CONFIRM: { variant: 'warning', label: 'Chờ xác nhận' },
      CONFIRMED: { variant: 'success', label: 'Đã xác nhận' },
      CHECKED_IN: { variant: 'info', label: 'Đã check-in' },
      COMPLETED: { variant: 'info', label: 'Hoàn tất' },
      CANCELLED: { variant: 'danger', label: 'Đã hủy' },
      REJECTED: { variant: 'danger', label: 'Bị từ chối' },
    };

    return map[booking.status] || { variant: 'secondary', label: booking.status || 'N/A' };
  };

  return (
    <Container className="py-5">
      <h1 className="mb-2">My Bookings</h1>
      {user?.fullName && <p className="text-muted">Xin chào {user.fullName}</p>}

      {error && <Alert variant="danger">{error}</Alert>}

      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Đang tải...</span>
          </Spinner>
        </div>
      ) : bookings.length === 0 ? (
        <Alert variant="info">Bạn chưa có đặt sân nào</Alert>
      ) : (
        <>
          <Row>
            {bookings.map((booking) => (
              <Col md={6} className="mb-4" key={booking.id}>
                <Card className="h-100">
                  {/* {isBookingExpired(booking) && (booking.status === 'PENDING_PAYMENT' || booking.status === 'PENDING_CONFIRM' || booking.status === 'PENDING') && (
                    <div className="px-3 pt-3">
                      <Alert variant="secondary" className="mb-0 py-2">
                        Đã hết hạn
                      </Alert>
                    </div>
                  )} */}
                  <Card.Body>
                    <div className="d-flex justify-content-between mb-2">
                      <Card.Title>Mã đơn: {booking.bookingCode || `#${booking.id}`}</Card.Title>
                      <Badge bg={getDisplayStatus(booking).variant}>
                        {getDisplayStatus(booking).label}
                      </Badge>
                    </div>

                    <Card.Text>
                      <strong>Tên cơ sở:</strong> {booking.facilityName || 'N/A'}
                    </Card.Text>

                    <Card.Text>
                      <strong>Tên sân:</strong> {booking.courtName || 'N/A'}
                    </Card.Text>

                    <Card.Text>
                      <strong>Ngày:</strong>{' '}
                      {booking.bookingDate ? new Date(booking.bookingDate).toLocaleDateString('vi-VN') : 'N/A'}
                    </Card.Text>

                    <Card.Text>
                      <strong>Giờ:</strong> {booking.startTime || 'N/A'} - {booking.endTime || 'N/A'}
                    </Card.Text>

                    <Card.Text>
                      <strong>Giá:</strong>{' '}
                      {Number(booking.totalPrice || 0).toLocaleString('vi-VN')} VNĐ
                    </Card.Text>

                    <Card.Text>
                      <strong>Tiền cọc:</strong>{' '}
                      {Number(booking.depositAmount || 0).toLocaleString('vi-VN')} VNĐ
                    </Card.Text>
                  </Card.Body>

                  <Card.Footer className="bg-white">
                    {booking.status === 'CHECKED_IN' && !booking.hasReview && (
                      <Button
                        variant="outline-secondary"
                        size="sm"
                        onClick={() => handleOpenReviewModal(booking)}
                        className="w-100 mb-2"
                      >
                        Đánh giá sân
                      </Button>
                    )}
                    {booking.status === 'CHECKED_IN' && booking.hasReview && (
                      <Button
                        variant="secondary"
                        size="sm"
                        className="w-100 mb-2"
                        disabled
                      >
                        Đã đánh giá
                      </Button>
                    )}
                    {booking.status === 'PENDING_PAYMENT' && !isBookingExpired(booking) && (
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => handlePayNow(booking.id)}
                        className="w-100 mb-2"
                      >
                        Thanh toán VNPay
                      </Button>
                    )}
                    {/* {booking.status === 'PENDING_PAYMENT' && isBookingExpired(booking) && (
                      <Button variant="secondary" size="sm" className="w-100 mb-2" disabled>
                        Đã hết hạn
                      </Button>
                    )} */}
                    {(booking.status === 'PENDING' || booking.status === 'PENDING_PAYMENT' || booking.status === 'PENDING_CONFIRM' || booking.status === 'CONFIRMED') && (
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleCancel(booking.id)}
                        className="w-100"
                      >
                        Hủy đặt sân
                      </Button>
                    )}
                    {(booking.status === 'COMPLETED' || booking.status === 'CANCELLED' || booking.status === 'REJECTED') && (
                      <small className="text-muted">
                        Không thể thao tác
                      </small>
                    )}
                    {isBookingExpired(booking) && (booking.status === 'PENDING' || booking.status === 'PENDING_PAYMENT' || booking.status === 'PENDING_CONFIRM') && (
                      <small className="text-muted d-block mt-2">
                        Quá thời gian thanh toán.
                      </small>
                    )}
                  </Card.Footer>
                </Card>
              </Col>
            ))}
          </Row>

          {/* Pagination */}
          {totalPages > 1 && (
            <nav className="mt-4">
              <ul className="pagination justify-content-center">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <Button
                    variant="light"
                    onClick={() => setPage(Math.max(0, page - 1))}
                    disabled={page === 0}
                  >
                    Trước
                  </Button>
                </li>
                <li className="page-item active mx-2">
                  <span className="page-link">
                    Trang {page + 1} / {totalPages}
                  </span>
                </li>
                <li
                  className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''
                    }`}
                >
                  <Button
                    variant="light"
                    onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                    disabled={page >= totalPages - 1}
                  >
                    Tiếp
                  </Button>
                </li>
              </ul>
            </nav>
          )}
        </>
      )}

      {/* Review Modal */}
      {selectedBooking && (
        <ReviewModal
          show={showReviewModal}
          onHide={() => {
            setShowReviewModal(false);
            setSelectedBooking(null);
          }}
          booking={selectedBooking}
          onSuccess={handleReviewSuccess}
        />
      )}
    </Container>
  );
};

export default MyBookings;

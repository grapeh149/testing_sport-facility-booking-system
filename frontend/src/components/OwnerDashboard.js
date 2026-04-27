import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Table, Spinner, Alert, Badge, Modal, Form } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import facilityService from '../services/facilityService';
import courtService from '../services/courtService';
import bookingService from '../services/bookingService';

const OwnerDashboard = () => {
  const { user } = useAuth();
  const [facilities, setFacilities] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [stats, setStats] = useState({
    totalFacilities: 0,
    totalBookings: 0,
    totalRevenue: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState(null);

  useEffect(() => {
    loadOwnerData();
  }, [user?.userId]);

  const loadOwnerData = async () => {
    try {
      setLoading(true);
      setError('');

      if (!user?.userId) return;

      // Load facilities
      const facilitiesRes = await facilityService.getOwnerFacilities(user.userId);
      if (facilitiesRes.data.success) {
        setFacilities(facilitiesRes.data.data || []);
        setStats(prev => ({
          ...prev,
          totalFacilities: facilitiesRes.data.data?.length || 0,
        }));
      }

      // Load bookings
      const bookingsRes = await bookingService.getOwnerAllBookings();
      if (bookingsRes.data.success) {
        setBookings(bookingsRes.data.data || []);
        setStats(prev => ({
          ...prev,
          totalBookings: bookingsRes.data.data?.length || 0,
        }));
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải dữ liệu');
      console.error('Error loading owner data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmBooking = async (bookingId) => {
    try {
      await bookingService.confirmBooking(bookingId, user.userId);
      setShowModal(false);
      loadOwnerData();
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể xác nhận đặt sân');
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <h2 className="mb-4">Bảng điều khiển chủ sân</h2>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row className="mb-4">
        <Col md={4}>
          <Card>
            <Card.Body>
              <h6 className="text-muted">Tổng sân</h6>
              <h3>{stats.totalFacilities}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card>
            <Card.Body>
              <h6 className="text-muted">Tổng đặt sân</h6>
              <h3>{stats.totalBookings}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card>
            <Card.Body>
              <h6 className="text-muted">Tổng doanh thu</h6>
              <h3>{stats.totalRevenue?.toLocaleString('vi-VN')} ₫</h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="mb-4">
        <Card.Header>
          <h5 className="mb-0">Các sân của tôi</h5>
        </Card.Header>
        <Card.Body>
          {facilities.length > 0 ? (
            <Table striped hover>
              <thead>
                <tr>
                  <th>Tên sân</th>
                  <th>Địa chỉ</th>
                  <th>Trạng thái</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {facilities.map((facility) => (
                  <tr key={facility.id}>
                    <td>{facility.name}</td>
                    <td>{facility.address}</td>
                    <td>
                      <Badge bg={facility.status === 'APPROVED' ? 'success' : 'warning'}>
                        {facility.status}
                      </Badge>
                    </td>
                    <td>
                      <Button size="sm" variant="primary" href={`/facility/${facility.id}`}>
                        Xem chi tiết
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <p className="text-muted">Chưa có sân nào</p>
          )}
        </Card.Body>
      </Card>

      <Card>
        <Card.Header>
          <h5 className="mb-0">Đặt sân gần đây</h5>
        </Card.Header>
        <Card.Body>
          {bookings.length > 0 ? (
            <Table striped hover>
              <thead>
                <tr>
                  <th>Ngày đặt</th>
                  <th>Khách hàng</th>
                  <th>Sân</th>
                  <th>Trạng thái</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {bookings.slice(0, 10).map((booking) => (
                  <tr key={booking.id}>
                    <td>{new Date(booking.bookingDate).toLocaleDateString('vi-VN')}</td>
                    <td>{booking.user?.fullName || 'N/A'}</td>
                    <td>{booking.court?.name || 'N/A'}</td>
                    <td>
                      <Badge bg={booking.status === 'CONFIRMED' ? 'success' : 'warning'}>
                        {booking.status}
                      </Badge>
                    </td>
                    <td>
                      {booking.status === 'PENDING' && (
                        <Button 
                          size="sm" 
                          variant="success"
                          onClick={() => {
                            setSelectedBooking(booking);
                            setShowModal(true);
                          }}
                        >
                          Xác nhận
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <p className="text-muted">Chưa có đặt sân nào</p>
          )}
        </Card.Body>
      </Card>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận đặt sân</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedBooking && (
            <>
              <p><strong>Khách hàng:</strong> {selectedBooking.user?.fullName}</p>
              <p><strong>Sân:</strong> {selectedBooking.court?.name}</p>
              <p><strong>Ngày:</strong> {new Date(selectedBooking.bookingDate).toLocaleDateString('vi-VN')}</p>
              <p><strong>Giờ:</strong> {selectedBooking.timeslot?.startTime} - {selectedBooking.timeslot?.endTime}</p>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Hủy
          </Button>
          <Button 
            variant="primary" 
            onClick={() => handleConfirmBooking(selectedBooking.id)}
          >
            Xác nhận
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default OwnerDashboard;

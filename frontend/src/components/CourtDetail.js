import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert, Badge } from 'react-bootstrap';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import courtService from '../services/courtService';
import timeslotService from '../services/timeslotService';
import bookingService from '../services/bookingService';
import '../components/CourtSearch.css';

const CourtDetail = () => {
  const { courtId } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const [court, setCourt] = useState(null);
  const [timeslots, setTimeslots] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCourtDetails();
  }, [courtId]);

  const loadCourtDetails = async () => {
    try {
      setLoading(true);
      setError('');
      
      const courtRes = await courtService.getCourtDetail(courtId);
      if (courtRes.data.success) {
        setCourt(courtRes.data.data);
      }

      const timeslotsRes = await timeslotService.getTimeslotsByCourt(courtId);
      if (timeslotsRes.data.success) {
        setTimeslots(timeslotsRes.data.data);
      }

      const bookingsRes = await bookingService.getCourtBookings(courtId);
      if (bookingsRes.data.success) {
        setBookings(bookingsRes.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải thông tin sân');
      console.error('Error loading court details:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBooking = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    navigate(`/booking/${courtId}`);
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

  if (error) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  if (!court) {
    return (
      <Container className="py-5">
        <Alert variant="warning">Không tìm thấy sân</Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row>
        <Col lg={8}>
          <Card className="mb-4">
            <Card.Body>
              <h2>{court.name}</h2>
              <p className="text-muted">{court.description}</p>
              
              {court.sportType && (
                <p>
                  <strong>Loại thể thao:</strong> {court.sportType.name}
                </p>
              )}

              <p>
                <strong>Giá:</strong> {court.price?.toLocaleString('vi-VN')} ₫ / giờ
              </p>

              <div className="mt-4">
                <h4>Khung giờ hoạt động</h4>
                {timeslots.length > 0 ? (
                  <div className="row">
                    {timeslots.map((slot) => (
                      <div key={slot.id} className="col-md-6 mb-2">
                        <Badge bg="info">
                          {slot.startTime} - {slot.endTime}
                        </Badge>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-muted">Không có khung giờ nào</p>
                )}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card>
            <Card.Body>
              <h5>Đặt sân</h5>
              <p className="text-muted">Giá: {court.price?.toLocaleString('vi-VN')} ₫ / giờ</p>
              <Button 
                variant="primary" 
                className="w-100"
                onClick={handleBooking}
              >
                Đặt sân ngay
              </Button>
              {!isAuthenticated && (
                <p className="text-muted small mt-2">
                  <Link to="/login">Đăng nhập</Link> để đặt sân
                </p>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default CourtDetail;

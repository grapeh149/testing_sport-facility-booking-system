import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Card, Alert, Spinner } from 'react-bootstrap';
import { useParams, useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import courtService from '../services/courtService';
import timeslotService from '../services/timeslotService';
import bookingService from '../services/bookingService';
import paymentService from '../services/paymentService';
import facilityService from '../services/facilityService';

const BookingForm = () => {
  const { courtId } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [court, setCourt] = useState(null);
  const [facility, setFacility] = useState(null);
  const [timeslots, setTimeslots] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [formData, setFormData] = useState({
    bookingDate: '',
    timeslotId: '',
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const deferPayment = new URLSearchParams(location.search).get('deferPayment') === '1';

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
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    loadCourtDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [courtId, isAuthenticated, navigate]);

  const loadCourtDetails = async () => {
    try {
      setLoading(true);
      const response = await courtService.getCourtDetail(courtId);
      const courtData = response.data.data;
      setCourt(courtData);

      // Load facility info
      if (courtData?.facilityId) {
        try {
          const facilityRes = await facilityService.getFacilityDetail(courtData.facilityId);
          setFacility(facilityRes.data?.data || facilityRes.data);
        } catch (facilityErr) {
          console.error('Error loading facility:', facilityErr);
        }
      }

      // Lấy danh sách khung giờ
      const timeslotsRes = await timeslotService.getTimeslotsByCourt(courtId);
      const allTimeslots = timeslotsRes.data.data || [];
      
      // Filter out past timeslots for today
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const now = new Date();
      
      const filteredTimeslots = allTimeslots.filter(ts => {
        // For today bookings, only show timeslots that haven't started yet
        if (formData.bookingDate) {
          const selectedDate = new Date(formData.bookingDate);
          selectedDate.setHours(0, 0, 0, 0);
          
          if (selectedDate.getTime() === today.getTime()) {
            const [startHour, startMin, startSec] = ts.startTime.split(':').map(Number);
            const timeslotStartTime = new Date();
            timeslotStartTime.setHours(startHour, startMin, startSec || 0);
            return timeslotStartTime > now; // Only future timeslots
          }
        }
        return true; // Show all timeslots for future dates
      });
      
      setTimeslots(filteredTimeslots);

      setError('');
    } catch (err) {
      setError('Không thể tải thông tin sân');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Calculate total price when timeslot is selected
    if (name === 'timeslotId' && value) {
      const timeslot = timeslots.find(ts => ts.id === parseInt(value));
      if (timeslot && court?.price) {
        // Calculate price based on timeslot duration
        // startTime and endTime are in HH:MM:SS format
        const startHour = parseInt(timeslot.startTime.split(':')[0]);
        const startMin = parseInt(timeslot.startTime.split(':')[1]);
        const endHour = parseInt(timeslot.endTime.split(':')[0]);
        const endMin = parseInt(timeslot.endTime.split(':')[1]);
        
        const startTotalMin = startHour * 60 + startMin;
        const endTotalMin = endHour * 60 + endMin;
        const durationMin = endTotalMin - startTotalMin;
        const durationHour = durationMin / 60;
        
        const total = Math.ceil(durationHour * court.price);
        setTotalPrice(total);
      }
    }

    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.bookingDate) {
      newErrors.bookingDate = 'Vui lòng chọn ngày';
    } else {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const selectedDate = new Date(formData.bookingDate);
      if (selectedDate < today) {
        newErrors.bookingDate = 'Không thể chọn ngày trong quá khứ';
      }
      
      // Validate time slot for today selections - only allow future timeslots
      if (selectedDate.getTime() === today.getTime() && formData.timeslotId) {
        const timeslot = timeslots.find(ts => ts.id === parseInt(formData.timeslotId));
        if (timeslot) {
          const now = new Date();
          const [startHour, startMin, startSec] = timeslot.startTime.split(':').map(Number);
          const timeslotStartTime = new Date();
          timeslotStartTime.setHours(startHour, startMin, startSec || 0);
          
          if (timeslotStartTime <= now) {
            newErrors.timeslotId = 'Khung giờ này đã bắt đầu hoặc đã qua. Vui lòng chọn khung giờ trong tương lai';
          }
        }
      }
    }

    if (!formData.timeslotId) {
      newErrors.timeslotId = 'Vui lòng chọn khung giờ';
    }

    return newErrors;
  };

  const validateBookingDateTime = () => {
    // Check if current time is before the booking date + time
    const selectedDate = new Date(formData.bookingDate);
    const timeslot = timeslots.find(ts => ts.id === parseInt(formData.timeslotId));
    
    if (!selectedDate || !timeslot) {
      return { isValid: false, message: 'Dữ liệu không hợp lệ' };
    }

    const [startHour, startMin, startSec] = timeslot.startTime.split(':').map(Number);
    const bookingDateTime = new Date(selectedDate);
    bookingDateTime.setHours(startHour, startMin, startSec || 0);
    
    const now = new Date();
    
    console.log('🔍 Payment time validation:');
    console.log('   Now:', now.toLocaleString('vi-VN'));
    console.log('   Booking Date+Time:', bookingDateTime.toLocaleString('vi-VN'));
    console.log('   Valid:', now < bookingDateTime);

    if (now >= bookingDateTime) {
      return { 
        isValid: false, 
        message: 'Thời gian hiện tại đã vượt quá thời gian đặt sân. Vui lòng chọn thời gian khác.' 
      };
    }

    return { isValid: true };
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess('');
    setError('');

    const newErrors = validateForm();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Validate booking date/time against current time before payment
    const timeValidation = validateBookingDateTime();
    if (!timeValidation.isValid) {
      setError(timeValidation.message);
      return;
    }

    setSubmitting(true);

    try {
      const bookingResponse = await bookingService.createBooking({
        courtId: parseInt(courtId),
        timeSlotId: parseInt(formData.timeslotId),
        bookingDate: formData.bookingDate,
      });

      const bookingData = unwrapApiResponse(bookingResponse, 'Tao booking that bai');
      const bookingId = bookingData?.id;
      if (!bookingId) {
        throw new Error('Khong lay duoc bookingId de thanh toan');
      }

      if (deferPayment) {
        setSuccess('Đã thêm vào sân của tôi. Bạn có thể thanh toán cọc sau trong mục Đặt sân của tôi.');
        setTimeout(() => {
          navigate('/my-bookings');
        }, 1200);
      } else {
        const returnUrl = `${window.location.origin}/payment/vnpay-return`;
        const paymentResponse = await paymentService.createVNPayPaymentUrl(bookingId, returnUrl);
        const paymentUrl = unwrapApiResponse(paymentResponse, 'Tao URL thanh toan that bai');
        if (!paymentUrl) {
          throw new Error('Khong tao duoc URL thanh toan VNPay');
        }

        setSuccess('Đặt sân thành công! Đang chuyển hướng đến VNPay...');
        window.location.href = paymentUrl;
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tạo đặt sân');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
      </Container>
    );
  }

  if (error && !court) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{error}</Alert>
        <Link to="/">
          <Button variant="primary">Quay lại danh sách</Button>
        </Link>
      </Container>
    );
  }

  return (
    <Container className="d-flex justify-content-center align-items-center py-5" style={{ minHeight: '80vh' }}>
      <Card style={{ width: '100%', maxWidth: '560px' }}>
        <Card.Body>
          <h2 className="mb-2">Complete Your Booking</h2>
          <p className="text-muted mb-4">Xác nhận ngày, khung giờ và thanh toán đặt cọc.</p>

          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}

          <div className="mb-4 p-3 rounded" style={{ background: '#f1f6ff', border: '1px solid #dbe8ff' }}>
            <p className="mb-2">
              <strong>Sân:</strong> {court?.name}
            </p>
            <p className="mb-2">
              <strong>Tên cơ sở:</strong> {facility?.name || 'Đang tải...'}
            </p>
            <p className="mb-0">
              <strong>Giá:</strong> {(totalPrice > 0 ? totalPrice : court?.price)?.toLocaleString('vi-VN')} VNĐ{totalPrice > 0 ? '' : '/giờ'}
            </p>
          </div>

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Ngày đặt</Form.Label>
              <Form.Control
                type="date"
                name="bookingDate"
                value={formData.bookingDate}
                onChange={handleChange}
                isInvalid={!!errors.bookingDate}
                min={new Date().toISOString().split('T')[0]}
              />
              <Form.Control.Feedback type="invalid">
                {errors.bookingDate}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Khung giờ</Form.Label>
              <Form.Select
                name="timeslotId"
                value={formData.timeslotId}
                onChange={handleChange}
                isInvalid={!!errors.timeslotId}
              >
                <option value="">-- Chọn khung giờ --</option>
                {timeslots.map((slot) => (
                  <option key={slot.id} value={slot.id}>
                    {slot.startTime} - {slot.endTime}
                  </option>
                ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">
                {errors.timeslotId}
              </Form.Control.Feedback>
            </Form.Group>

            <Button variant="success" type="submit" className="w-100" disabled={submitting}>
              {submitting ? 'Đang xử lý...' : (deferPayment ? 'Thêm vào sân của tôi' : 'Đặt Sân & Thanh Toán')} 
            </Button>
          </Form>

          <Link to="/" className="d-block text-center mt-3">
            Quay lại
          </Link>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default BookingForm;

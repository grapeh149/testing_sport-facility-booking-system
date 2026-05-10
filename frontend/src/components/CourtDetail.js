import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert, Badge } from 'react-bootstrap';
import { useParams, Link } from 'react-router-dom';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { useAuth } from '../context/AuthContext';
import courtService from '../services/courtService';
import timeslotService from '../services/timeslotService';
import bookingService from '../services/bookingService';
import TimeSlotBooking from './TimeSlotBooking';
import './CourtDetail.css';

const CourtDetail = () => {
  const { courtId } = useParams();
  const { isAuthenticated } = useAuth();

  const [court, setCourt] = useState(null);
  const [timeslots, setTimeslots] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [selectedTimeslot, setSelectedTimeslot] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const calendarRef = React.useRef(null);

  useEffect(() => {
    loadCourtDetails();
    loadBookings();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [courtId, isAuthenticated]);

  useEffect(() => {
    generateCalendarEvents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [timeslots, bookings]);

  const loadCourtDetails = async () => {
    try {
      setLoading(true);
      const courtRes = await courtService.getCourtDetail(courtId);
      const courtData = courtRes.data.data || courtRes.data;
      setCourt(courtData);

      // Load time slots
      const timeslotsRes = await timeslotService.getTimeslotsByCourt(courtId);
      setTimeslots(timeslotsRes.data.data || timeslotsRes.data || []);

      setError('');
    } catch (err) {
      setError('Không thể tải thông tin sân');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const loadBookings = async () => {
    try {
      const courtBookingsRes = await bookingService.getCourtBookings(courtId);
      const courtBookings = courtBookingsRes.data?.data || courtBookingsRes.data?.data?.content || courtBookingsRes.data?.content || [];
      setBookings(Array.isArray(courtBookings) ? courtBookings : []);
    } catch (err) {
      console.error('Error loading bookings:', err);
      setBookings([]);
    }
  };

  const getTimeslotStatus = (timeslot, bookingDate) => {
    // Check if the slot is in the past
    const [startHour, startMin, startSec] = timeslot.startTime.split(':').map(Number);
    const bookingStart = new Date(bookingDate);
    bookingStart.setHours(startHour, startMin, startSec || 0, 0);

    if (new Date() >= bookingStart) {
      return 'booked';
    }

    const booking = bookings.find(b => {
      let bDateStr = b.bookingDate;
      if (Array.isArray(bDateStr)) {
        bDateStr = `${bDateStr[0]}-${String(bDateStr[1]).padStart(2, '0')}-${String(bDateStr[2]).padStart(2, '0')}`;
      }
      const bStartTime = b.startTime ? String(b.startTime).substring(0, 5) : '';
      const tStartTime = timeslot.startTime ? String(timeslot.startTime).substring(0, 5) : '';
      return bStartTime === tStartTime && bDateStr === bookingDate;
    });
    
    if (!booking) {
      return 'available';
    }

    if (booking.status === 'CONFIRMED' || booking.status === 'CHECKED_IN' || booking.status === 'COMPLETED') {
      return 'booked';
    }

    return 'available';
  };

  const generateCalendarEvents = () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    // Generate events for the next 30 days
    const calendarEvents = [];
    
    for (let dayOffset = 0; dayOffset < 30; dayOffset++) {
      const currentDate = new Date(today);
      currentDate.setDate(currentDate.getDate() + dayOffset);
      
      const year = currentDate.getFullYear();
      const month = String(currentDate.getMonth() + 1).padStart(2, '0');
      const day = String(currentDate.getDate()).padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;
      
      timeslots.forEach((timeslot) => {
        // Only show timeslots that match the day of the week
        if (timeslot.dayOfWeek !== null && timeslot.dayOfWeek !== undefined && Number(timeslot.dayOfWeek) !== currentDate.getDay()) {
          return;
        }

        const status = getTimeslotStatus(timeslot, dateStr);
        
        const eventDate = `${dateStr}T${timeslot.startTime}`;
        const endEventDate = `${dateStr}T${timeslot.endTime}`;
        
        let backgroundColor = '#28a745'; // available - green
        let borderColor = '#1e7e34';
        let title = `Trống - ${parseInt(timeslot.price).toLocaleString('vi-VN')}đ`;
        let textColor = '#fff';
        
        if (status === 'booked') {
          backgroundColor = '#dc3545'; // booked - red
          borderColor = '#bb2d3b';
          title = `Đã khóa - ${parseInt(timeslot.price).toLocaleString('vi-VN')}đ`;
          textColor = '#fff';
        }
        
        calendarEvents.push({
          id: `${timeslot.id}-${dateStr}`,
          title,
          start: eventDate,
          end: endEventDate,
          backgroundColor,
          borderColor,
          textColor,
          extendedProps: {
            timeslot,
            bookingDate: dateStr,
            status,
          },
        });
      });
    }
    
    setEvents(calendarEvents);
  };

  const handleEventClick = (info) => {
    const { timeslot, bookingDate, status } = info.event.extendedProps;
    
    if (status === 'available') {
      setSelectedTimeslot(timeslot);
      setSelectedDate(bookingDate);
      setShowBookingModal(true);
    }
  };

  const handleCloseBookingModal = () => {
    setShowBookingModal(false);
    setSelectedTimeslot(null);
    setSelectedDate(null);
    loadBookings();
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

  if (error || !court) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{error || 'Không tìm thấy sân'}</Alert>
        <Link to="/">
          <Button variant="primary">Quay lại danh sách</Button>
        </Link>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Link to="/" className="mb-4 d-inline-block">
        <Button variant="outline-secondary">Quay lại</Button>
      </Link>

      {/* Court Info */}
      <Card className="mb-4 shadow-sm">
        <Card.Body>
          <Row>
            <Col md={8}>
              <h1>{court.name}</h1>
              <p className="text-muted">{court.description || 'Không có mô tả'}</p>
              
              <Row className="mb-3">
                <Col md={6}>
                  <Card.Text>
                    <strong>Loại mặt sân:</strong> {court.surfaceType || '(Không rõ)'}
                  </Card.Text>
                </Col>
                <Col md={6}>
                  <Card.Text>
                    <strong>Loại:</strong> {court.isIndoor ? 'Trong nhà' : 'Ngoài trời'}
                  </Card.Text>
                </Col>
              </Row>

              <Card.Text>
                <Badge bg={court.isActive ? 'success' : 'secondary'}>
                  {court.isActive ? 'Có sẵn' : 'Không có sẵn'}
                </Badge>
              </Card.Text>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Date Picker */}
      <Card className="mb-4 shadow-sm">
        <Card.Body>
          <p className="mb-3 text-muted">
            <strong>Hướng dẫn:</strong> Click vào khung giờ "Trống" để đặt sân
          </p>
          <div style={{ 
            display: 'flex', 
            gap: '20px',
            flexWrap: 'wrap',
            alignItems: 'center'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ 
                width: '24px', 
                height: '24px', 
                backgroundColor: '#28a745',
                borderRadius: '4px'
              }}></div>
              <span>Trống</span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ 
                width: '24px', 
                height: '24px', 
                backgroundColor: '#dc3545',
                borderRadius: '4px'
              }}></div>
              <span>Đã đặt</span>
            </div>
          </div>
        </Card.Body>
      </Card>

      {/* Schedule Grid - FullCalendar */}
      <Card className="shadow-sm">
        <Card.Header className="bg-light">
          <h5 className="mb-0">Lịch khung giờ - Tuần tới</h5>
        </Card.Header>
        <Card.Body className="p-0">
          {timeslots.length === 0 ? (
            <Alert variant="info" className="m-3">Không có khung giờ nào cho sân này</Alert>
          ) : (
            <div style={{ padding: '20px' }}>
              <FullCalendar
                ref={calendarRef}
                plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                initialView="timeGridWeek"
                headerToolbar={{
                  left: 'prev,next today',
                  center: 'title',
                  right: 'timeGridWeek,timeGridDay',
                }}
                views={{
                  timeGridWeek: {
                    type: 'timeGridWeek',
                    duration: { days: 7 },
                  },
                  timeGridDay: {
                    type: 'timeGridDay',
                  },
                }}
                events={events}
                eventClick={handleEventClick}
                locale="vi"
                slotLabelFormat={{
                  hour: '2-digit',
                  minute: '2-digit',
                  meridiem: false,
                  hour12: false,
                }}
                slotLabelInterval="00:30:00"
                eventDisplay="block"
                nowIndicator={true}
                selectable={false}
                selectConstraint="businessHours"
                height="auto"
                contentHeight="auto"
              />
            </div>
          )}
        </Card.Body>
      </Card>

      {/* TimeSlot Booking Modal */}
      {selectedTimeslot && (
        <TimeSlotBooking
          show={showBookingModal}
          onHide={handleCloseBookingModal}
          court={court}
          timeslot={selectedTimeslot}
          bookingDate={selectedDate}
        />
      )}
    </Container>
  );
};

export default CourtDetail;

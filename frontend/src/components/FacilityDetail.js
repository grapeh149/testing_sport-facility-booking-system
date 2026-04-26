import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert, Badge } from 'react-bootstrap';
import { useParams, Link } from 'react-router-dom';
import facilityService from '../services/facilityService';
import courtService from '../services/courtService';
import reviewService from '../services/reviewService';

const FacilityDetail = () => {
  const { id } = useParams();
  const [facility, setFacility] = useState(null);
  const [courts, setCourts] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [facilityImages, setFacilityImages] = useState([]);
  const [currentSlide, setCurrentSlide] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const hasMultipleSlides = facilityImages.length > 1;

  useEffect(() => {
    loadFacilityDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  useEffect(() => {
    if (facilityImages.length <= 1) {
      return undefined;
    }

    const timer = setTimeout(() => {
      setCurrentSlide((prev) => (prev + 1) % facilityImages.length);
    }, 3000);

    return () => clearTimeout(timer);
  }, [currentSlide, facilityImages.length]);

  const loadFacilityDetails = async () => {
    try {
      setLoading(true);

      // Lấy chi tiết sân
      const facilityRes = await facilityService.getFacilityDetail(id);
      const facilityData = facilityRes.data.data || facilityRes.data;
      setFacility(facilityData);

      // Lấy danh sách sân trong cơ sở
      const courtsRes = await courtService.getCourtsByFacility(id);
      setCourts(courtsRes.data.data || courtsRes.data || []);

      // Lấy danh sách review
      const reviewsRes = await reviewService.getReviewsByFacility(id, 0, 10);
      setReviews(reviewsRes.data?.data?.content || reviewsRes.data?.content || []);

      // Lấy danh sách ảnh của cơ sở cho banner
      const imagesRes = await facilityService.getFacilityImages(id);
      const images = imagesRes.data?.data || imagesRes.data || [];
      const urls = Array.isArray(images)
        ? images
          .map((img) => img?.imageUrl)
          .filter((url) => typeof url === 'string' && url.trim() !== '')
        : [];

      if (urls.length > 0) {
        setFacilityImages(urls);
      } else {
        // Fallback images if none found in DB
        setFacilityImages([
          'https://res.cloudinary.com/dd1frsvzk/image/upload/v1776954141/sport-text-banner-poster-design_1308-132612_o1tlrk.avif',
          'https://res.cloudinary.com/dd1frsvzk/image/upload/v1776954358/SPORT-SANTE_noipw2.webp'
        ]);
      }
      setCurrentSlide(0);

      setError('');
    } catch (err) {
      setError('Không thể tải thông tin sân');
      console.error(err);
    } finally {
      setLoading(false);
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

  const handlePrevSlide = () => {
    if (facilityImages.length <= 1) {
      return;
    }
    setCurrentSlide((prev) => (prev === 0 ? facilityImages.length - 1 : prev - 1));
  };

  const handleNextSlide = () => {
    if (facilityImages.length <= 1) {
      return;
    }
    setCurrentSlide((prev) => (prev + 1) % facilityImages.length);
  };

  if (error || !facility) {
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
      <Link to="/" className="mb-3 d-inline-block">
        <Button variant="outline-secondary">Về danh sách</Button>
      </Link>

      <Card className="mb-4 shadow-sm overflow-hidden">
        <div style={{ position: 'relative', width: '100%', height: '360px', backgroundColor: '#f8f9fa' }}>
          <img
            src={facilityImages[currentSlide]}
            alt={`${facility.name} banner ${currentSlide + 1}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />

          {hasMultipleSlides && (
            <>
              <Button
                variant="dark"
                onClick={handlePrevSlide}
                style={{
                  position: 'absolute',
                  top: '50%',
                  left: '16px',
                  transform: 'translateY(-50%)',
                  borderRadius: '50%',
                  width: '42px',
                  height: '42px',
                  padding: 0,
                  opacity: 0.75,
                }}
              >
                {'<'}
              </Button>

              <Button
                variant="dark"
                onClick={handleNextSlide}
                style={{
                  position: 'absolute',
                  top: '50%',
                  right: '16px',
                  transform: 'translateY(-50%)',
                  borderRadius: '50%',
                  width: '42px',
                  height: '42px',
                  padding: 0,
                  opacity: 0.75,
                }}
              >
                {'>'}
              </Button>

              <div
                style={{
                  position: 'absolute',
                  bottom: '12px',
                  left: '50%',
                  transform: 'translateX(-50%)',
                  background: 'rgba(0, 0, 0, 0.45)',
                  color: '#fff',
                  borderRadius: '16px',
                  padding: '4px 10px',
                  fontSize: '14px',
                }}
              >
                {currentSlide + 1}/{facilityImages.length}
              </div>
            </>
          )}
        </div>
      </Card>

      {/* Facility Info */}
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col md={8}>
              <h1 className="mb-2">{facility.name}</h1>
              <p className="text-muted">Địa chỉ: {facility.address}, {facility.district}</p>
              <p className="text-muted">Thành phố: {facility.city}</p>
              <p>
                <strong>Số điện thoại:</strong> {facility.phone}
              </p>
              <p>
                <strong>Trạng thái:</strong>{' '}
                <Badge bg={facility.status === 'APPROVED' ? 'success' : 'warning'}>
                  {facility.status === 'APPROVED' ? 'Đã duyệt' : 'Chờ duyệt'}
                </Badge>
              </p>
              <p>
                <strong>Đánh giá:</strong> {facility.avgRating ? `${facility.avgRating.toFixed(2)}/5` : 'Chưa có'} ({Math.max(facility.totalReviews || 0, reviews.length)} đánh giá)
              </p>
              {facility.description && (
                <p className="mt-3">
                  <strong>Mô tả:</strong> {facility.description}
                </p>
              )}
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Courts */}
      <h2 className="mb-3">Danh Sách Sân</h2>
      {courts.length === 0 ? (
        <Alert variant="info">Không có sân nào trong cơ sở này</Alert>
      ) : (
        <Row>
          {courts.map((court) => (
            <Col md={6} className="mb-3" key={court.id}>
              <Card className="h-100 shadow-sm border-0">
                {court.sportType?.iconUrl && (
                  <div style={{ height: '200px', overflow: 'hidden', position: 'relative' }}>
                    <Card.Img 
                      variant="top" 
                      src={court.sportType.iconUrl} 
                      style={{ height: '100%', objectFit: 'cover' }}
                    />
                    {!court.isActive && (
                      <div style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.4)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white',
                        fontWeight: 'bold',
                        fontSize: '1.2rem'
                      }}>
                        ĐANG ĐÓNG
                      </div>
                    )}
                  </div>
                )}
                <Card.Body>
                  <Card.Title className="mb-3 fw-bold">{court.name}</Card.Title>

                  <Card.Text className="mb-2">
                    <strong>Mô tả:</strong>
                    <br />
                    <span className="text-muted">{court.description || '(Không có mô tả)'}</span>
                  </Card.Text>

                  <Card.Text className="mb-2">
                    <strong>Loại mặt sân:</strong> {court.surfaceType || '(Không rõ)'}
                  </Card.Text>

                  <Card.Text className="mb-2">
                    <strong>Loại:</strong> {court.isIndoor ? 'Trong nhà' : 'Ngoài trời'}
                  </Card.Text>

                  <div className="mb-4">
                    <Badge bg={court.isActive ? 'success' : 'secondary'}>
                      {court.isActive ? 'Có sẵn' : 'Không có sẵn'}
                    </Badge>
                  </div>

                  <div className="d-grid gap-2">
                    {court.isActive ? (
                      <Link to={`/court/${court.id}`}>
                        <Button variant="success" className="w-100 py-2 fw-bold">
                          Xem lịch và đặt sân
                        </Button>
                      </Link>
                    ) : (
                      <Button
                        variant="secondary"
                        className="w-100 py-2 booking-btn-disabled"
                        disabled
                        aria-disabled="true"
                      >
                        Sân đang đóng
                      </Button>
                    )}
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Reviews */}
      <h2 className="mt-5 mb-3">Đánh giá ({reviews.length})</h2>
      {reviews.length === 0 ? (
        <Alert variant="info">Chưa có đánh giá nào</Alert>
      ) : (
        <div>
          {reviews.map((review) => (
            <Card className="mb-3" key={review.id}>
              <Card.Body>
                <div className="d-flex justify-content-between align-items-start">
                  <div className="d-flex align-items-center">
                    {review.customerAvatarUrl ? (
                      <img
                        src={review.customerAvatarUrl}
                        alt="avatar"
                        style={{ width: '40px', height: '40px', borderRadius: '50%', objectFit: 'cover', marginRight: '15px' }}
                      />
                    ) : (
                      <div style={{
                        width: '40px', height: '40px', borderRadius: '50%',
                        backgroundColor: '#007bff', color: '#fff',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontWeight: 'bold', marginRight: '15px', fontSize: '18px'
                      }}>
                        {(review.customerName || 'K')[0].toUpperCase()}
                      </div>
                    )}
                    <div>
                      <strong>{review.customerName || 'Khách hàng'}</strong>
                      {review.courtName && (
                        <div className="text-muted small mt-1">
                          Sân: {review.courtName}
                        </div>
                      )}
                      <div className="mt-2">
                        {[...Array(5)].map((_, i) => {
                          const ratingNum = parseInt(review.rating) || 0;
                          return (
                            <span key={i} style={{ color: i < ratingNum ? '#ffc107' : '#ddd', fontSize: '18px' }}>
                              ★
                            </span>
                          );
                        })}
                        <span className="ms-2 fw-bold">{review.rating}/5</span>
                      </div>
                    </div>
                  </div>
                  <small className="text-muted">
                    {review.createdAt ? new Date(review.createdAt).toLocaleDateString('vi-VN') : 'N/A'}
                  </small>
                </div>
                <p className="mt-3 mb-0">{review.comment}</p>
                {review.ownerReply && (
                  <div className="mt-3 p-3 bg-light border-left" style={{ borderLeft: '3px solid #007bff' }}>
                    <strong className="text-primary">Phản hồi từ chủ sân:</strong>
                    <p className="mt-2 mb-0">{review.ownerReply}</p>
                  </div>
                )}
              </Card.Body>
            </Card>
          ))}
        </div>
      )}
    </Container>
  );
};

export default FacilityDetail;

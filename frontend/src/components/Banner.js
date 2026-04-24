import React from 'react';
import { Container, Row, Col, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import './Banner.css';

const Banner = () => {
  const navigate = useNavigate();

  return (
    <div className="banner-section">
      <div className="banner-overlay"></div>
      <Container className="h-100">
        <Row className="h-100 align-items-center">
          <Col lg={6} className="banner-content">
            <span className="banner-tag">Bạn đã sẵn sàng chưa?</span>
            <h1 className="banner-title">Đặt lịch thể thao dễ dàng</h1>
            <p className="banner-subtitle">
              Khám phá và đặt sân thể thao phù hợp trong vài giây. Từ cầu lông, bóng bàn đến bóng đá, mọi lựa chọn đều rõ ràng và dễ thao tác.
            </p>
            <div className="banner-buttons">
              <Button
                className="btn-banner-primary me-3"
                onClick={() => navigate('/court-search')}
              >
                Book a Court
              </Button>
              <Button
                className="btn-banner-secondary"
                onClick={() => navigate('/')}
              >
                Explore Locations
              </Button>
            </div>
            <div className="quick-search-strip mt-4">
              <div className="strip-item">
                <span className="strip-label">Sport Type</span>
                <span className="strip-value">Tennis</span>
              </div>
              <div className="strip-item">
                <span className="strip-label">Location</span>
                <span className="strip-value">City or Zip</span>
              </div>
              <div className="strip-item">
                <span className="strip-label">Date</span>
                <span className="strip-value">mm/dd/yyyy</span>
              </div>
              <Button className="strip-action" onClick={() => navigate('/court-search')}>Tìm sân</Button>
            </div>
          </Col>
          <Col lg={6} className="banner-image">
            <div className="hero-media" />
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Banner;

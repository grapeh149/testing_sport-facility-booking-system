import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer mt-5 py-5">
      <Container>
        <Row className="mb-4">
          <Col md={3} sm={6} className="mb-3">
            <h5 className="fw-bold">CourtReserve</h5>
            <p>
              Nền tảng đặt sân thể thao giúp bạn tìm kiếm và quản lý lịch chơi nhanh chóng.
            </p>
          </Col>
          <Col md={3} sm={6} className="mb-3">
            <h6 className="fw-bold">Explore</h6>
            <ul className="list-unstyled">
              <li><a href="/court-search" className="text-decoration-none">Tìm sân</a></li>
              <li><a href="/" className="text-decoration-none">Membership</a></li>
              <li><a href="/" className="text-decoration-none">Danh sách giải đấu</a></li>
              <li><a href="/register" className="text-decoration-none">Trở thành chủ sân</a></li>
            </ul>
          </Col>
          <Col md={3} sm={6} className="mb-3">
            <h6 className="fw-bold">Support</h6>
            <ul className="list-unstyled">
              <li><a href="/" className="text-decoration-none">Trung tâm trợ giúp</a></li>
              <li><a href="/" className="text-decoration-none">Liên hệ</a></li>
              <li><a href="/" className="text-decoration-none">Điều khoản sử dụng</a></li>
              <li><a href="/" className="text-decoration-none">Chính sách bảo mật</a></li>
            </ul>
          </Col>
          <Col md={3} sm={6} className="mb-3">
            <h6 className="fw-bold">Contact</h6>
            <p>Hotline: 0392611076</p>
            <p>Email: sportvn@gmail.com</p>
            <p>Giờ hỗ trợ: 08:00 - 22:00</p>
          </Col>
        </Row>
        <hr />
        <Row>
          <Col md={6}>
            <p className="mb-0">&copy; 2026 Sport Booking. Bản quyền thuộc về Group 6.</p>
          </Col>
          <Col md={6} className="text-end">
            <p className="mb-0">Phiên bản 1.0 | Được phát triển bởi Group 6</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;

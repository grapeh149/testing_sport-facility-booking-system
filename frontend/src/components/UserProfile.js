import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Badge, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';

const UserProfile = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    loadProfile();
  }, [user, navigate]);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const response = await userService.getProfile();
      
      if (response.data.success) {
        setProfile(response.data.data);
        setError('');
      } else {
        setError(response.data.message || 'Không thể lấy thông tin profile');
      }
    } catch (err) {
      console.error('Profile error:', err);
      setError(err.response?.data?.message || 'Lỗi khi lấy thông tin profile');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ height: '80vh' }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
      </Container>
    );
  }

  if (!profile) {
    return (
      <Container style={{ minHeight: '80vh' }} className="mt-5">
        <Alert variant="danger">Không thể lấy thông tin profile</Alert>
      </Container>
    );
  }

  return (
    <Container style={{ minHeight: '80vh' }} className="mt-5">
      <Row className="mb-4">
        <Col md={8}>
          <h2 className="mb-4">Thông tin cá nhân</h2>
        </Col>
      </Row>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row>
        <Col md={4} className="mb-4">
          <Card>
            <Card.Body className="text-center">
              {profile.avatarUrl ? (
                <img 
                  src={profile.avatarUrl} 
                  alt={profile.fullName}
                  className="rounded-circle"
                  style={{ width: '150px', height: '150px', objectFit: 'cover', marginBottom: '1rem' }}
                />
              ) : (
                <div 
                  className="rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center"
                  style={{ width: '150px', height: '150px', backgroundColor: '#e9ecef' }}
                >
                  <span style={{ fontSize: '3rem' }}></span>
                </div>
              )}
              <h4>{profile.fullName}</h4>
              <p className="text-muted">@{profile.username || profile.email.split('@')[0]}</p>
              <Badge bg="primary">{profile.role}</Badge>
            </Card.Body>
          </Card>
        </Col>

        <Col md={8}>
          <Card>
            <Card.Body>
              <h5 className="mb-3">Thông tin cá nhân</h5>
              
              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label text-muted">Họ và tên</label>
                  <p className="form-control-plaintext fw-bold">{profile.fullName}</p>
                </div>
                <div className="col-md-6">
                  <label className="form-label text-muted">Tên đăng nhập</label>
                  <p className="form-control-plaintext fw-bold">
                    {profile.username || profile.email.split('@')[0]}
                  </p>
                </div>
              </div>

              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label text-muted">Email</label>
                  <p className="form-control-plaintext">{profile.email}</p>
                </div>
                <div className="col-md-6">
                  <label className="form-label text-muted">Số điện thoại</label>
                  <p className="form-control-plaintext">{profile.phone || 'Chưa cập nhật'}</p>
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label text-muted">Loại tài khoản</label>
                <p className="form-control-plaintext">
                  {profile.role === 'ADMIN' && 'Quản trị viên hệ thống'}
                  {profile.role === 'OWNER' && 'Chủ sân vận động'}
                  {profile.role === 'CUSTOMER' && 'Khách hàng'}
                </p>
              </div>

              <div className="d-flex gap-2">
                <Link to="/profile/edit">
                  <Button variant="primary">
                    Cập nhật thông tin
                  </Button>
                </Link>
                <Link to="/profile/change-password">
                  <Button variant="warning">
                    Đổi mật khẩu
                  </Button>
                </Link>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default UserProfile;

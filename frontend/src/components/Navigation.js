import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Container, NavDropdown, Badge, Spinner } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import notificationService from '../services/notificationService';
import './Navigation.css';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loadingNotifications, setLoadingNotifications] = useState(false);

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

  const loadNotifications = async () => {
    if (!isAuthenticated) {
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    try {
      setLoadingNotifications(true);
      const [listRes, countRes] = await Promise.all([
        notificationService.getLatestNotifications(),
        notificationService.getUnreadCount(),
      ]);

      const listData = unwrapApiResponse(listRes, 'Khong the tai thong bao') || [];
      const unread = unwrapApiResponse(countRes, 'Khong the tai so thong bao chua doc') || 0;
      setNotifications(Array.isArray(listData) ? listData : []);
      setUnreadCount(Number(unread) || 0);
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setLoadingNotifications(false);
    }
  };

  useEffect(() => {
    loadNotifications();
    if (!isAuthenticated) {
      return undefined;
    }
    const timer = setInterval(loadNotifications, 30000);
    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  const handleMarkAsRead = async (notificationId) => {
    try {
      await notificationService.markAsRead(notificationId);
      setNotifications((prev) => prev.map((item) =>
        item.id === notificationId ? { ...item, isRead: true } : item
      ));
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch (err) {
      console.error('Failed to mark notification as read:', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((item) => ({ ...item, isRead: true })));
      setUnreadCount(0);
    } catch (err) {
      console.error('Failed to mark all notifications as read:', err);
    }
  };

  const formatNotificationTime = (value) => {
    if (!value) return '';
    try {
      return new Date(value).toLocaleString('vi-VN');
    } catch {
      return '';
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Navbar className="navbar-custom" expand="lg" sticky="top">
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold navbar-brand-custom">
          Sport Booking
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Nav.Link as={Link} to="/" className="nav-link-custom">
              Trang chủ
            </Nav.Link>
            <Nav.Link as={Link} to="/court-search" className="nav-link-custom">
              Tìm kiếm sân
            </Nav.Link>

            {isAuthenticated ? (
              <>
                <Nav.Link as={Link} to="/my-bookings" className="nav-link-custom">
                  Đặt sân của tôi
                </Nav.Link>

                {user?.role === 'OWNER' && (
                  <Nav.Link as={Link} to="/owner/dashboard" className="nav-link-custom">
                    Quản Lý Cơ Sở
                  </Nav.Link>
                )}

                {user?.role === 'ADMIN' && (
                  <Nav.Link as={Link} to="/admin/dashboard" className="nav-link-custom">
                    Admin
                  </Nav.Link>
                )}

                <NavDropdown
                  title={
                    <span className="notification-title">
                      Thông báo
                      {unreadCount > 0 && (
                        <Badge pill bg="danger" className="notification-badge">
                          {unreadCount > 99 ? '99+' : unreadCount}
                        </Badge>
                      )}
                    </span>
                  }
                  id="notification-dropdown"
                  align="end"
                  className="nav-link-custom notification-dropdown"
                >
                  <div className="notification-header d-flex justify-content-between align-items-center px-3 py-2">
                    <strong>Thông báo</strong>
                    {unreadCount > 0 && (
                      <button type="button" className="mark-all-btn" onClick={handleMarkAllAsRead}>
                        Đọc tất cả
                      </button>
                    )}
                  </div>
                  <NavDropdown.Divider />
                  {loadingNotifications ? (
                    <div className="px-3 py-3 text-center"><Spinner animation="border" size="sm" /></div>
                  ) : notifications.length === 0 ? (
                    <div className="px-3 py-2 text-muted">Chưa có thông báo</div>
                  ) : (
                    notifications.map((item) => (
                      <NavDropdown.Item
                        key={item.id}
                        className={`notification-item ${item.isRead ? 'is-read' : 'is-unread'}`}
                        onClick={() => !item.isRead && handleMarkAsRead(item.id)}
                      >
                        <div className="notification-item-title">{item.title}</div>
                        <div className="notification-item-message">{item.message}</div>
                        <small className="text-muted">{formatNotificationTime(item.createdAt)}</small>
                      </NavDropdown.Item>
                    ))
                  )}
                </NavDropdown>

                <NavDropdown
                  title={
                    <span className="d-flex align-items-center">
                      {(user?.avatarUrl) ? (
                        <img
                          src={user.avatarUrl}
                          alt="avatar"
                          style={{ width: '28px', height: '28px', borderRadius: '50%', objectFit: 'cover', marginRight: '8px' }}
                        />
                      ) : (
                        <div style={{
                          width: '28px', height: '28px', borderRadius: '50%',
                          backgroundColor: '#007bff', color: '#fff',
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                          fontWeight: 'bold', marginRight: '8px', fontSize: '14px'
                        }}>
                          {(user?.fullName || 'U')[0].toUpperCase()}
                        </div>
                      )}
                      {user?.fullName}
                    </span>
                  }
                  id="user-profile-dropdown"
                  className="nav-link-custom username-dropdown"
                >
                  <NavDropdown.Item as={Link} to="/profile">
                    Thông tin cá nhân
                  </NavDropdown.Item>
                  <NavDropdown.Item as={Link} to="/profile/edit">
                    Cập nhật thông tin
                  </NavDropdown.Item>
                  <NavDropdown.Item as={Link} to="/profile/change-password">
                    Đổi mật khẩu
                  </NavDropdown.Item>
                  <NavDropdown.Divider />
                  <NavDropdown.Item onClick={handleLogout}>
                    Đăng xuất
                  </NavDropdown.Item>
                </NavDropdown>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" className="nav-link-custom">
                  Đăng nhập
                </Nav.Link>
                <Nav.Link as={Link} to="/register" className="nav-link-custom">
                  Đăng ký
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Navigation;

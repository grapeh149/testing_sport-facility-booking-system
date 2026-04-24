import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Table, Badge, Tab, Tabs, Form, Modal, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import adminService from '../services/adminService';
import sportTypeService from '../services/sportTypeService';

const AdminDashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [facilities, setFacilities] = useState([]);
  const [users, setUsers] = useState([]);
  const [sportTypes, setSportTypes] = useState([]);
  const [rejectReason, setRejectReason] = useState('');
  const [selectedFacility, setSelectedFacility] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showUserModal, setShowUserModal] = useState(false);
  const [showEditUserModal, setShowEditUserModal] = useState(false);
  const [editingSportTypeId, setEditingSportTypeId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  
  // User form states
  const [userForm, setUserForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    role: 'CUSTOMER'
  });
  
  // Edit User form states
  const [editUserForm, setEditUserForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    role: 'CUSTOMER'
  });
  
  // Sport Type form states
  const [sportTypeForm, setSportTypeForm] = useState({
    name: '',
    description: '',
    iconUrl: ''
  });
  
  // Edit Sport Type form states
  const [editSportTypeForm, setEditSportTypeForm] = useState({
    name: '',
    description: '',
    iconUrl: '',
    isActive: true
  });

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

  const getStatusBadge = (status) => {
    if (status === 'APPROVED') {
      return { variant: 'success', text: 'Đã duyệt' };
    }
    if (status === 'REJECTED') {
      return { variant: 'danger', text: 'Đã hủy' };
    }
    return { variant: 'warning', text: 'Đang chờ duyệt' };
  };

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'ADMIN') {
      navigate('/');
      return;
    }
    loadAdminData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, user, navigate]);

  const loadAdminData = async () => {
    try {
      setLoading(true);
      
      // Load facilities, users, and sport-types from backend APIs
      const [facilitiesRes, usersRes, sportTypesRes] = await Promise.allSettled([
        adminService.getAllFacilities(0, 100),
        adminService.getAllUsers(0, 100),
        sportTypeService.getAllSportTypes()
      ]);

      // Extract facilities data
      if (facilitiesRes.status === 'fulfilled') {
        const facilityPayload = unwrapApiResponse(facilitiesRes.value, 'Khong the tai danh sach co so') || {};
        const facilitiesData = facilityPayload.content || facilityPayload || [];
        console.log('Loaded facilities:', facilitiesData);
        setFacilities(facilitiesData);
      } else {
        console.error('Failed to load facilities:', facilitiesRes.reason);
        setMessage({ type: 'warning', text: 'Không thể tải danh sách sân' });
      }

      // Extract users data
      if (usersRes.status === 'fulfilled') {
        const usersPayload = unwrapApiResponse(usersRes.value, 'Khong the tai danh sach nguoi dung') || {};
        const usersData = usersPayload.content || usersPayload || [];
        console.log('Loaded users:', usersData);
        setUsers(usersData);
      } else {
        console.error('Failed to load users:', usersRes.reason);
        setMessage({ type: 'warning', text: 'Không thể tải danh sách người dùng' });
      }

      // Extract sport-types data
      if (sportTypesRes.status === 'fulfilled') {
        const sportTypesPayload = unwrapApiResponse(sportTypesRes.value, 'Khong the tai danh sach loai the thao') || [];
        console.log('Loaded sport-types:', sportTypesPayload);
        setSportTypes(sportTypesPayload);
      } else {
        console.error('Failed to load sport-types:', sportTypesRes.reason);
        setMessage({ type: 'warning', text: 'Không thể tải danh sách loại thể thao' });
      }
    } catch (err) {
      console.error('Error loading admin data:', err);
      setMessage({ type: 'danger', text: 'Lỗi khi tải dữ liệu: ' + err.message });
    } finally {
      setLoading(false);
    }
  };

  const handleApproveFacility = async (id) => {
    try {
      setLoading(true);
      const adminId = user?.userId;
      if (!adminId) {
        throw new Error('Khong the xac dinh admin');
      }
      const response = await adminService.approveFacility(id, adminId);
      unwrapApiResponse(response, 'Phe duyet co so that bai');
      await loadAdminData();
      setMessage({ type: 'success', text: 'Phê duyệt cơ sở thành công!' });
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi phê duyệt cơ sở: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRejectFacility = (facility) => {
    setSelectedFacility(facility);
    setShowRejectModal(true);
  };

  const confirmReject = async () => {
    if (!rejectReason.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập lý do từ chối' });
      return;
    }
    try {
      setLoading(true);
      const adminId = user?.userId;
      if (!adminId) {
        throw new Error('Khong the xac dinh admin');
      }
      const response = await adminService.rejectFacility(selectedFacility.id, rejectReason, adminId);
      unwrapApiResponse(response, 'Cap nhat trang thai co so that bai');
      await loadAdminData();
      setMessage({ type: 'success', text: selectedFacility?.status === 'APPROVED' ? 'Hủy cơ sở thành công!' : 'Từ chối cơ sở thành công!' });
      setShowRejectModal(false);
      setRejectReason('');
      setSelectedFacility(null);
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi từ chối cơ sở: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLockUser = async (userId) => {
    if (window.confirm('Bạn có chắc muốn khóa tài khoản này không?')) {
      try {
        setLoading(true);
        const response = await adminService.lockUser(userId);
        unwrapApiResponse(response, 'Khoa tai khoan that bai');
        await loadAdminData();
        setMessage({ type: 'success', text: 'Khóa tài khoản thành công!' });
      } catch (err) {
        setMessage({ type: 'danger', text: 'Lỗi khi khóa tài khoản: ' + (err.response?.data?.message || err.message) });
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleUnlockUser = async (userId) => {
    if (window.confirm('Bạn có chắc muốn mở khóa tài khoản này không?')) {
      try {
        setLoading(true);
        const response = await adminService.unlockUser(userId);
        unwrapApiResponse(response, 'Mo khoa tai khoan that bai');
        await loadAdminData();
        setMessage({ type: 'success', text: 'Mở khóa tài khoản thành công!' });
      } catch (err) {
        setMessage({ type: 'danger', text: 'Lỗi khi mở khóa tài khoản: ' + (err.response?.data?.message || err.message) });
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleUserFormChange = (field, value) => {
    setUserForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleEditUserFormChange = (field, value) => {
    setEditUserForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    
    if (!userForm.fullName.trim() || !userForm.email.trim() || !userForm.phone.trim() || !userForm.password.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng điền tất cả các trường bắt buộc' });
      return;
    }

    try {
      setLoading(true);
      const response = await adminService.createUser({
        fullName: userForm.fullName,
        email: userForm.email,
        phone: userForm.phone,
        password: userForm.password,
        role: userForm.role
      });
      
      unwrapApiResponse(response, 'Tạo người dùng thất bại');
      setMessage({ type: 'success', text: 'Tạo người dùng thành công!' });
      
      // Reset form
      setUserForm({ fullName: '', email: '', phone: '', password: '', role: 'CUSTOMER' });
      setShowUserModal(false);
      
      // Reload users
      await loadAdminData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi tạo người dùng: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEditUser = (user) => {
    setSelectedUser(user);
    setEditUserForm({
      fullName: user.fullName || '',
      email: user.email || '',
      phone: user.phone || '',
      role: user.role || 'CUSTOMER'
    });
    setShowEditUserModal(true);
  };

  const handleUpdateUser = async () => {
    if (!editUserForm.fullName.trim() || !editUserForm.email.trim() || !editUserForm.phone.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng điền tất cả các trường bắt buộc' });
      return;
    }

    try {
      setLoading(true);
      const response = await adminService.updateUser(selectedUser.id, {
        fullName: editUserForm.fullName,
        email: editUserForm.email,
        phone: editUserForm.phone,
        role: editUserForm.role
      });
      
      unwrapApiResponse(response, 'Cập nhật người dùng thất bại');
      setMessage({ type: 'success', text: 'Cập nhật người dùng thành công!' });
      setShowEditUserModal(false);
      setSelectedUser(null);
      
      // Reload users
      await loadAdminData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi cập nhật người dùng: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Bạn có chắc muốn xóa tài khoản này không? Thao tác này không thể hoàn tác!')) {
      try {
        setLoading(true);
        const response = await adminService.deleteUser(userId);
        unwrapApiResponse(response, 'Xóa người dùng thất bại');
        setMessage({ type: 'success', text: 'Xóa người dùng thành công!' });
        await loadAdminData();
      } catch (err) {
        setMessage({ type: 'danger', text: 'Lỗi khi xóa người dùng: ' + (err.response?.data?.message || err.message) });
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleCancelEditUser = () => {
    setShowEditUserModal(false);
    setSelectedUser(null);
    setEditUserForm({ fullName: '', email: '', phone: '', role: 'CUSTOMER' });
  };

  const handleCreateSportType = async (e) => {
    e.preventDefault();
    
    if (!sportTypeForm.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập tên loại thể thao' });
      return;
    }

    try {
      setLoading(true);
      const response = await sportTypeService.createSportType({
        name: sportTypeForm.name,
        description: sportTypeForm.description,
        iconUrl: sportTypeForm.iconUrl,
        isActive: true
      });
      
      unwrapApiResponse(response, 'Tạo loại thể thao thất bại');
      setMessage({ type: 'success', text: 'Tạo loại thể thao thành công!' });
      
      // Reset form
      setSportTypeForm({ name: '', description: '', iconUrl: '' });
      
      // Reload sport-types
      await loadAdminData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi tạo loại thể thao: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSportTypeFormChange = (field, value) => {
    setSportTypeForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleEditSportType = (sportType) => {
    setEditingSportTypeId(sportType.id);
    setEditSportTypeForm({
      name: sportType.name,
      description: sportType.description || '',
      iconUrl: sportType.iconUrl || '',
      isActive: sportType.isActive
    });
    setShowEditModal(true);
  };

  const handleEditFormChange = (field, value) => {
    setEditSportTypeForm(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleUpdateSportType = async () => {
    if (!editSportTypeForm.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập tên loại thể thao' });
      return;
    }

    try {
      setLoading(true);
      const response = await sportTypeService.updateSportType(editingSportTypeId, {
        name: editSportTypeForm.name,
        description: editSportTypeForm.description,
        iconUrl: editSportTypeForm.iconUrl,
        isActive: editSportTypeForm.isActive
      });
      
      unwrapApiResponse(response, 'Cập nhật loại thể thao thất bại');
      setMessage({ type: 'success', text: 'Cập nhật loại thể thao thành công!' });
      setShowEditModal(false);
      setEditingSportTypeId(null);
      
      // Reload sport-types
      await loadAdminData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi cập nhật loại thể thao: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelEdit = () => {
    setShowEditModal(false);
    setEditingSportTypeId(null);
    setEditSportTypeForm({
      name: '',
      description: '',
      iconUrl: '',
      isActive: true
    });
  };

  // Helper function to get owner name from facility object
  const getOwnerName = (facility) => {
    return facility?.ownerName || 'N/A';
  };

  // Helper function to format date
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('vi-VN');
    } catch {
      return dateString;
    }
  };

  const resolveUserStatus = (item) => {
    if (item?.status) {
      return item.status;
    }
    return item?.isActive ? 'ACTIVE' : 'BLOCKED';
  };

  // Handler for viewing facility details
  const handleViewFacility = (facility) => {
    setSelectedFacility(facility);
    setShowDetailModal(true);
  };

  const stats = [
    { label: 'Tổng Người Dùng', value: users.length, color: 'primary' },
    { label: 'Chủ Cơ Sở', value: users.filter(u => u.role === 'OWNER').length, color: 'success' },
    { label: 'Cơ Sở Chờ Duyệt', value: facilities.filter(f => f.status === 'PENDING').length, color: 'warning' },
    { label: 'Cơ Sở Đã Duyệt', value: facilities.filter(f => f.status === 'APPROVED').length, color: 'info' },
  ];

  return (
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col>
          <h2 className="mb-1">Admin Dashboard</h2>
          <p className="text-muted">Quản lý toàn hệ thống từ đây</p>
        </Col>
        <Col auto>
          <Button variant="primary" onClick={() => navigate('/')}>Quay lại</Button>
        </Col>
      </Row>

      {message.text && (
        <Alert 
          variant={message.type} 
          onClose={() => setMessage({ type: '', text: '' })} 
          dismissible
        >
          {message.text}
        </Alert>
      )}

      {/* Stats */}
      <Row className="mb-4">
        {stats.map((stat, idx) => (
          <Col md={3} sm={6} className="mb-3" key={idx}>
            <Card className="text-center border-0 shadow-sm">
              <Card.Body>
                <h4 className="mt-2">{stat.value}</h4>
                <p className="text-muted mb-0">{stat.label}</p>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>

      {/* Tabs */}
      <Card className="border-0 shadow-sm">
        <Card.Body>
          <Tabs defaultActiveKey="facilities" className="mb-4">
            
            {/* Facilities Approval Tab */}
            <Tab eventKey="facilities" title="Duyệt Cơ Sở">
              <Table hover responsive className="mt-4">
                <thead>
                  <tr>
                    <th>Tên Cơ Sở</th>
                    <th>Chủ Cơ Sở</th>
                    <th>Ngày Tạo</th>
                    <th>Trạng Thái</th>
                    <th>Hành Động</th>
                  </tr>
                </thead>
                <tbody>
                  {facilities.map(facility => (
                    <tr key={facility.id}>
                      <td className="fw-bold">{facility.name}</td>
                      <td>{getOwnerName(facility)}</td>
                      <td>{formatDate(facility.createdAt)}</td>
                      <td>
                        <Badge bg={getStatusBadge(facility.status).variant}>
                          {getStatusBadge(facility.status).text}
                        </Badge>
                      </td>
                      <td>
                        {facility.status === 'PENDING' && (
                          <>
                            <Button 
                              size="sm" 
                              variant="success" 
                              className="me-2"
                              onClick={() => handleApproveFacility(facility.id)}
                              disabled={loading}
                            >
                              Phê Duyệt
                            </Button>
                            <Button 
                              size="sm" 
                              variant="danger"
                              onClick={() => handleRejectFacility(facility)}
                              disabled={loading}
                            >
                              Hủy
                            </Button>
                          </>
                        )}
                        {facility.status === 'APPROVED' && (
                          <Button 
                            size="sm" 
                            variant="danger"
                            className="me-2"
                            onClick={() => handleRejectFacility(facility)}
                            disabled={loading}
                          >
                            Hủy
                          </Button>
                        )}
                        {facility.status === 'REJECTED' && (
                          <Button 
                            size="sm" 
                            variant="success"
                            className="me-2"
                            onClick={() => handleApproveFacility(facility.id)}
                            disabled={loading}
                          >
                            Duyệt
                          </Button>
                        )}
                        <Button 
                          size="sm" 
                          variant="info"
                          onClick={() => handleViewFacility(facility)}
                          disabled={loading}
                        >
                          Xem
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Tab>

            {/* Users Tab */}
            <Tab eventKey="users" title="Người Dùng">
              <div className="mt-4">
                <div className="mb-3">
                  <Button 
                    variant="primary" 
                    onClick={() => setShowUserModal(true)}
                    disabled={loading}
                  >
                    + Tạo Người Dùng Mới
                  </Button>
                </div>

                <Table hover responsive>
                  <thead>
                    <tr>
                      <th>Tên</th>
                      <th>Email</th>
                      <th>Điện Thoại</th>
                      <th>Vai Trò</th>
                      <th>Ngày Tham Gia</th>
                      <th>Trạng Thái</th>
                      <th>Hành Động</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map(user => (
                      <tr key={user.id}>
                        <td className="fw-bold">{user.fullName || user.name || 'N/A'}</td>
                        <td>{user.email}</td>
                        <td>{user.phone || 'N/A'}</td>
                        <td>
                          <Badge bg={user.role === 'OWNER' ? 'primary' : (user.role === 'ADMIN' ? 'danger' : 'secondary')}>
                            {user.role}
                          </Badge>
                        </td>
                        <td>{formatDate(user.createdAt)}</td>
                        <td>
                          <Badge bg={resolveUserStatus(user) === 'ACTIVE' ? 'success' : 'danger'}>
                            {resolveUserStatus(user) === 'ACTIVE' ? 'Hoạt Động' : (resolveUserStatus(user) === 'BLOCKED' ? 'Bị Khóa' : resolveUserStatus(user))}
                          </Badge>
                        </td>
                        <td>
                          <Button 
                            size="sm" 
                            variant="info"
                            className="me-2"
                            onClick={() => handleEditUser(user)}
                            disabled={loading}
                          >
                            Sửa
                          </Button>
                          {resolveUserStatus(user) === 'ACTIVE' ? (
                            <Button 
                              size="sm" 
                              variant="warning"
                              className="me-2"
                              onClick={() => handleLockUser(user.id)}
                              disabled={loading}
                            >
                              Khóa
                            </Button>
                          ) : (
                            <Button 
                              size="sm" 
                              variant="success"
                              className="me-2"
                              onClick={() => handleUnlockUser(user.id)}
                              disabled={loading}
                            >
                              Mở khóa
                            </Button>
                          )}
                          <Button 
                            size="sm" 
                            variant="danger"
                            onClick={() => handleDeleteUser(user.id)}
                            disabled={loading}
                          >
                            Xóa
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            </Tab>

            {/* Sport-Types Tab */}
            <Tab eventKey="sport-types" title="Loại Thể Thao">
              <div className="mt-4">
                <Row className="mb-4">
                  <Col md={6}>
                    <Card className="border-0 shadow-sm">
                      <Card.Header className="bg-light">
                        <h5 className="mb-0">Tạo Loại Thể Thao Mới</h5>
                      </Card.Header>
                      <Card.Body>
                        <Form onSubmit={handleCreateSportType}>
                          <Form.Group className="mb-3">
                            <Form.Label>Tên Loại Thể Thao <span style={{ color: 'red' }}>*</span></Form.Label>
                            <Form.Control
                              type="text"
                              placeholder="Ví dụ: Bóng Đá, Bóng Rổ, Cầu Lông..."
                              value={sportTypeForm.name}
                              onChange={(e) => handleSportTypeFormChange('name', e.target.value)}
                              disabled={loading}
                            />
                          </Form.Group>

                          <Form.Group className="mb-3">
                            <Form.Label>Mô Tả</Form.Label>
                            <Form.Control
                              as="textarea"
                              rows={3}
                              placeholder="Nhập mô tả loại thể thao..."
                              value={sportTypeForm.description}
                              onChange={(e) => handleSportTypeFormChange('description', e.target.value)}
                              disabled={loading}
                            />
                          </Form.Group>

                          <Form.Group className="mb-3">
                            <Form.Label>URL Icon</Form.Label>
                            <Form.Control
                              type="text"
                              placeholder="https://example.com/icon.png"
                              value={sportTypeForm.iconUrl}
                              onChange={(e) => handleSportTypeFormChange('iconUrl', e.target.value)}
                              disabled={loading}
                            />
                          </Form.Group>

                          <Button 
                            variant="primary" 
                            type="submit" 
                            className="w-100"
                            disabled={loading}
                          >
                            {loading ? 'Đang tạo...' : 'Tạo Loại Thể Thao'}
                          </Button>
                        </Form>
                      </Card.Body>
                    </Card>
                  </Col>

                  <Col md={6}>
                    <Card className="border-0 shadow-sm">
                      <Card.Header className="bg-light">
                        <h5 className="mb-0">Danh Sách Loại Thể Thao ({sportTypes.length})</h5>
                      </Card.Header>
                      <Card.Body>
                        {sportTypes.length === 0 ? (
                          <p className="text-muted">Chưa có loại thể thao nào</p>
                        ) : (
                          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
                            {sportTypes.map(sportType => (
                              <div 
                                key={sportType.id} 
                                className="mb-3 p-2 border rounded"
                                style={{ backgroundColor: '#f8f9fa' }}
                              >
                                <div className="d-flex align-items-center justify-content-between">
                                  <div className="d-flex align-items-center flex-grow-1">
                                    {sportType.iconUrl && (
                                      <img 
                                        src={sportType.iconUrl} 
                                        alt={sportType.name}
                                        style={{ width: '40px', height: '40px', marginRight: '10px', borderRadius: '4px' }}
                                        onError={(e) => e.target.style.display = 'none'}
                                      />
                                    )}
                                    <div className="flex-grow-1">
                                      <h6 className="mb-1"><strong>{sportType.name}</strong></h6>
                                      {sportType.description && (
                                        <p className="mb-1 text-muted small">{sportType.description}</p>
                                      )}
                                      <Badge bg={sportType.isActive ? 'success' : 'secondary'}>
                                        {sportType.isActive ? 'Hoạt Động' : 'Vô Hiệu'}
                                      </Badge>
                                    </div>
                                  </div>
                                  <Button
                                    size="sm"
                                    variant="warning"
                                    onClick={() => handleEditSportType(sportType)}
                                    disabled={loading}
                                    className="ms-2"
                                  >
                                    Sửa
                                  </Button>
                                </div>
                              </div>
                            ))}
                          </div>
                        )}
                      </Card.Body>
                    </Card>
                  </Col>
                </Row>
              </div>
            </Tab>

          </Tabs>
        </Card.Body>
      </Card>

      {/* Reject Modal */}
      <Modal show={showRejectModal} onHide={() => setShowRejectModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{selectedFacility?.status === 'APPROVED' ? 'Hủy Cơ Sở' : 'Từ Chối Cơ Sở'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Bạn đang {selectedFacility?.status === 'APPROVED' ? 'hủy cơ sở' : 'từ chối cơ sở'}: <strong>{selectedFacility?.name}</strong></p>
          <Form.Group>
            <Form.Label>Lý Do {selectedFacility?.status === 'APPROVED' ? 'Hủy' : 'Từ Chối'}</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              placeholder={selectedFacility?.status === 'APPROVED' ? 'Nhập lý do hủy...' : 'Nhập lý do từ chối...'}
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowRejectModal(false)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={confirmReject}>
            {selectedFacility?.status === 'APPROVED' ? 'Xác Nhận Hủy' : 'Xác Nhận Từ Chối'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Facility Detail Modal */}
      <Modal show={showDetailModal} onHide={() => setShowDetailModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Chi Tiết Cơ Sở</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedFacility && (
            <div>
              <Row className="mb-3">
                <Col md={6}>
                  <p><strong>Tên Cơ Sở:</strong> {selectedFacility.name}</p>
                  <p><strong>Chủ Cơ Sở:</strong> {selectedFacility.ownerName || 'N/A'}</p>
                  <p><strong>Ngày Tạo:</strong> {formatDate(selectedFacility.createdAt)}</p>
                  <p><strong>Địa Chỉ:</strong> {selectedFacility.address}</p>
                  <p><strong>Quận:</strong> {selectedFacility.district}</p>
                  <p><strong>Thành Phố:</strong> {selectedFacility.city}</p>
                </Col>
                <Col md={6}>
                  <p><strong>Số Điện Thoại:</strong> {selectedFacility.phone}</p>
                  <p><strong>Trạng Thái:</strong> <Badge bg={getStatusBadge(selectedFacility.status).variant}>{getStatusBadge(selectedFacility.status).text}</Badge></p>
                  <p><strong>Đánh Giá:</strong> ⭐ {selectedFacility.avgRating?.toFixed(2) || 'N/A'}</p>
                  <p><strong>Số Bình Luận:</strong> {selectedFacility.totalReviews || 0}</p>
                  {selectedFacility.status === 'REJECTED' && (
                    <p><strong>Lý Do Hủy:</strong> {selectedFacility.rejectReason || 'N/A'}</p>
                  )}
                </Col>
              </Row>
              <p><strong>Mô Tả:</strong></p>
              <p className="text-muted">{selectedFacility.description || 'Chưa có mô tả'}</p>
              {selectedFacility.coverImageUrl && (
                <div className="mt-3">
                  <p><strong>Hình Ảnh:</strong></p>
                  <img 
                    src={selectedFacility.coverImageUrl} 
                    alt="Facility Cover" 
                    style={{ maxWidth: '100%', maxHeight: '300px', borderRadius: '8px' }}
                  />
                </div>
              )}
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDetailModal(false)}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Edit Sport Type Modal */}
      <Modal show={showEditModal} onHide={handleCancelEdit}>
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Loại Thể Thao</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Tên Loại Thể Thao <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="text"
                placeholder="Ví dụ: Bóng Đá, Bóng Rổ, Cầu Lông..."
                value={editSportTypeForm.name}
                onChange={(e) => handleEditFormChange('name', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô Tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="Nhập mô tả loại thể thao..."
                value={editSportTypeForm.description}
                onChange={(e) => handleEditFormChange('description', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>URL Icon</Form.Label>
              <Form.Control
                type="text"
                placeholder="https://example.com/icon.png"
                value={editSportTypeForm.iconUrl}
                onChange={(e) => handleEditFormChange('iconUrl', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Kích Hoạt"
                checked={editSportTypeForm.isActive}
                onChange={(e) => handleEditFormChange('isActive', e.target.checked)}
                disabled={loading}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancelEdit} disabled={loading}>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleUpdateSportType} disabled={loading}>
            {loading ? 'Đang cập nhật...' : 'Cập Nhật'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Create User Modal */}
      <Modal show={showUserModal} onHide={() => setShowUserModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Tạo Người Dùng Mới</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleCreateUser}>
            <Form.Group className="mb-3">
              <Form.Label>Tên Đầy Đủ <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="text"
                placeholder="Nhập tên đầy đủ"
                value={userForm.fullName}
                onChange={(e) => handleUserFormChange('fullName', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="email"
                placeholder="Nhập email"
                value={userForm.email}
                onChange={(e) => handleUserFormChange('email', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Số Điện Thoại <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="tel"
                placeholder="Nhập số điện thoại"
                value={userForm.phone}
                onChange={(e) => handleUserFormChange('phone', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mật Khẩu <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="password"
                placeholder="Nhập mật khẩu"
                value={userForm.password}
                onChange={(e) => handleUserFormChange('password', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Vai Trò</Form.Label>
              <Form.Select 
                value={userForm.role}
                onChange={(e) => handleUserFormChange('role', e.target.value)}
                disabled={loading}
              >
                <option value="CUSTOMER">Khách Hàng</option>
                <option value="OWNER">Chủ Cơ Sở</option>
                <option value="ADMIN">Quản Trị Viên</option>
              </Form.Select>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button 
            variant="secondary" 
            onClick={() => {
              setShowUserModal(false);
              setUserForm({ fullName: '', email: '', phone: '', password: '', role: 'CUSTOMER' });
            }}
            disabled={loading}
          >
            Hủy
          </Button>
          <Button 
            variant="primary" 
            onClick={handleCreateUser}
            disabled={loading}
          >
            {loading ? 'Đang tạo...' : 'Tạo Người Dùng'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Edit User Modal */}
      <Modal show={showEditUserModal} onHide={handleCancelEditUser} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Thông Tin Người Dùng</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Tên Đầy Đủ <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="text"
                placeholder="Nhập tên đầy đủ"
                value={editUserForm.fullName}
                onChange={(e) => handleEditUserFormChange('fullName', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="email"
                placeholder="Nhập email"
                value={editUserForm.email}
                onChange={(e) => handleEditUserFormChange('email', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Số Điện Thoại <span style={{ color: 'red' }}>*</span></Form.Label>
              <Form.Control
                type="tel"
                placeholder="Nhập số điện thoại"
                value={editUserForm.phone}
                onChange={(e) => handleEditUserFormChange('phone', e.target.value)}
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Vai Trò</Form.Label>
              <Form.Select 
                value={editUserForm.role}
                onChange={(e) => handleEditUserFormChange('role', e.target.value)}
                disabled={loading}
              >
                <option value="CUSTOMER">Khách Hàng</option>
                <option value="OWNER">Chủ Cơ Sở</option>
                <option value="ADMIN">Quản Trị Viên</option>
              </Form.Select>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button 
            variant="secondary" 
            onClick={handleCancelEditUser}
            disabled={loading}
          >
            Hủy
          </Button>
          <Button 
            variant="primary" 
            onClick={handleUpdateUser}
            disabled={loading}
          >
            {loading ? 'Đang cập nhật...' : 'Cập Nhật'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AdminDashboard;

import React, { useState } from 'react';
import { Container, Form, Button, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';

const ChangePassword = () => {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear errors when user starts typing
    if (error) setError('');
  };

  const validateForm = () => {
    if (!formData.oldPassword) {
      setError('Mật khẩu cũ không được để trống');
      return false;
    }
    if (!formData.newPassword) {
      setError('Mật khẩu mới không được để trống');
      return false;
    }
    if (formData.newPassword.length < 6) {
      setError('Mật khẩu mới phải có ít nhất 6 ký tự');
      return false;
    }
    if (!formData.confirmPassword) {
      setError('Xác nhận mật khẩu không được để trống');
      return false;
    }
    if (formData.newPassword !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không trùng khớp');
      return false;
    }
    if (formData.oldPassword === formData.newPassword) {
      setError('Mật khẩu mới phải khác mật khẩu cũ');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const response = await userService.updatePassword({
        oldPassword: formData.oldPassword,
        newPassword: formData.newPassword,
        confirmPassword: formData.confirmPassword
      });

      if (response.data.success) {
        setSuccess('Đổi mật khẩu thành công! Vui lòng đăng nhập lại.');
        setFormData({
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
        setTimeout(() => {
          logout();
          navigate('/login');
        }, 1200);
      } else {
        setError(response.data.message || 'Đổi mật khẩu thất bại');
      }
    } catch (err) {
      console.error('Change password error:', err);
      setError(err.response?.data?.message || 'Đổi mật khẩu thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="d-flex align-items-center justify-content-center py-4" style={{ minHeight: '100vh' }}>
      <Card style={{ width: '100%', maxWidth: '600px' }}>
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="mb-0">Đổi mật khẩu</h2>
            <Button 
              variant="outline-secondary" 
              onClick={() => navigate('/profile')}
            >
              Quay lại
            </Button>
          </div>

          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Mật khẩu cũ <span className="text-danger">*</span></Form.Label>
              <Form.Control
                type="password"
                name="oldPassword"
                value={formData.oldPassword}
                onChange={handleChange}
                placeholder="Nhập mật khẩu hiện tại"
              />
            </Form.Group>

            <hr className="my-4" />

            <Form.Group className="mb-3">
              <Form.Label>Mật khẩu mới <span className="text-danger">*</span></Form.Label>
              <Form.Control
                type="password"
                name="newPassword"
                value={formData.newPassword}
                onChange={handleChange}
                placeholder="Nhập mật khẩu mới"
                isInvalid={formData.newPassword.length > 0 && formData.newPassword.length < 6}
              />
              <Form.Text className="text-muted d-block">
                Mật khẩu phải có ít nhất 6 ký tự
              </Form.Text>
              {formData.newPassword.length > 0 && formData.newPassword.length < 6 && (
                <Form.Control.Feedback type="invalid" className="d-block">
                  Mật khẩu quá ngắn
                </Form.Control.Feedback>
              )}
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Xác nhận mật khẩu <span className="text-danger">*</span></Form.Label>
              <Form.Control
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Nhập lại mật khẩu mới"
                isInvalid={
                  formData.confirmPassword.length > 0 && 
                  formData.newPassword !== formData.confirmPassword
                }
              />
              {formData.confirmPassword.length > 0 && 
               formData.newPassword !== formData.confirmPassword && (
                <Form.Control.Feedback type="invalid" className="d-block">
                  Mật khẩu xác nhận không trùng khớp
                </Form.Control.Feedback>
              )}
            </Form.Group>

            <div className="alert alert-info mb-3">
              <small>
                <strong>Lưu ý:</strong> Sau khi đổi mật khẩu, bạn sẽ cần đăng nhập lại bằng mật khẩu mới.
              </small>
            </div>

            <div className="d-flex gap-2">
              <Button 
                variant="danger" 
                type="submit"
                disabled={loading}
              >
                {loading ? 'Đang xử lý...' : 'Đổi mật khẩu'}
              </Button>
              <Button 
                variant="secondary" 
                onClick={() => navigate('/profile')}
              >
                Hủy
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ChangePassword;

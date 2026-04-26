import React, { useState } from 'react';
import { Container, Form, Button, Card, Alert, InputGroup } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Xóa lỗi khi user bắt đầu nhập
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.email.trim()) {
      newErrors.email = 'Email không được để trống';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Email không hợp lệ';
    }

    if (!formData.password) {
      newErrors.password = 'Mật khẩu không được để trống';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Mật khẩu phải có ít nhất 6 ký tự';
    }

    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError('');

    const newErrors = validateForm();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);

    const result = await login(formData.email, formData.password);

    if (result.success) {
      // Navigate based on user role
      if (result.user?.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else if (result.user?.role === 'OWNER') {
        navigate('/owner/dashboard');
      } else {
        navigate('/');
      }
    } else {
      if (result.error && typeof result.error === 'object') {
        setErrors(result.error);
      } else {
        setServerError(result.message || 'Đăng nhập thất bại');
      }
    }

    setLoading(false);
  };

  return (
    <Container className="d-flex justify-content-center align-items-center py-5" style={{ minHeight: '80vh' }}>
      <Card style={{ width: '100%', maxWidth: '520px' }}>
        <Card.Body>
          <h2 className="mb-2">Welcome Back</h2>
          <p className="text-muted mb-4">Sign in để tiếp tục quản lý lịch đặt sân của bạn.</p>

          {serverError && <Alert variant="danger">{serverError}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                placeholder="Nhập email"
                value={formData.email}
                onChange={handleChange}
                isInvalid={!!errors.email}
              />
              <Form.Control.Feedback type="invalid">
                {errors.email}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mật khẩu</Form.Label>
              <InputGroup>
                <Form.Control
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  placeholder="Nhập mật khẩu"
                  value={formData.password}
                  onChange={handleChange}
                  isInvalid={!!errors.password}
                />
                <Button
                  variant="outline-secondary"
                  onClick={() => setShowPassword((prev) => !prev)}
                  aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                >
                  {showPassword ? 'Ẩn' : 'Hiện'}
                </Button>
                <Form.Control.Feedback type="invalid">
                  {errors.password}
                </Form.Control.Feedback>
              </InputGroup>
              <Form.Control.Feedback type="invalid">
                {errors.password}
              </Form.Control.Feedback>
            </Form.Group>

            <Button
              variant="primary"
              type="submit"
              className="w-100"
              disabled={loading}
            >
              {loading ? 'Đang đăng nhập...' : 'Sign In'}
            </Button>
          </Form>

          <p className="text-center mt-3">
            Chưa có tài khoản? <Link to="/register">Đăng ký tại đây</Link>
          </p>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Login;

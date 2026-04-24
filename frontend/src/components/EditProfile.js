import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Card, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';
import cloudinaryService from '../services/cloudinaryService';

const EditProfile = () => {
  const { user, refreshUser } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    phone: '',
    avatarUrl: ''
  });
  const [avatarFile, setAvatarFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    loadProfile();
  }, [user, navigate]);

  const loadProfile = async () => {
    try {
      const response = await userService.getProfile();
      
      if (response.data.success) {
        const data = response.data.data;
        setFormData({
          fullName: data.fullName || '',
          username: data.username || '',
          phone: data.phone || '',
          avatarUrl: data.avatarUrl || ''
        });
        setError('');
      } else {
        setError(response.data.message || 'Lỗi khi lấy thông tin profile');
      }
    } catch (err) {
      console.error('Load profile error:', err);
      setError(err.response?.data?.message || 'Lỗi khi lấy thông tin profile');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarFile(file);
      // Hiển thị preview ngay lập tức
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData(prev => ({ ...prev, avatarUrl: reader.result }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.fullName.trim()) {
      setError('Tên đầy đủ không được để trống');
      return;
    }

    setSaving(true);
    try {
      let finalAvatarUrl = formData.avatarUrl;

      // Nếu có chọn file mới thì upload lên Cloudinary
      if (avatarFile) {
        setUploading(true);
        try {
          const uploadRes = await cloudinaryService.uploadFacilityImage(avatarFile);
          finalAvatarUrl = uploadRes; // Assuming it returns the URL string
        } catch (uploadErr) {
          setError('Không thể upload ảnh: ' + uploadErr.message);
          setSaving(false);
          setUploading(false);
          return;
        }
        setUploading(false);
      }

      const response = await userService.updateProfile({
        ...formData,
        avatarUrl: finalAvatarUrl
      });

      if (response.data.success) {
        setSuccess('Cập nhật profile thành công!');
        if (refreshUser) refreshUser();
        setTimeout(() => {
          navigate('/profile');
        }, 1500);
      } else {
        setError(response.data.message || 'Cập nhật profile thất bại');
      }
    } catch (err) {
      console.error('Update profile error:', err);
      setError(err.response?.data?.message || 'Cập nhật profile thất bại');
    } finally {
      setSaving(false);
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

  return (
    <Container style={{ minHeight: '80vh', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }} className="mt-5">
      <div className="w-100" style={{ maxWidth: '600px' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="mb-0">Cập nhật thông tin cá nhân</h2>
          <Button 
            variant="outline-secondary" 
            onClick={() => navigate('/profile')}
          >
            Quay lại
          </Button>
        </div>

        <Card>
        <Card.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Tên đầy đủ <span className="text-danger">*</span></Form.Label>
              <Form.Control
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Nhập tên đầy đủ"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Tên đăng nhập</Form.Label>
              <Form.Control
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="Nhập tên đăng nhập (tùy chọn)"
                maxLength="50"
              />
              <Form.Text className="text-muted">
                Tên đăng nhập không được vượt quá 50 ký tự
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Số điện thoại</Form.Label>
              <Form.Control
                type="text"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="Nhập số điện thoại"
                maxLength="15"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Ảnh đại diện</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                disabled={saving || uploading}
              />
              <Form.Text className="text-muted">
                Dung lượng tối đa 5MB. Định dạng hỗ trợ: JPG, PNG, WEBP.
              </Form.Text>
              {formData.avatarUrl && (
                <div className="mt-2 text-center">
                  <img 
                    src={formData.avatarUrl} 
                    alt="Preview"
                    style={{ width: '150px', height: '150px', borderRadius: '50%', objectFit: 'cover', border: '3px solid #007bff', padding: '3px' }}
                    onError={(e) => {
                      e.target.style.display = 'none';
                    }}
                  />
                  <div className="mt-1 small text-muted">Xem trước ảnh đại diện mới</div>
                </div>
              )}
              {uploading && (
                <div className="mt-2 text-info small">
                  <Spinner animation="border" size="sm" className="me-2" />
                  Đang upload ảnh...
                </div>
              )}
            </Form.Group>

            <div className="d-flex gap-2">
              <Button 
                variant="primary" 
                type="submit"
                disabled={saving}
              >
                {saving ? 'Đang cập nhật...' : 'Lưu thay đổi'}
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
      </div>
    </Container>
  );
};

export default EditProfile;

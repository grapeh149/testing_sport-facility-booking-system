import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import courtService from '../services/courtService';
import sportTypeService from '../services/sportTypeService';
import './CourtSearch.css';

const CourtSearch = () => {
  const [courts, setCourts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [sportTypes, setSportTypes] = useState([]);

  // Filter states
  const [address, setAddress] = useState('');
  const [sportTypeId, setSportTypeId] = useState(null);

  const placeholderImage = 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg';

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

  // Load loại thể thao
  useEffect(() => {
    loadSportTypes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadSportTypes = async () => {
    try {
      const activeResponse = await sportTypeService.getActiveSportTypes();
      const activeData = unwrapApiResponse(activeResponse, 'Khong the tai loai the thao') || [];

      if (Array.isArray(activeData) && activeData.length > 0) {
        setSportTypes(activeData);
        return;
      }

      // Fallback: if active list is empty, load all for filtering convenience
      const allResponse = await sportTypeService.getAllSportTypes();
      const allData = unwrapApiResponse(allResponse, 'Khong the tai toan bo loai the thao') || [];
      setSportTypes(Array.isArray(allData) ? allData : []);
    } catch (err) {
      console.error('Lỗi khi tải loại thể thao:', err);
      setSportTypes([]);
    }
  };

  // Perform search
  const handleSearch = async (e) => {
    e.preventDefault();
    await searchCourts();
  };

  const searchCourts = async () => {
    try {
      setLoading(true);
      setError('');

      const normalizedAddress = address.trim();

      const response = await courtService.searchCourts(
        normalizedAddress,
        sportTypeId || null
      );

      const data = unwrapApiResponse(response, 'Khong the tim kiem san') || [];
      setCourts(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể tìm kiếm sân');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setAddress('');
    setSportTypeId(null);
    setCourts([]);
    setError('');
  };

  return (
    <Container className="py-5 court-search-page">
      <div className="d-flex justify-content-between align-items-end mb-4 flex-wrap gap-2">
        <div>
          <h1 className="mb-1">Tìm sân thể thao</h1>
          <p className="text-muted mb-0">Lọc theo khu vực và môn thể thao để tìm sân phù hợp.</p>
        </div>
      </div>

      {/* Search Form */}
      <Card className="mb-4 search-form-card">
        <Card.Body>
          <Form onSubmit={handleSearch}>
            <Row className="g-3">
              {/* Address Filter */}
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="fw-bold">Địa chỉ / Quận</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Nhập địa chỉ hoặc quận..."
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </Form.Group>
              </Col>

              {/* Sport Type Filter */}
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="fw-bold">Loại thể thao</Form.Label>
                  <Form.Select
                    value={sportTypeId || ''}
                    onChange={(e) => setSportTypeId(e.target.value ? parseInt(e.target.value) : null)}
                  >
                    <option value="">-- Tất cả loại thể thao --</option>
                    {sportTypes.map((type) => (
                      <option key={type.id} value={type.id}>
                        {type.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>


            </Row>

            {/* Buttons */}
            <Row className="mt-4">
              <Col className="d-flex gap-2">
                <Button variant="primary" type="submit" className="flex-grow-1" disabled={loading}>
                  {loading ? (
                    <>
                      <Spinner animation="border" size="sm" className="me-2" />
                      Đang tìm kiếm...
                    </>
                  ) : (
                    'Tìm kiếm'
                  )}
                </Button>
                <Button variant="secondary" type="button" onClick={handleReset}>
                  Xóa bộ lọc
                </Button>
              </Col>
            </Row>
          </Form>
        </Card.Body>
      </Card>

      {/* Error Alert */}
      {error && <Alert variant="danger">{error}</Alert>}

      {/* Loading State */}
      {loading && (
        <div className="text-center py-5">
          <Spinner animation="border" role="status" className="mb-3">
            <span className="visually-hidden">Đang tải...</span>
          </Spinner>
          <p>Đang tìm kiếm sân...</p>
        </div>
      )}

      {/* Results */}
      {!loading && courts.length === 0 && (
        <Alert variant="info">
          {address || sportTypeId
            ? 'Không tìm thấy sân phù hợp với tiêu chí tìm kiếm'
            : 'Hãy nhập tiêu chí tìm kiếm để bắt đầu'}
        </Alert>
      )}

      {!loading && courts.length > 0 && (
        <>
          <Alert variant="success">
            Tìm thấy <strong>{courts.length}</strong> sân phù hợp
          </Alert>
          <Row>
            {courts.map((court) => (
              <Col md={4} className="mb-4" key={court.id}>
                <Card className="h-100 hover-card">
                  <Card.Img
                    variant="top"
                    src={court.facility?.coverImageUrl || placeholderImage}
                    alt={court.name}
                    style={{ height: '200px', objectFit: 'cover' }}
                  />
                  <Card.Body>
                    <Card.Title className="fw-bold">{court.name}</Card.Title>
                    <Card.Text className="text-muted mb-2">
                      {court.facility?.address}
                    </Card.Text>
                    <Card.Text className="mb-2">
                      <strong>Cơ sở:</strong> {court.facility?.name}
                    </Card.Text>
                    <Card.Text className="mb-2">
                      <strong>Loại:</strong> {court.sportType?.name}
                    </Card.Text>
                    <Card.Text className="mb-2">
                      <strong>Loại mặt sân:</strong> {court.surfaceType || 'N/A'}
                    </Card.Text>
                    <Card.Text className="mb-2">
                      {court.isIndoor ? 'Trong nhà' : 'Ngoài trời'}
                    </Card.Text>
                    {court.facility && (
                      <Card.Text>
                        <strong>Đánh giá:</strong> {court.facility.avgRating || 'N/A'} / 5
                        ({court.facility.totalReviews || 0} đánh giá)
                      </Card.Text>
                    )}
                  </Card.Body>
                  <Card.Footer className="bg-white">
                    <Link to={`/court/${court.id}`} className="w-100 d-block">
                      <Button variant="primary" className="w-100">
                        Xem chi tiết và đặt sân
                      </Button>
                    </Link>
                  </Card.Footer>
                </Card>
              </Col>
            ))}
          </Row>
        </>
      )}
    </Container>
  );
};

export default CourtSearch;

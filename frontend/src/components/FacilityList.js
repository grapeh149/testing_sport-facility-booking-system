import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import facilityService from '../services/facilityService';

const FacilityList = () => {
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

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

  const canGoPrev = page > 0;
  const canGoNext = totalPages > 0 && page < totalPages - 1;
  const placeholderImage = 'https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg';

  useEffect(() => {
    loadFacilities();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const loadFacilities = async () => {
    try {
      setLoading(true);
      const response = await facilityService.getFacilities(page, 6);
      const payload = unwrapApiResponse(response, 'Khong the tai danh sach san') || {};
      const facilityList = payload.content || payload || [];
      setFacilities(Array.isArray(facilityList) ? facilityList : []);
      setTotalPages(payload.totalPages || 0);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách sân');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleNextPage = () => {
    if (!loading && canGoNext) {
      setPage((prev) => prev + 1);
    }
  };

  const handlePrevPage = () => {
    if (!loading && canGoPrev) {
      setPage((prev) => prev - 1);
    }
  };

  return (
    <Container className="py-5">
      <div className="d-flex justify-content-between align-items-end mb-4 flex-wrap gap-2">
        <div>
          <h1 className="mb-1">Các cơ sở thể thao hàng đầu</h1>
          {/* <p className="text-muted mb-0">Các sân thể thao phổ biến nhất trong khu vực của bạn.</p> */}
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Đang tải...</span>
          </Spinner>
        </div>
      ) : facilities.length === 0 ? (
        <Alert variant="info">Không tìm thấy sân nào</Alert>
      ) : (
        <>
          <Row>
            {facilities.map((facility) => (
              <Col md={4} className="mb-4" key={facility.id}>
                <Card className="h-100">
                  <Card.Img
                    variant="top"
                    src={facility.coverImageUrl || placeholderImage}
                    alt={facility.name}
                    style={{ height: '200px', objectFit: 'cover' }}
                  />
                  <Card.Body>
                    <Card.Title className="mb-2">{facility.name}</Card.Title>
                    <Card.Text className="text-muted mb-2">
                      {facility.address}
                    </Card.Text>
                    {/* <Card.Text className="mb-2">
                      <strong>Đơn vị:</strong> {facility.companyName || 'N/A'}
                    </Card.Text> */}
                    <Card.Text className="mb-2">
                      <strong>SĐT:</strong> {facility.phone}
                    </Card.Text>
                    <Card.Text className="mb-0">
                      <strong>Đánh giá:</strong> {facility.avgRating || facility.rating || 'Chưa có đánh giá'}
                    </Card.Text>
                  </Card.Body>
                  <Card.Footer className="bg-white border-0 pt-0 pb-3">
                    <Link to={`/facility/${facility.id}`}>
                      <Button variant="primary" className="w-100">
                        Xem chi tiết cơ sở
                      </Button>
                    </Link>
                  </Card.Footer>
                </Card>
              </Col>
            ))}
          </Row>

          {/* Pagination */}
          <nav className="mt-4">
            <ul className="pagination justify-content-center">
              <li className={`page-item ${!canGoPrev ? 'disabled' : ''}`}>
                <Button variant="outline-secondary" onClick={handlePrevPage} disabled={!canGoPrev || loading}>
                  Trước
                </Button>
              </li>
              <li className="page-item active mx-2">
                <span className="page-link">
                  Trang {totalPages === 0 ? 0 : page + 1} / {totalPages}
                </span>
              </li>
              <li
                className={`page-item ${!canGoNext ? 'disabled' : ''}`}
              >
                <Button variant="outline-secondary" onClick={handleNextPage} disabled={!canGoNext || loading}>
                  Tiếp
                </Button>
              </li>
            </ul>
          </nav>
        </>
      )}
    </Container>
  );
};

export default FacilityList;

import React, { useState } from 'react';
import { Modal, Button, Form, Alert } from 'react-bootstrap';
import reviewService from '../services/reviewService';

const ReviewModal = ({ show, onHide, booking, onSuccess }) => {
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleClose = () => {
    setRating(5);
    setComment('');
    setError('');
    setSuccess('');
    onHide();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!comment.trim()) {
      setError('Vui lòng nhập nhận xét');
      return;
    }

    try {
      setLoading(true);
      setError('');
      setSuccess('');

      const reviewData = {
        rating: parseInt(rating),
        comment: comment.trim(),
        bookingId: booking.id,
      };

      const response = await reviewService.createReview(booking.id, reviewData);
      
      if (response?.data?.success) {
        setSuccess('Đánh giá thành công!');
        setRating(5);
        setComment('');
        
        // Đóng modal sau 1.5 giây
        setTimeout(() => {
          handleClose();
          onSuccess && onSuccess();
        }, 1500);
      } else {
        setError(response?.data?.message || 'Không thể gửi đánh giá');
      }
    } catch (err) {
      console.error('Review error:', err);
      setError(err.response?.data?.message || err.message || 'Không thể gửi đánh giá');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>Đánh giá sân</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {booking && (
          <div className="mb-3">
            <p className="mb-1">
              <strong>Sân:</strong> {booking.courtName}
            </p>
            <p>
              <strong>Cơ sở:</strong> {booking.facilityName}
            </p>
          </div>
        )}

        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Đánh giá sao <span className="text-danger">*</span></Form.Label>
            <div className="rating-input">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  className={`btn btn-lg me-2 ${rating >= star ? 'btn-warning' : 'btn-outline-warning'}`}
                  style={{ fontSize: '1.5rem' }}
                  onClick={() => setRating(star)}
                  disabled={loading}
                >
                  ⭐
                </button>
              ))}
            </div>
            <small className="text-muted">Bạn chọn: {rating} sao</small>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Nhận xét <span className="text-danger">*</span></Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              placeholder="Chia sẻ trải nghiệm của bạn về sân..."
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              disabled={loading}
              maxLength={1000}
            />
            <small className="text-muted">{comment.length}/1000</small>
          </Form.Group>

          <div className="d-grid gap-2">
            <Button 
              variant="primary" 
              type="submit"
              disabled={loading}
            >
              {loading ? 'Đang gửi...' : 'Gửi đánh giá'}
            </Button>
            <Button 
              variant="secondary" 
              onClick={handleClose}
              disabled={loading}
            >
              Đóng
            </Button>
          </div>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default ReviewModal;

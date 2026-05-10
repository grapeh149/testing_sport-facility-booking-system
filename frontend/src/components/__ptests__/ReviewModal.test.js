import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import ReviewModal from '../ReviewModal';
import reviewService from '../../services/reviewService';

jest.mock('../../services/reviewService');

const mockBooking = {
  id: 1,
  courtName: 'Sân A',
  facilityName: 'Cơ sở ABC',
};

const defaultProps = {
  show: true,
  onHide: jest.fn(),
  booking: mockBooking,
  onSuccess: jest.fn(),
};

beforeEach(() => {
  jest.useFakeTimers();
});

afterEach(() => {
  jest.runOnlyPendingTimers();
  jest.useRealTimers();
  jest.clearAllMocks();
});

describe('ReviewModal – Conditional Rendering', () => {
  test('renders modal with booking info when show=true', () => {
    render(<ReviewModal {...defaultProps} />);
    expect(screen.getByText('Đánh giá sân')).toBeInTheDocument();
    expect(screen.getByText(/Sân A/)).toBeInTheDocument();
    expect(screen.getByText(/Cơ sở ABC/)).toBeInTheDocument();
  });

  test('does not render booking info block when booking is null', () => {
    render(<ReviewModal {...defaultProps} booking={null} />);
    expect(screen.queryByText(/Sân A/)).not.toBeInTheDocument();
  });

  test('renders 5 star buttons', () => {
    render(<ReviewModal {...defaultProps} />);
    const starButtons = screen.getAllByRole('button', { name: '⭐' });
    expect(starButtons).toHaveLength(5);
  });

  test('shows character counter at 0 initially', () => {
    render(<ReviewModal {...defaultProps} />);
    expect(screen.getByText('0/1000')).toBeInTheDocument();
  });

  test('does not render error or success alert initially', () => {
    render(<ReviewModal {...defaultProps} />);
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });
});

describe('ReviewModal – Star Rating Interaction', () => {
  test('default rating is 5 stars', () => {
    render(<ReviewModal {...defaultProps} />);
    expect(screen.getByText('Bạn chọn: 5 sao')).toBeInTheDocument();
  });

  test('clicking star 3 updates rating display', () => {
    render(<ReviewModal {...defaultProps} />);
    const starButtons = screen.getAllByRole('button', { name: '⭐' });
    fireEvent.click(starButtons[2]); // index 2 = star 3
    expect(screen.getByText('Bạn chọn: 3 sao')).toBeInTheDocument();
  });

  test('clicking star 1 updates rating to 1', () => {
    render(<ReviewModal {...defaultProps} />);
    const starButtons = screen.getAllByRole('button', { name: '⭐' });
    fireEvent.click(starButtons[0]);
    expect(screen.getByText('Bạn chọn: 1 sao')).toBeInTheDocument();
  });

  test('clicking star 5 keeps rating at 5', () => {
    render(<ReviewModal {...defaultProps} />);
    const starButtons = screen.getAllByRole('button', { name: '⭐' });
    fireEvent.click(starButtons[4]);
    expect(screen.getByText('Bạn chọn: 5 sao')).toBeInTheDocument();
  });
});

describe('ReviewModal – Comment Input', () => {
  test('typing comment updates character counter', () => {
    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'San rat tot' } });
    expect(screen.getByText('11/1000')).toBeInTheDocument();
  });

  test('textarea has maxLength of 1000', () => {
    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    expect(textarea).toHaveAttribute('maxLength', '1000');
  });
});

describe('ReviewModal – Form Validation', () => {
  test('shows validation error when submitting with empty comment', async () => {
    render(<ReviewModal {...defaultProps} />);
    const submitBtn = screen.getByRole('button', { name: 'Gửi đánh giá' });
    fireEvent.click(submitBtn);
    expect(await screen.findByText('Vui lòng nhập nhận xét')).toBeInTheDocument();
    expect(reviewService.createReview).not.toHaveBeenCalled();
  });

  test('shows validation error when comment is only spaces', async () => {
    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: '   ' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));
    expect(await screen.findByText('Vui lòng nhập nhận xét')).toBeInTheDocument();
  });
});

describe('ReviewModal – Async Submit Success', () => {
  test('shows success message and calls onSuccess after delay', async () => {
    reviewService.createReview.mockResolvedValueOnce({
      data: { success: true, data: {} },
    });

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Sân rất tốt' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Đang gửi...')).toBeInTheDocument();
    expect(await screen.findByText('Đánh giá thành công!')).toBeInTheDocument();

    act(() => jest.advanceTimersByTime(1500));

    expect(defaultProps.onSuccess).toHaveBeenCalled();
    expect(defaultProps.onHide).toHaveBeenCalled();
  });

  test('calls reviewService.createReview with correct data', async () => {
    reviewService.createReview.mockResolvedValueOnce({
      data: { success: true, data: {} },
    });

    render(<ReviewModal {...defaultProps} />);
    const starButtons = screen.getAllByRole('button', { name: '⭐' });
    fireEvent.click(starButtons[2]); // rating = 3
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Được thôi' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    await waitFor(() => {
      expect(reviewService.createReview).toHaveBeenCalledWith(1, {
        rating: 3,
        comment: 'Được thôi',
        bookingId: 1,
      });
    });
  });
});

describe('ReviewModal – Async Submit Failure', () => {
  test('shows error when response.data.success is false', async () => {
    reviewService.createReview.mockResolvedValueOnce({
      data: { success: false, message: 'Đã đánh giá rồi' },
    });

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Đã đánh giá rồi')).toBeInTheDocument();
  });

  test('shows fallback error when response has no message', async () => {
    reviewService.createReview.mockResolvedValueOnce({
      data: { success: false },
    });

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Không thể gửi đánh giá')).toBeInTheDocument();
  });

  test('shows error from caught exception', async () => {
    reviewService.createReview.mockRejectedValueOnce({
      response: { data: { message: 'Lỗi server' } },
    });

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Lỗi server')).toBeInTheDocument();
  });

  test('shows generic error when exception has no message', async () => {
    reviewService.createReview.mockRejectedValueOnce(new Error('Network'));

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Network')).toBeInTheDocument();
  });
});

describe('ReviewModal – handleClose', () => {
  test('clicking Đóng calls onHide and resets form', () => {
    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });

    fireEvent.click(screen.getByRole('button', { name: 'Đóng' }));
    expect(defaultProps.onHide).toHaveBeenCalled();
  });

  test('onSuccess is optional – does not throw when undefined', async () => {
    reviewService.createReview.mockResolvedValueOnce({
      data: { success: true, data: {} },
    });

    render(<ReviewModal {...defaultProps} onSuccess={undefined} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    await screen.findByText('Đánh giá thành công!');
    act(() => jest.advanceTimersByTime(1500));
    // should not throw
  });
});

describe('ReviewModal – Loading State', () => {
  test('disables buttons while submitting', async () => {
    let resolve;
    reviewService.createReview.mockReturnValueOnce(new Promise(r => { resolve = r; }));

    render(<ReviewModal {...defaultProps} />);
    const textarea = screen.getByPlaceholderText(/Chia sẻ trải nghiệm/);
    fireEvent.change(textarea, { target: { value: 'Test' } });
    fireEvent.click(screen.getByRole('button', { name: 'Gửi đánh giá' }));

    expect(await screen.findByText('Đang gửi...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Đóng' })).toBeDisabled();
    expect(screen.getAllByRole('button', { name: '⭐' })[0]).toBeDisabled();

    resolve({ data: { success: true } });
  });
});

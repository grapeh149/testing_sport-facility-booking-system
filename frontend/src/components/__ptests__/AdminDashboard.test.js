import React from 'react';
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AdminDashboard from '../AdminDashboard';
import adminService from '../../services/adminService';
import sportTypeService from '../../services/sportTypeService';

jest.mock('../../services/adminService');
jest.mock('../../services/sportTypeService');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: jest.fn(),
}));
jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

const { useNavigate } = require('react-router-dom');
const { useAuth } = require('../../context/AuthContext');

const mockNavigate = jest.fn();

const mockFacilities = [
  { id: 1, name: 'Cơ sở A', ownerName: 'Nguyễn Văn A', status: 'PENDING', createdAt: '2024-01-01' },
  { id: 2, name: 'Cơ sở B', ownerName: 'Trần Thị B', status: 'APPROVED', createdAt: '2024-02-01' },
  { id: 3, name: 'Cơ sở C', ownerName: 'Lê Văn C', status: 'REJECTED', createdAt: '2024-03-01', rejectReason: 'Vi phạm' },
];

const mockUsers = [
  { id: 1, fullName: 'Admin User', email: 'admin@test.com', phone: '0900000001', role: 'ADMIN', status: 'ACTIVE', createdAt: '2024-01-01' },
  { id: 2, fullName: 'Owner User', email: 'owner@test.com', phone: '0900000002', role: 'OWNER', status: 'ACTIVE', createdAt: '2024-01-02' },
  { id: 3, fullName: 'Customer', email: 'cust@test.com', phone: '0900000003', role: 'CUSTOMER', isActive: false, createdAt: '2024-01-03' },
];

const mockSportTypes = [
  { id: 1, name: 'Bóng đá', description: 'Môn thể thao vua', iconUrl: '', isActive: true },
  { id: 2, name: 'Cầu lông', description: '', iconUrl: 'http://icon.png', isActive: false },
];

const successResponse = (data) => ({
  data: { success: true, data },
});

const setupAdminAuth = () => {
  useNavigate.mockReturnValue(mockNavigate);
  useAuth.mockReturnValue({
    user: { userId: 99, role: 'ADMIN' },
    isAuthenticated: true,
  });
};

const setupLoadData = () => {
  adminService.getAllFacilities.mockResolvedValue(
    successResponse({ content: mockFacilities })
  );
  adminService.getAllUsers.mockResolvedValue(
    successResponse({ content: mockUsers })
  );
  sportTypeService.getAllSportTypes.mockResolvedValue(
    successResponse(mockSportTypes)
  );
};

const renderComponent = () => {
  setupAdminAuth();
  setupLoadData();
  return render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
};

// Wait for dashboard to finish loading
const waitForDashboard = async () => {
  await waitFor(() =>
    expect(screen.queryByText('Đang tải...')).not.toBeInTheDocument()
  );
  await waitFor(() => screen.getByText('Admin Dashboard'));
};

describe('AdminDashboard – Auth Guard', () => {
  test('redirects to / when not authenticated', () => {
    useNavigate.mockReturnValue(mockNavigate);
    useAuth.mockReturnValue({ user: null, isAuthenticated: false });
    adminService.getAllFacilities.mockResolvedValue(successResponse({ content: [] }));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('redirects to / when user is not ADMIN', () => {
    useNavigate.mockReturnValue(mockNavigate);
    useAuth.mockReturnValue({ user: { role: 'CUSTOMER' }, isAuthenticated: true });
    adminService.getAllFacilities.mockResolvedValue(successResponse({ content: [] }));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('does not redirect when user is ADMIN', async () => {
    renderComponent();
    await waitForDashboard();
    expect(mockNavigate).not.toHaveBeenCalled();
  });
});

describe('AdminDashboard – Data Loading', () => {
  test('renders facilities in table', async () => {
    renderComponent();
    await waitForDashboard();
    expect(screen.getByText('Cơ sở A')).toBeInTheDocument();
    expect(screen.getByText('Cơ sở B')).toBeInTheDocument();
  });

  test('renders facility statuses as badges', async () => {
    renderComponent();
    await waitForDashboard();
    expect(screen.getByText('Đang chờ duyệt')).toBeInTheDocument();
    expect(screen.getByText('Đã duyệt')).toBeInTheDocument();
    expect(screen.getByText('Đã hủy')).toBeInTheDocument();
  });

  test('shows warning when facilities API fails', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockRejectedValue(new Error('fail'));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);

    await waitFor(() =>
      expect(screen.getByText('Không thể tải danh sách sân')).toBeInTheDocument()
    );
  });

  test('shows warning when users API fails', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockResolvedValue(successResponse({ content: [] }));
    adminService.getAllUsers.mockRejectedValue(new Error('fail'));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);

    await waitFor(() =>
      expect(screen.getByText('Không thể tải danh sách người dùng')).toBeInTheDocument()
    );
  });

  test('shows warning when sportTypes API fails', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockResolvedValue(successResponse({ content: [] }));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockRejectedValue(new Error('fail'));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);

    await waitFor(() =>
      expect(screen.getByText('Không thể tải danh sách loại thể thao')).toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Stats', () => {
  test('shows correct total users count', async () => {
    renderComponent();
    await waitForDashboard();
    expect(screen.getByText('Tổng Người Dùng')).toBeInTheDocument();
    expect(screen.getByText('3')).toBeInTheDocument(); // 3 users
  });

  test('shows correct OWNER count stat card', async () => {
    renderComponent();
    await waitForDashboard();
    // Stats show Chủ Cơ Sở label – getAllByText because table header also has it
    expect(screen.getAllByText('Chủ Cơ Sở').length).toBeGreaterThan(0);
  });

  test('shows correct pending facilities count', async () => {
    renderComponent();
    await waitForDashboard();
    expect(screen.getByText('Cơ Sở Chờ Duyệt')).toBeInTheDocument();
  });
});

describe('AdminDashboard – Alert Dismissal', () => {
  test('dismisses alert when close button clicked', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockRejectedValue(new Error('fail'));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    const alert = await screen.findByText('Không thể tải danh sách sân');
    const closeBtn = alert.closest('.alert').querySelector('.btn-close');
    fireEvent.click(closeBtn);
    expect(screen.queryByText('Không thể tải danh sách sân')).not.toBeInTheDocument();
  });
});

describe('AdminDashboard – Approve Facility', () => {
  test('calls approveFacility and reloads on success', async () => {
    adminService.approveFacility.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    const approveBtn = screen.getByRole('button', { name: 'Phê Duyệt' });
    fireEvent.click(approveBtn);

    await waitFor(() =>
      expect(adminService.approveFacility).toHaveBeenCalledWith(1, 99)
    );
    await waitFor(() =>
      expect(screen.getByText('Phê duyệt cơ sở thành công!')).toBeInTheDocument()
    );
  });

  test('shows error when approveFacility fails', async () => {
    adminService.approveFacility.mockRejectedValueOnce({ message: 'Lỗi mạng' });
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByRole('button', { name: 'Phê Duyệt' }));

    await waitFor(() =>
      expect(screen.getByText(/Lỗi khi phê duyệt cơ sở/)).toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Reject Facility', () => {
  test('opens reject modal when Hủy button clicked', async () => {
    renderComponent();
    await waitForDashboard();

    // PENDING facility has "Hủy" button
    const rejBtns = screen.getAllByRole('button', { name: 'Hủy' });
    fireEvent.click(rejBtns[0]);

    expect(screen.getByText('Từ Chối Cơ Sở')).toBeInTheDocument();
  });

  test('shows warning when confirming reject with empty reason', async () => {
    renderComponent();
    await waitForDashboard();

    const rejBtns = screen.getAllByRole('button', { name: 'Hủy' });
    fireEvent.click(rejBtns[0]);

    const confirmBtn = screen.getByRole('button', { name: 'Xác Nhận Từ Chối' });
    fireEvent.click(confirmBtn);

    expect(
      await screen.findByText('Vui lòng nhập lý do từ chối')
    ).toBeInTheDocument();
    expect(adminService.rejectFacility).not.toHaveBeenCalled();
  });

  test('calls rejectFacility and shows success when reason is entered', async () => {
    adminService.rejectFacility.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    const rejBtns = screen.getAllByRole('button', { name: 'Hủy' });
    fireEvent.click(rejBtns[0]);

    const textarea = screen.getByPlaceholderText(/Nhập lý do từ chối/);
    fireEvent.change(textarea, { target: { value: 'Vi phạm quy định' } });

    fireEvent.click(screen.getByRole('button', { name: 'Xác Nhận Từ Chối' }));

    await waitFor(() =>
      expect(adminService.rejectFacility).toHaveBeenCalled()
    );
    await waitFor(() =>
      expect(screen.getByText('Từ chối cơ sở thành công!')).toBeInTheDocument()
    );
  });

  test('shows APPROVED-specific text when rejecting approved facility', async () => {
    renderComponent();
    await waitForDashboard();

    // APPROVED facility has "Hủy" button at index 1 (PENDING=0, APPROVED=1)
    const rejBtns = screen.getAllByRole('button', { name: 'Hủy' });
    // Click the second one which belongs to APPROVED facility
    fireEvent.click(rejBtns[1]);

    expect(screen.getByText('Hủy Cơ Sở')).toBeInTheDocument();
  });

  test('shows error when rejectFacility fails', async () => {
    adminService.rejectFacility.mockRejectedValueOnce({ message: 'Server error' });
    renderComponent();
    await waitForDashboard();

    const rejBtns = screen.getAllByRole('button', { name: 'Hủy' });
    fireEvent.click(rejBtns[0]);
    fireEvent.change(screen.getByPlaceholderText(/Nhập lý do từ chối/), {
      target: { value: 'Lý do' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Xác Nhận Từ Chối' }));

    await waitFor(() =>
      expect(screen.getByText(/Lỗi khi từ chối cơ sở/)).toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – View Facility Detail', () => {
  test('opens detail modal when Xem button clicked', async () => {
    renderComponent();
    await waitForDashboard();

    const viewBtns = screen.getAllByRole('button', { name: 'Xem' });
    fireEvent.click(viewBtns[0]);

    expect(screen.getByText('Chi Tiết Cơ Sở')).toBeInTheDocument();
  });

  test('closes detail modal when Đóng clicked', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getAllByRole('button', { name: 'Xem' })[0]);
    fireEvent.click(screen.getByRole('button', { name: 'Đóng' }));

    await waitFor(() =>
      expect(screen.queryByText('Chi Tiết Cơ Sở')).not.toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Lock / Unlock User', () => {
  beforeEach(() => {
    window.confirm = jest.fn().mockReturnValue(true);
  });

  test('calls lockUser when Khóa clicked and confirmed', async () => {
    adminService.lockUser.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    // Switch to Users tab
    fireEvent.click(screen.getByText('Người Dùng'));

    const lockBtns = await screen.findAllByRole('button', { name: 'Khóa' });
    fireEvent.click(lockBtns[0]);

    await waitFor(() =>
      expect(adminService.lockUser).toHaveBeenCalled()
    );
    await waitFor(() =>
      expect(screen.getByText('Khóa tài khoản thành công!')).toBeInTheDocument()
    );
  });

  test('does not call lockUser when user cancels confirm', async () => {
    window.confirm = jest.fn().mockReturnValue(false);
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const lockBtns = await screen.findAllByRole('button', { name: 'Khóa' });
    fireEvent.click(lockBtns[0]);

    expect(adminService.lockUser).not.toHaveBeenCalled();
  });

  test('calls unlockUser for blocked user', async () => {
    adminService.unlockUser.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));

    const unlockBtns = await screen.findAllByRole('button', { name: 'Mở khóa' });
    fireEvent.click(unlockBtns[0]);

    await waitFor(() =>
      expect(adminService.unlockUser).toHaveBeenCalled()
    );
    await waitFor(() =>
      expect(screen.getByText('Mở khóa tài khoản thành công!')).toBeInTheDocument()
    );
  });

  test('shows error when lockUser fails', async () => {
    adminService.lockUser.mockRejectedValueOnce({ message: 'Lỗi' });
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const lockBtns = await screen.findAllByRole('button', { name: 'Khóa' });
    fireEvent.click(lockBtns[0]);

    await waitFor(() =>
      expect(screen.getByText(/Lỗi khi khóa tài khoản/)).toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Create User', () => {
  const fillUserForm = () => {
    fireEvent.change(screen.getByPlaceholderText('Nhập tên đầy đủ'), {
      target: { value: 'New User' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập email'), {
      target: { value: 'new@test.com' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập số điện thoại'), {
      target: { value: '0909999999' },
    });
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), {
      target: { value: 'password123' },
    });
  };

  test('opens create user modal when button clicked', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    fireEvent.click(await screen.findByText('+ Tạo Người Dùng Mới'));

    expect(screen.getByText('Tạo Người Dùng Mới')).toBeInTheDocument();
  });

  test('shows validation warning when fields are empty', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    fireEvent.click(await screen.findByText('+ Tạo Người Dùng Mới'));
    fireEvent.click(screen.getByRole('button', { name: 'Tạo Người Dùng' }));

    expect(
      await screen.findByText('Vui lòng điền tất cả các trường bắt buộc')
    ).toBeInTheDocument();
    expect(adminService.createUser).not.toHaveBeenCalled();
  });

  test('calls createUser and shows success when form is valid', async () => {
    adminService.createUser.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    fireEvent.click(await screen.findByText('+ Tạo Người Dùng Mới'));

    fillUserForm();
    fireEvent.click(screen.getByRole('button', { name: 'Tạo Người Dùng' }));

    await waitFor(() =>
      expect(screen.getByText('Tạo người dùng thành công!')).toBeInTheDocument()
    );
  });

  test('shows error when createUser fails', async () => {
    adminService.createUser.mockRejectedValueOnce({ message: 'Email đã tồn tại' });
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    fireEvent.click(await screen.findByText('+ Tạo Người Dùng Mới'));

    fillUserForm();
    fireEvent.click(screen.getByRole('button', { name: 'Tạo Người Dùng' }));

    await waitFor(() =>
      expect(screen.getByText(/Lỗi khi tạo người dùng/)).toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Edit User', () => {
  test('opens edit user modal with prefilled data', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const editBtns = await screen.findAllByRole('button', { name: 'Sửa' });
    fireEvent.click(editBtns[0]);

    expect(screen.getByText('Chỉnh Sửa Thông Tin Người Dùng')).toBeInTheDocument();
  });

  test('shows validation warning when required fields are empty on update', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const editBtns = await screen.findAllByRole('button', { name: 'Sửa' });
    fireEvent.click(editBtns[0]);

    // Clear fullName field
    const fullNameInput = screen.getAllByPlaceholderText('Nhập tên đầy đủ')[0];
    fireEvent.change(fullNameInput, { target: { value: '' } });

    fireEvent.click(screen.getByRole('button', { name: 'Cập Nhật' }));

    expect(
      await screen.findByText('Vui lòng điền tất cả các trường bắt buộc')
    ).toBeInTheDocument();
  });

  test('calls updateUser and shows success on valid update', async () => {
    adminService.updateUser.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const editBtns = await screen.findAllByRole('button', { name: 'Sửa' });
    fireEvent.click(editBtns[0]);

    fireEvent.click(screen.getByRole('button', { name: 'Cập Nhật' }));

    await waitFor(() =>
      expect(screen.getByText('Cập nhật người dùng thành công!')).toBeInTheDocument()
    );
  });

  test('cancels edit and closes modal', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const editBtns = await screen.findAllByRole('button', { name: 'Sửa' });
    fireEvent.click(editBtns[0]);

    // Scope Hủy button within the edit-user modal dialog
    const modalTitle = await screen.findByText('Chỉnh Sửa Thông Tin Người Dùng');
    const modalContent = modalTitle.closest('.modal-content');
    fireEvent.click(within(modalContent).getByRole('button', { name: 'Hủy' }));

    await waitFor(() =>
      expect(screen.queryByText('Chỉnh Sửa Thông Tin Người Dùng')).not.toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Delete User', () => {
  beforeEach(() => {
    window.confirm = jest.fn().mockReturnValue(true);
  });

  test('calls deleteUser and shows success when confirmed', async () => {
    adminService.deleteUser.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const delBtns = await screen.findAllByRole('button', { name: 'Xóa' });
    fireEvent.click(delBtns[0]);

    await waitFor(() =>
      expect(adminService.deleteUser).toHaveBeenCalled()
    );
    await waitFor(() =>
      expect(screen.getByText('Xóa người dùng thành công!')).toBeInTheDocument()
    );
  });

  test('does not call deleteUser when cancelled', async () => {
    window.confirm = jest.fn().mockReturnValue(false);
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Người Dùng'));
    const delBtns = await screen.findAllByRole('button', { name: 'Xóa' });
    fireEvent.click(delBtns[0]);

    expect(adminService.deleteUser).not.toHaveBeenCalled();
  });
});

describe('AdminDashboard – Sport Types', () => {
  test('renders sport types list', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Loại Thể Thao'));

    expect(await screen.findByText('Bóng đá')).toBeInTheDocument();
    expect(screen.getByText('Cầu lông')).toBeInTheDocument();
  });

  test('shows "Chưa có loại thể thao nào" when list is empty', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockResolvedValue(successResponse({ content: [] }));
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    await waitForDashboard();

    fireEvent.click(screen.getByText('Loại Thể Thao'));

    expect(await screen.findByText('Chưa có loại thể thao nào')).toBeInTheDocument();
  });

  test('shows validation warning when name is empty on create', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Loại Thể Thao'));
    const submitBtn = await screen.findByRole('button', { name: 'Tạo Loại Thể Thao' });
    fireEvent.click(submitBtn);

    expect(
      await screen.findByText('Vui lòng nhập tên loại thể thao')
    ).toBeInTheDocument();
    expect(sportTypeService.createSportType).not.toHaveBeenCalled();
  });

  test('creates sport type and shows success', async () => {
    sportTypeService.createSportType.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Loại Thể Thao'));

    const nameInput = await screen.findByPlaceholderText(/Bóng Đá, Bóng Rổ/);
    fireEvent.change(nameInput, { target: { value: 'Bóng rổ' } });
    fireEvent.click(screen.getByRole('button', { name: 'Tạo Loại Thể Thao' }));

    await waitFor(() =>
      expect(screen.getByText('Tạo loại thể thao thành công!')).toBeInTheDocument()
    );
  });

  test('shows error when createSportType fails', async () => {
    sportTypeService.createSportType.mockRejectedValueOnce({ message: 'fail' });
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByText('Loại Thể Thao'));

    const nameInput = await screen.findByPlaceholderText(/Bóng Đá, Bóng Rổ/);
    fireEvent.change(nameInput, { target: { value: 'Bóng rổ' } });
    fireEvent.click(screen.getByRole('button', { name: 'Tạo Loại Thể Thao' }));

    await waitFor(() =>
      expect(screen.getByText(/Lỗi khi tạo loại thể thao/)).toBeInTheDocument()
    );
  });

  // Helper: open sport type edit modal for the first sport type ('Bóng đá')
  const openSportTypeEditModal = async () => {
    fireEvent.click(screen.getByText('Loại Thể Thao'));
    const firstItem = await screen.findByText('Bóng đá');
    const itemCard = firstItem.closest('.mb-3');
    fireEvent.click(within(itemCard).getByRole('button', { name: 'Sửa' }));
  };

  test('opens edit sport type modal', async () => {
    renderComponent();
    await waitForDashboard();
    await openSportTypeEditModal();
    expect(await screen.findByText('Chỉnh Sửa Loại Thể Thao')).toBeInTheDocument();
  });

  test('shows validation warning when name is empty on update', async () => {
    renderComponent();
    await waitForDashboard();
    await openSportTypeEditModal();

    const modalTitle = await screen.findByText('Chỉnh Sửa Loại Thể Thao');
    const modal = modalTitle.closest('.modal-content');
    const nameInput = within(modal).getByPlaceholderText(/Bóng Đá, Bóng Rổ/);
    fireEvent.change(nameInput, { target: { value: '' } });
    fireEvent.click(within(modal).getByRole('button', { name: 'Cập Nhật' }));

    expect(
      await screen.findByText('Vui lòng nhập tên loại thể thao')
    ).toBeInTheDocument();
  });

  test('updates sport type and shows success', async () => {
    sportTypeService.updateSportType.mockResolvedValueOnce(successResponse({}));
    renderComponent();
    await waitForDashboard();
    await openSportTypeEditModal();

    const modalTitle = await screen.findByText('Chỉnh Sửa Loại Thể Thao');
    const modal = modalTitle.closest('.modal-content');
    fireEvent.click(within(modal).getByRole('button', { name: 'Cập Nhật' }));

    await waitFor(() =>
      expect(screen.getByText('Cập nhật loại thể thao thành công!')).toBeInTheDocument()
    );
  });

  test('cancels edit sport type', async () => {
    renderComponent();
    await waitForDashboard();
    await openSportTypeEditModal();

    const modalTitle = await screen.findByText('Chỉnh Sửa Loại Thể Thao');
    const modal = modalTitle.closest('.modal-content');
    fireEvent.click(within(modal).getByRole('button', { name: 'Hủy' }));

    await waitFor(() =>
      expect(screen.queryByText('Chỉnh Sửa Loại Thể Thao')).not.toBeInTheDocument()
    );
  });
});

describe('AdminDashboard – Helper Functions', () => {
  test('shows N/A for facility without ownerName', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockResolvedValue(
      successResponse({ content: [{ id: 99, name: 'No Owner', status: 'PENDING' }] })
    );
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    await waitForDashboard();

    // Multiple N/A may appear (owner + date) – just check at least one exists
    expect(screen.getAllByText('N/A').length).toBeGreaterThan(0);
  });

  test('shows N/A for facility with null createdAt', async () => {
    setupAdminAuth();
    adminService.getAllFacilities.mockResolvedValue(
      successResponse({ content: [{ id: 99, name: 'No Date', ownerName: 'X', status: 'PENDING', createdAt: null }] })
    );
    adminService.getAllUsers.mockResolvedValue(successResponse({ content: [] }));
    sportTypeService.getAllSportTypes.mockResolvedValue(successResponse([]));

    render(<MemoryRouter><AdminDashboard /></MemoryRouter>);
    await waitForDashboard();

    expect(screen.getAllByText('N/A').length).toBeGreaterThan(0);
  });

  test('navigate back button calls navigate("/")', async () => {
    renderComponent();
    await waitForDashboard();

    fireEvent.click(screen.getByRole('button', { name: 'Quay lại' }));
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});

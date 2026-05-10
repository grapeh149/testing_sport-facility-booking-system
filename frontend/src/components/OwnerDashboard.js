import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Table, Badge, Tab, Tabs, Form, Modal, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import facilityService from '../services/facilityService';
import courtService from '../services/courtService';
import bookingService from '../services/bookingService';
import sportTypeService from '../services/sportTypeService';
import timeslotService from '../services/timeslotService';
import checkInService from '../services/checkInService';
import { cloudinaryService } from '../services/cloudinaryService';

const OwnerDashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [facilities, setFacilities] = useState([]);
  const [courts, setCourts] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [revenues, setRevenues] = useState([]);
  const [showAddFacility, setShowAddFacility] = useState(false);
  const [showEditFacility, setShowEditFacility] = useState(false);
  const [showAddCourt, setShowAddCourt] = useState(false);
  const [showEditCourt, setShowEditCourt] = useState(false);
  const [showBookingDetail, setShowBookingDetail] = useState(false);
  const [showCancelBookingModal, setShowCancelBookingModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [bookingToCancelId, setBookingToCancelId] = useState(null);
  const [cancelReason, setCancelReason] = useState('');
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState({ type: '', id: null });
  const [editingFacility, setEditingFacility] = useState(null);
  const [editingCourt, setEditingCourt] = useState(null);
  const [sportTypes, setSportTypes] = useState([]);
  const [selectedCourtForTimeslot, setSelectedCourtForTimeslot] = useState('');
  const [courtTimeslots, setCourtTimeslots] = useState([]);
  const [timeslotLoading, setTimeslotLoading] = useState(false);
  const [newCourt, setNewCourt] = useState({
    facilityId: '',
    sportTypeId: 1,
    name: '',
    description: '',
    surfaceType: '',
    isIndoor: false,
    isActive: true
  });
  const [newFacility, setNewFacility] = useState({
    name: '',
    address: '',
    district: '',
    city: '',
    phone: '',
    commissionRate: '10',
    description: '',
    openTime: '07:00',
    closeTime: '23:00',
    cancelBeforeHours: 2
  });
  const [newTimeslot, setNewTimeslot] = useState({
    dayOfWeek: -1,
    startTime: '07:00',
    endTime: '08:00',
    price: '',
    depositRate: '0.30'
  });

  // Image upload states
  const [addFacilityCoverImageFile, setAddFacilityCoverImageFile] = useState(null);
  const [addFacilityCoverImagePreview, setAddFacilityCoverImagePreview] = useState(null);
  const [addFacilityDescImages, setAddFacilityDescImages] = useState([]);
  const [addFacilityDescImagePreviews, setAddFacilityDescImagePreviews] = useState([]);
  const [addFacilityImageUploading, setAddFacilityImageUploading] = useState(false);

  const [editFacilityCoverImageFile, setEditFacilityCoverImageFile] = useState(null);
  const [editFacilityCoverImagePreview, setEditFacilityCoverImagePreview] = useState(null);
  const [editFacilityDescImages, setEditFacilityDescImages] = useState([]);
  const [editFacilityDescImagePreviews, setEditFacilityDescImagePreviews] = useState([]);
  const [editFacilityImageUploading, setEditFacilityImageUploading] = useState(false);

  // Existing images from database
  const [existingCoverImageUrl, setExistingCoverImageUrl] = useState(null);
  const [existingDescImages, setExistingDescImages] = useState([]);
  const [descImageToRemove, setDescImageToRemove] = useState(new Set());

  const dayOptions = [
    { value: -1, label: 'Tất cả các ngày' },
    { value: 0, label: 'Chủ Nhật' },
    { value: 1, label: 'Thứ Hai' },
    { value: 2, label: 'Thứ Ba' },
    { value: 3, label: 'Thứ Tư' },
    { value: 4, label: 'Thứ Năm' },
    { value: 5, label: 'Thứ Sáu' },
    { value: 6, label: 'Thứ Bảy' }
  ];

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

  const renderFacilityRating = (facility) => {
    const avgRating = facility?.avgRating;
    const totalReviews = facility?.totalReviews || 0;
    if (avgRating === null || avgRating === undefined) {
      return 'Chưa có đánh giá';
    }
    const ratingNumber = Number(avgRating);
    if (Number.isNaN(ratingNumber)) {
      return 'Chưa có đánh giá';
    }
    return `${ratingNumber.toFixed(1)} (${totalReviews})`;
  };

  const getDayLabel = (dayOfWeek) => {
    // Handle null or undefined as "Tất cả các ngày" (since backend returns null for all days)
    if (dayOfWeek === null || dayOfWeek === undefined) {
      return 'Tât ca cac ngay';
    }
    const day = dayOptions.find(option => option.value === Number(dayOfWeek));
    return day ? day.label : 'Tất cả các ngày';
  };

  const formatTimeDisplay = (value) => {
    if (!value) {
      return 'N/A';
    }
    return String(value).slice(0, 5);
  };

  const getCourtName = (courtId) => {
    const court = courts.find(item => item.id === courtId);
    if (!court) {
      return `San #${courtId}`;
    }
    const facility = facilities.find(item => item.id === court.facilityId);
    return `${court.name}${facility ? ` - ${facility.name}` : ''}`;
  };

  const getFacilityName = (facilityId) => {
    const facility = facilities.find(f => f.id === facilityId);
    return facility ? facility.name : `Cơ sở #${facilityId}`;
  };

  const getSportTypeName = (sportTypeId) => {
    const sportType = sportTypes.find(st => st.id === sportTypeId);
    return sportType ? sportType.name : `Loại #${sportTypeId}`;
  };

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'OWNER') {
      navigate('/');
      return;
    }
    if (user?.userId) {
      loadOwnerData();
    }
    loadSportTypes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, user, navigate]);

  useEffect(() => {
    if (!selectedCourtForTimeslot) {
      setCourtTimeslots([]);
      return;
    }
    loadTimeslotsByCourt(selectedCourtForTimeslot);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCourtForTimeslot]);

  const loadSportTypes = async () => {
    try {
      const response = await sportTypeService.getActiveSportTypes();
      setSportTypes(response.data?.data || []);
    } catch (err) {
      console.log('Error loading sport types:', err);
      // Fallback to default sport types
      setSportTypes([
        { id: 1, name: 'Bóng Đá' },
        { id: 2, name: 'Bóng Rổ' },
        { id: 3, name: 'Bóng Chuyền' },
        { id: 4, name: '🏸 Cầu Lông' }
      ]);
    }
  };

  const loadOwnerData = async () => {
    try {
      setLoading(true);

      const ownerId = user?.userId;
      if (!ownerId) {
        throw new Error('Khong the xac dinh chu san');
      }

      // Load facilities owned by current owner (includes PENDING facilities)
      const facilitiesResponse = await facilityService.getOwnerFacilities(ownerId);
      const facilitiesData = unwrapApiResponse(facilitiesResponse, 'Khong the tai danh sach san') || [];
      console.log('Loaded facilities:', facilitiesData);
      setFacilities(facilitiesData);

      // Load courts for all facilities
      if (facilitiesData.length > 0) {
        try {
          const allCourts = [];
          for (let fac of facilitiesData) {
            try {
              const courtsResponse = await courtService.getCourtsByFacility(fac.id);
              const courtsData = unwrapApiResponse(courtsResponse, 'Khong the tai danh sach san choi') || [];
              allCourts.push(...courtsData);
            } catch (courtErr) {
              console.log('Could not load courts for facility', fac.id);
            }
          }
          console.log('Loaded courts:', allCourts);
          setCourts(allCourts);
        } catch (err) {
          console.log('Error loading courts:', err);
        }
      }

      // Load bookings for current owner
      try {
        const bookingsResponse = await bookingService.getOwnerAllBookings(0, 100);
        const bookingPayload = unwrapApiResponse(bookingsResponse, 'Khong the tai danh sach don dat') || {};
        const bookingsData = bookingPayload.content || bookingPayload || [];
        console.log('Loaded bookings for owner:', bookingsData);
        setBookings(bookingsData);

        // Calculate revenue from bookings
        calculateRevenue(bookingsData);
      } catch (bookingErr) {
        console.log('Could not load bookings for owner:', bookingErr);
      }
    } catch (err) {
      console.error('Error loading owner data:', err);
      setMessage({ type: 'danger', text: 'Lỗi khi tải dữ liệu: ' + (err.response?.data?.message || err.message) });
    } finally {
      setLoading(false);
    }
  };

  const handleAddFacility = async () => {
    if (!newFacility.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập tên sân' });
      return;
    }
    if (!newFacility.address.trim() || !newFacility.district.trim() || !newFacility.city.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập đầy đủ địa chỉ (tên, địa chỉ chi tiết, quận/huyện, tỉnh/thành phố)' });
      return;
    }
    if (!newFacility.phone.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập số điện thoại' });
      return;
    }
    try {
      setLoading(true);
      const ownerId = user?.userId;
      if (!ownerId) {
        throw new Error('Không thể xác định chủ sân');
      }
      const facilityData = {
        name: newFacility.name,
        address: newFacility.address,
        district: newFacility.district,
        city: newFacility.city,
        phone: newFacility.phone,
        description: newFacility.description,
        openTime: newFacility.openTime,
        closeTime: newFacility.closeTime,
        commissionRate: parseFloat(newFacility.commissionRate),
        cancelBeforeHours: parseInt(newFacility.cancelBeforeHours),
        autoConfirm: false
      };
      const response = await facilityService.createFacility(facilityData, ownerId);
      const createdFacility = unwrapApiResponse(response, 'Them san moi that bai');
      console.log('Facility created:', createdFacility);

      // Upload images after facility is created
      if (addFacilityCoverImageFile || addFacilityDescImages.length > 0) {
        const uploadResult = await uploadFacilityImages(createdFacility.id, addFacilityCoverImageFile, addFacilityDescImages);
        if (!uploadResult.success) {
          throw new Error(uploadResult.message || 'Tạo cơ sở thành công nhưng upload ảnh thất bại. Vui lòng thử lại trong phần chỉnh sửa cơ sở.');
        }
      }

      setMessage({ type: 'success', text: 'Thêm cơ sở mới thành công!' });
      setShowAddFacility(false);
      setNewFacility({
        name: '',
        address: '',
        district: '',
        city: '',
        phone: '',
        commissionRate: '10',
        description: '',
        openTime: '07:00',
        closeTime: '23:00',
        cancelBeforeHours: 2
      });
      await loadOwnerData();
      // Reset image uploads
      setAddFacilityCoverImageFile(null);
      setAddFacilityCoverImagePreview(null);
      setAddFacilityDescImages([]);
      setAddFacilityDescImagePreviews([]);
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi thêm cơ sở mới: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Image upload handlers for Add Facility modal
  const handleAddFacilityCoverImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAddFacilityCoverImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setAddFacilityCoverImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAddFacilityDescImagesChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 5) {
      setMessage({ type: 'warning', text: 'Tối đa 5 ảnh mô tả' });
      return;
    }
    setAddFacilityDescImages(files);
    const previews = [];
    let loaded = 0;
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        previews.push(reader.result);
        loaded++;
        if (loaded === files.length) {
          setAddFacilityDescImagePreviews(previews);
        }
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveAddFacilityDescImage = (index) => {
    const updated = addFacilityDescImages.filter((_, i) => i !== index);
    setAddFacilityDescImages(updated);
    const updatedPreviews = addFacilityDescImagePreviews.filter((_, i) => i !== index);
    setAddFacilityDescImagePreviews(updatedPreviews);
  };

  // Image upload handlers for Edit Facility modal
  const handleEditFacilityCoverImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setEditFacilityCoverImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setEditFacilityCoverImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleEditFacilityDescImagesChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 5) {
      setMessage({ type: 'warning', text: 'Tối đa 5 ảnh mô tả' });
      return;
    }
    setEditFacilityDescImages(files);
    const previews = [];
    let loaded = 0;
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        previews.push(reader.result);
        loaded++;
        if (loaded === files.length) {
          setEditFacilityDescImagePreviews(previews);
        }
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveEditFacilityDescImage = (index) => {
    const updated = editFacilityDescImages.filter((_, i) => i !== index);
    setEditFacilityDescImages(updated);
    const updatedPreviews = editFacilityDescImagePreviews.filter((_, i) => i !== index);
    setEditFacilityDescImagePreviews(updatedPreviews);
  };

  const handleMarkExistingDescImageForRemoval = (imageId) => {
    const newSet = new Set(descImageToRemove);
    if (newSet.has(imageId)) {
      newSet.delete(imageId);
    } else {
      newSet.add(imageId);
    }
    setDescImageToRemove(newSet);
  };

  const uploadFacilityImages = async (facilityId, coverFile, descFiles) => {
    try {
      setAddFacilityImageUploading(true);

      if (coverFile) {
        const coverImageUrl = await cloudinaryService.uploadFacilityImage(coverFile);
        if (!coverImageUrl) {
          throw new Error('Không lấy được URL ảnh bìa từ Cloudinary');
        }
        const updateResponse = await facilityService.updateFacilityCoverImage(facilityId, coverImageUrl);
        unwrapApiResponse(updateResponse, 'Khong the luu cover image');
      }

      if (descFiles && descFiles.length > 0) {
        const uploadedUrls = [];
        for (const file of descFiles) {
          const imageUrl = await cloudinaryService.uploadFacilityImage(file);
          if (!imageUrl) {
            throw new Error('Không lấy được URL ảnh mô tả từ Cloudinary');
          }
          uploadedUrls.push(imageUrl);
        }

        if (uploadedUrls.length > 0) {
          const dbResponse = await facilityService.addFacilityImages(facilityId, uploadedUrls);
          unwrapApiResponse(dbResponse, 'Khong the luu anh mo ta');
        }
      }

      return { success: true, message: 'Upload ảnh thành công' };
    } catch (err) {
      return {
        success: false,
        message: err.response?.data?.message || err.message || 'Upload ảnh thất bại',
      };
    } finally {
      setAddFacilityImageUploading(false);
    }
  };

  const uploadFacilityImagesForEdit = async (facilityId, coverFile, descFiles) => {
    try {
      setEditFacilityImageUploading(true);

      if (coverFile) {
        const coverImageUrl = await cloudinaryService.uploadFacilityImage(coverFile);
        if (!coverImageUrl) {
          throw new Error('Không lấy được URL ảnh bìa từ Cloudinary');
        }
        const updateResponse = await facilityService.updateFacilityCoverImage(facilityId, coverImageUrl);
        unwrapApiResponse(updateResponse, 'Khong the luu cover image');
        setEditFacilityCoverImageFile(null);
        setEditFacilityCoverImagePreview(null);
      }

      if (descFiles && descFiles.length > 0) {
        const uploadedUrls = [];
        for (const file of descFiles) {
          const imageUrl = await cloudinaryService.uploadFacilityImage(file);
          if (!imageUrl) {
            throw new Error('Không lấy được URL ảnh mô tả từ Cloudinary');
          }
          uploadedUrls.push(imageUrl);
        }

        if (uploadedUrls.length > 0) {
          const dbResponse = await facilityService.addFacilityImages(facilityId, uploadedUrls);
          unwrapApiResponse(dbResponse, 'Khong the luu anh mo ta');
        }
      }

      return true;
    } catch (err) {
      return false;
    } finally {
      setEditFacilityImageUploading(false);
    }
  };

  const calculateRevenue = (bookingsData) => {
    const revenueMap = {};
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    bookingsData.forEach(booking => {
      try {
        const bookingDate = new Date(booking.createdAt || booking.bookingDate);
        const isCurrentMonth = bookingDate.getMonth() === currentMonth && bookingDate.getFullYear() === currentYear;
        const isCurrentYear = bookingDate.getFullYear() === currentYear;

        const amount = parseFloat(booking.totalPrice || booking.price || 0);

        if (isCurrentMonth) {
          revenueMap.currentMonth = (revenueMap.currentMonth || 0) + amount;
        }
        if (isCurrentYear) {
          revenueMap.currentYear = (revenueMap.currentYear || 0) + amount;
        }
      } catch (e) {
        console.log('Error calculating revenue for booking', booking);
      }
    });

    setRevenues([revenueMap]);
  };

  const handleCloseEditFacilityModal = () => {
    setShowEditFacility(false);
    setEditingFacility(null);
    setExistingCoverImageUrl(null);
    setExistingDescImages([]);
    setDescImageToRemove(new Set());
    setEditFacilityCoverImageFile(null);
    setEditFacilityCoverImagePreview(null);
    setEditFacilityDescImages([]);
    setEditFacilityDescImagePreviews([]);
  };

  const handleEditFacility = async (facility) => {
    // Load latest facility detail and images from database
    try {
      const facilityDetailResponse = await facilityService.getFacilityDetail(facility.id);
      const facilityDetail = unwrapApiResponse(facilityDetailResponse, 'Khong the tai chi tiet co so') || facility;
      setEditingFacility({ ...facility, ...facilityDetail });
      setExistingCoverImageUrl(facilityDetail.coverImageUrl || null);

      const response = await facilityService.getFacilityImages(facility.id);
      const images = unwrapApiResponse(response, 'Khong the tai anh') || [];
      setExistingDescImages(images);
    } catch (err) {
      setEditingFacility(facility);
      setExistingCoverImageUrl(facility.coverImageUrl || null);
      console.error('Error loading facility images:', err);
      setExistingDescImages([]);
    }

    // Reset new image uploads
    setEditFacilityCoverImageFile(null);
    setEditFacilityCoverImagePreview(null);
    setEditFacilityDescImages([]);
    setEditFacilityDescImagePreviews([]);
    setDescImageToRemove(new Set());

    setShowEditFacility(true);
  };

  const handleSaveFacility = async () => {
    if (!editingFacility.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập tên sân' });
      return;
    }
    try {
      setLoading(true);
      const facilityData = {
        name: editingFacility.name,
        address: editingFacility.address,
        district: editingFacility.district,
        city: editingFacility.city,
        phone: editingFacility.phone,
        description: editingFacility.description,
        openTime: editingFacility.openTime || '07:00',
        closeTime: editingFacility.closeTime || '23:00',
        commissionRate: editingFacility.commissionRate || 10,
        cancelBeforeHours: editingFacility.cancelBeforeHours || 2,
        autoConfirm: editingFacility.autoConfirm ?? false
      };

      // Add cover image URL if new cover image was uploaded
      if (editFacilityCoverImageFile) {
        try {
          const coverImageUrl = await cloudinaryService.uploadFacilityImage(editFacilityCoverImageFile);
          if (coverImageUrl) {
            facilityData.coverImageUrl = coverImageUrl;
            setEditFacilityCoverImageFile(null);
            setEditFacilityCoverImagePreview(null);
          }
        } catch (err) {
          console.error('Error uploading cover image:', err);
        }
      }

      const response = await facilityService.updateFacility(editingFacility.id, facilityData);
      unwrapApiResponse(response, 'Cap nhat san that bai');

      // Remove marked existing description images
      if (descImageToRemove.size > 0) {
        try {
          for (const imageId of descImageToRemove) {
            await facilityService.deleteFacilityImage(imageId);
          }
        } catch (err) {
          console.error('Error deleting images:', err);
        }
      }

      // Upload description images if any new ones added
      if (editFacilityDescImages.length > 0) {
        try {
          const uploadSuccess = await uploadFacilityImagesForEdit(editingFacility.id, null, editFacilityDescImages);
          if (uploadSuccess) {
            setEditFacilityDescImages([]);
            setEditFacilityDescImagePreviews([]);
          } else {
            console.warn('Some images failed to upload during edit');
          }
        } catch (err) {
          console.error('Error uploading description images:', err);
        }
      }

      setMessage({ type: 'success', text: 'Cập nhật sân thành công!' });
      setShowEditFacility(false);
      setEditingFacility(null);
      setExistingCoverImageUrl(null);
      setExistingDescImages([]);
      setDescImageToRemove(new Set());
      loadOwnerData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi cập nhật sân: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCourt = async () => {
    if (!newCourt.facilityId || !newCourt.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng chọn sân và nhập tên sân chơi' });
      return;
    }
    try {
      setLoading(true);
      const courtData = {
        ...newCourt,
        facilityId: parseInt(newCourt.facilityId)
      };
      const response = await courtService.createCourt(courtData);
      unwrapApiResponse(response, 'Them san choi that bai');
      setMessage({ type: 'success', text: 'Thêm sân chơi mới thành công!' });
      setShowAddCourt(false);
      setNewCourt({ facilityId: '', sportTypeId: 1, name: '', description: '', surfaceType: '', isIndoor: false, isActive: true });
      loadOwnerData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi thêm sân chơi: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEditCourt = (court) => {
    setEditingCourt(court);
    setShowEditCourt(true);
  };

  const handleSaveCourt = async () => {
    if (!editingCourt.name.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập tên sân chơi' });
      return;
    }
    try {
      setLoading(true);
      const courtData = {
        facilityId: editingCourt.facilityId,
        sportTypeId: editingCourt.sportTypeId,
        name: editingCourt.name,
        description: editingCourt.description,
        surfaceType: editingCourt.surfaceType,
        isIndoor: editingCourt.isIndoor,
        isActive: editingCourt.isActive
      };
      const response = await courtService.updateCourt(editingCourt.id, courtData);
      unwrapApiResponse(response, 'Cap nhat san choi that bai');
      setMessage({ type: 'success', text: 'Cập nhật sân chơi thành công!' });
      setShowEditCourt(false);
      setEditingCourt(null);
      loadOwnerData();
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi cập nhật sân chơi: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleViewBooking = (booking) => {
    setSelectedBooking(booking);
    setShowBookingDetail(true);
  };

  const handleConfirmBooking = async (bookingId) => {
    if (window.confirm('Xác nhận đơn đặt này?')) {
      try {
        setLoading(true);
        await bookingService.confirmBooking(bookingId, user?.userId);
        setBookings(prev => prev.map(b =>
          b.id === bookingId ? { ...b, status: 'CONFIRMED' } : b
        ));
        setMessage({ type: 'success', text: 'Xác nhận đơn đặt thành công!' });
      } catch (err) {
        setMessage({ type: 'danger', text: 'Lỗi khi xác nhận đơn đặt: ' + (err.response?.data?.message || err.message) });
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleCancelBooking = (bookingId) => {
    setBookingToCancelId(bookingId);
    setCancelReason('');
    setShowCancelBookingModal(true);
  };

  const handleConfirmCancelBooking = async () => {
    if (!cancelReason.trim()) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập lý do hủy' });
      return;
    }
    try {
      setLoading(true);
      await bookingService.cancelBookingWithReason(bookingToCancelId, cancelReason);
      setBookings(prev => prev.map(b =>
        b.id === bookingToCancelId ? { ...b, status: 'CANCELLED' } : b
      ));
      setMessage({ type: 'success', text: 'Hủy đơn đặt thành công!' });
      setShowCancelBookingModal(false);
      setCancelReason('');
      setBookingToCancelId(null);
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi hủy đơn đặt: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const canCheckInToday = (bookingDate) => {
    if (!bookingDate) {
      return false;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const bookingDay = new Date(bookingDate);
    bookingDay.setHours(0, 0, 0, 0);
    return bookingDay <= today;
  };

  const handleCheckInBooking = async (booking) => {
    if (!booking || booking.status !== 'CONFIRMED') {
      return;
    }

    if (!canCheckInToday(booking.bookingDate)) {
      setMessage({ type: 'warning', text: 'Không thể check-in trước ngày người chơi tới.' });
      return;
    }

    if (!window.confirm(`Xác nhận check-in cho đơn #${booking.id}?`)) {
      return;
    }

    try {
      setLoading(true);
      const response = await checkInService.checkInBooking(booking.id, user?.userId, 'Owner check-in');
      unwrapApiResponse(response, 'Check-in that bai');
      setBookings(prev => prev.map(b => (b.id === booking.id ? { ...b, status: 'CHECKED_IN' } : b)));
      setMessage({ type: 'success', text: 'Check-in thành công!' });
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi check-in: ' + (err.response?.data?.message || err.message) });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteFacility = (facilityId) => {
    setDeleteTarget({ type: 'facility', id: facilityId });
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    try {
      setLoading(true);
      if (deleteTarget.type === 'facility') {
        const response = await facilityService.deleteFacility(deleteTarget.id);
        unwrapApiResponse(response, 'Xoa co so that bai');
        setFacilities(prev => prev.filter(f => f.id !== deleteTarget.id));
        setCourts(prev => prev.filter(c => c.facilityId !== deleteTarget.id));
        setMessage({ type: 'success', text: 'Xóa cơ sở thành công! Tất cả sân chơi liên quan đã được xóa.' });
      } else if (deleteTarget.type === 'court') {
        const response = await courtService.deleteCourt(deleteTarget.id);
        unwrapApiResponse(response, 'Xoa san choi that bai');
        setCourts(prev => prev.filter(c => c.id !== deleteTarget.id));
        setMessage({ type: 'success', text: 'Xóa sân chơi thành công!' });
      }
      setShowDeleteModal(false);
      setDeleteTarget({ type: '', id: null });
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi xóa: ' + (err.response?.data?.message || err.message) });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCourt = (courtId) => {
    setDeleteTarget({ type: 'court', id: courtId });
    setShowDeleteModal(true);
  };

  const loadTimeslotsByCourt = async (courtId) => {
    try {
      setTimeslotLoading(true);
      const response = await timeslotService.getTimeslotsByCourt(courtId);
      const timeslotData = unwrapApiResponse(response, 'Khong the tai khung gio') || [];
      setCourtTimeslots(timeslotData);
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi tải khung giờ: ' + (err.response?.data?.message || err.message) });
      setCourtTimeslots([]);
    } finally {
      setTimeslotLoading(false);
    }
  };

  const handleAddTimeslot = async () => {
    if (!selectedCourtForTimeslot) {
      setMessage({ type: 'warning', text: 'Vui lòng chọn sân chơi để cấu hình khung giờ' });
      return;
    }
    if (!newTimeslot.startTime || !newTimeslot.endTime) {
      setMessage({ type: 'warning', text: 'Vui lòng nhập giờ bắt đầu và giờ kết thúc' });
      return;
    }
    if (!newTimeslot.price || Number(newTimeslot.price) <= 0) {
      setMessage({ type: 'warning', text: 'Giá phải lớn hơn 0' });
      return;
    }
    if (newTimeslot.startTime >= newTimeslot.endTime) {
      setMessage({ type: 'warning', text: 'Giờ kết thúc phải lớn hơn giờ bắt đầu' });
      return;
    }

    try {
      setTimeslotLoading(true);
      const payload = {
        courtId: Number(selectedCourtForTimeslot),
        dayOfWeek: Number(newTimeslot.dayOfWeek) === -1 ? null : Number(newTimeslot.dayOfWeek),
        startTime: newTimeslot.startTime,
        endTime: newTimeslot.endTime,
        price: Number(newTimeslot.price),
        depositRate: Number(newTimeslot.depositRate)
      };

      const response = await timeslotService.createTimeslot(payload);
      unwrapApiResponse(response, 'Them khung gio that bai');
      setMessage({ type: 'success', text: 'Thêm khung giờ thành công!' });
      await loadTimeslotsByCourt(selectedCourtForTimeslot);
      setNewTimeslot(prev => ({ ...prev, price: '' }));
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi thêm khung giờ: ' + (err.response?.data?.message || err.message) });
    } finally {
      setTimeslotLoading(false);
    }
  };

  const handleDeleteTimeslot = async (timeslotId) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa khung giờ này?')) {
      return;
    }
    try {
      setTimeslotLoading(true);
      const response = await timeslotService.deleteTimeslot(timeslotId);
      unwrapApiResponse(response, 'Xoa khung gio that bai');
      setMessage({ type: 'success', text: 'Xóa khung giờ thành công!' });
      await loadTimeslotsByCourt(selectedCourtForTimeslot);
    } catch (err) {
      setMessage({ type: 'danger', text: 'Lỗi khi xóa khung giờ: ' + (err.response?.data?.message || err.message) });
    } finally {
      setTimeslotLoading(false);
    }
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

  const stats = [
    { label: 'Tổng Sân', icon: '', value: facilities.length, color: 'primary' },
    { label: 'Sân Đang Hoạt Động', icon: '', value: facilities.filter(f => f.status === 'APPROVED').length, color: 'success' },
    { label: 'Sân Chưa Duyệt', icon: '', value: facilities.filter(f => f.status === 'PENDING').length, color: 'warning' },
    { label: 'Tổng Đơn Đặt', icon: '', value: bookings.length, color: 'info' },
  ];

  return (
    <Container fluid className="py-4 owner-dashboard-page">
      <Row className="mb-4">
        <Col>
          <h2 className="mb-1">Xin chào, {user?.fullName}</h2>
          <p className="text-muted">Quản lý các sân thể thao của bạn từ đây</p>
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
                <div style={{ fontSize: '2rem' }}>{stat.icon}</div>
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

            {/* Facilities Tab */}
            <Tab eventKey="facilities" title="Các Cơ Sở Của Tôi">
              <Row className="mt-4">
                <Col>
                  <Button
                    variant="success"
                    onClick={() => setShowAddFacility(true)}
                    className="mb-3"
                    disabled={loading}
                  >
                    Thêm Cơ Sở Mới
                  </Button>
                </Col>
              </Row>
              <Table hover responsive>
                <thead>
                  <tr>
                    <th>Tên Cơ Sở</th>
                    <th>Địa chỉ</th>
                    <th>Đánh Giá</th>
                    <th>Trạng Thái</th>
                    <th>Hành Động</th>
                  </tr>
                </thead>
                <tbody>
                  {facilities.map(facility => (
                    <tr key={facility.id}>
                      <td className="fw-bold">{facility.name}</td>
                      <td>{facility.address || 'N/A'}</td>
                      <td>{renderFacilityRating(facility)}</td>
                      <td>
                        <Badge bg={facility.status === 'APPROVED' ? 'success' : 'warning'}>
                          {facility.status === 'APPROVED' ? 'Đã duyệt' : 'Chờ duyệt'}
                        </Badge>
                      </td>
                      <td>
                        <Button
                          size="sm"
                          variant="info"
                          className="me-2"
                          onClick={() => handleEditFacility(facility)}
                          disabled={loading}
                        >
                          Sửa
                        </Button>
                        <Button
                          size="sm"
                          variant="danger"
                          onClick={() => handleDeleteFacility(facility.id)}
                          disabled={loading}
                        >
                          Xóa
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Tab>

            {/* Courts Tab */}
            <Tab eventKey="courts" title="Sân Chơi">
              <Row className="mt-4">
                <Col>
                  <Button
                    variant="success"
                    onClick={() => setShowAddCourt(true)}
                    className="mb-3"
                    disabled={loading}
                  >
                    Thêm Sân Chơi Mới
                  </Button>
                </Col>
              </Row>
              <Table hover responsive className="mt-2">
                <thead>
                  <tr>
                    <th>Sân Chơi</th>
                    <th>Cơ Sở</th>
                    <th>Loại</th>
                    <th>Mặt Sân</th>
                    <th>Ngày Tạo</th>
                    <th>Trạng Thái</th>
                    <th>Hành Động</th>
                  </tr>
                </thead>
                <tbody>
                  {courts.length === 0 ? (
                    <tr>
                      <td colSpan="7" className="text-center text-muted py-4">Chưa có sân chơi nào. <Button size="sm" variant="link" onClick={() => setShowAddCourt(true)}>Thêm sân chơi mới</Button></td>
                    </tr>
                  ) : (
                    courts.map(court => (
                      <tr key={court.id}>
                        <td className="fw-bold">{court.name}</td>
                        <td>{getFacilityName(court.facilityId)}</td>
                        <td><Badge bg="info">{getSportTypeName(court.sportTypeId)}</Badge></td>
                        <td>{court.surfaceType || 'N/A'}</td>
                        <td>{formatDate(court.createdAt)}</td>
                        <td>
                          <Badge bg={court.isActive ? 'success' : 'secondary'}>
                            {court.isActive ? 'Hoạt động' : 'Đóng'}
                          </Badge>
                        </td>
                        <td>
                          <Button
                            size="sm"
                            variant="warning"
                            className="me-2"
                            onClick={() => handleEditCourt(court)}
                            disabled={loading}
                          >
                            Sửa
                          </Button>
                          <Button
                            size="sm"
                            variant="danger"
                            onClick={() => handleDeleteCourt(court.id)}
                            disabled={loading}
                          >
                            Xóa
                          </Button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </Table>
            </Tab>

            {/* Timeslots Tab */}
            <Tab eventKey="timeslots" title="Khung Giờ">
              <Row className="mt-4">
                <Col md={5}>
                  <Form.Group className="mb-3">
                    <Form.Label>Chọn Sân Chơi</Form.Label>
                    <Form.Control
                      as="select"
                      value={selectedCourtForTimeslot}
                      onChange={(e) => setSelectedCourtForTimeslot(e.target.value)}
                      disabled={loading || timeslotLoading}
                    >
                      <option value="">-- Chọn sân chơi --</option>
                      {courts.map(court => (
                        <option key={court.id} value={court.id}>
                          {getCourtName(court.id)}
                        </option>
                      ))}
                    </Form.Control>
                  </Form.Group>
                </Col>
              </Row>

              <Card className="border-0 bg-light mb-3">
                <Card.Body>
                  <h6 className="mb-3">Thêm Khung Giờ Mới</h6>
                  <Row>
                    <Col md={2}>
                      <Form.Group className="mb-2">
                        <Form.Label>Thứ</Form.Label>
                        <Form.Control
                          as="select"
                          value={newTimeslot.dayOfWeek}
                          onChange={(e) => setNewTimeslot({ ...newTimeslot, dayOfWeek: Number(e.target.value) })}
                          disabled={timeslotLoading}
                        >
                          {dayOptions.map(option => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                          ))}
                        </Form.Control>
                      </Form.Group>
                    </Col>
                    <Col md={2}>
                      <Form.Group className="mb-2">
                        <Form.Label>Bắt đầu</Form.Label>
                        <Form.Control
                          type="time"
                          value={newTimeslot.startTime}
                          onChange={(e) => setNewTimeslot({ ...newTimeslot, startTime: e.target.value })}
                          disabled={timeslotLoading}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={2}>
                      <Form.Group className="mb-2">
                        <Form.Label>Kết thúc</Form.Label>
                        <Form.Control
                          type="time"
                          value={newTimeslot.endTime}
                          onChange={(e) => setNewTimeslot({ ...newTimeslot, endTime: e.target.value })}
                          disabled={timeslotLoading}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={3}>
                      <Form.Group className="mb-2">
                        <Form.Label>Giá (VND)</Form.Label>
                        <Form.Control
                          type="number"
                          min="1000"
                          value={newTimeslot.price}
                          onChange={(e) => setNewTimeslot({ ...newTimeslot, price: e.target.value })}
                          disabled={timeslotLoading}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={2}>
                      <Form.Group className="mb-2">
                        <Form.Label>Tỉ lệ cọc</Form.Label>
                        <Form.Control
                          type="number"
                          min="0"
                          max="1"
                          step="0.01"
                          value={newTimeslot.depositRate}
                          onChange={(e) => setNewTimeslot({ ...newTimeslot, depositRate: e.target.value })}
                          disabled={timeslotLoading}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={1} className="d-flex align-items-end">
                      <Button
                        variant="success"
                        className="w-100"
                        onClick={handleAddTimeslot}
                        disabled={timeslotLoading || loading}
                      >
                        Thêm
                      </Button>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>

              <Table hover responsive>
                <thead>
                  <tr>
                    <th>Thứ</th>
                    <th>Bắt đầu</th>
                    <th>Kết thúc</th>
                    <th>Giá</th>
                    <th>Tỉ lệ cọc</th>
                    <th>Hành Động</th>
                  </tr>
                </thead>
                <tbody>
                  {!selectedCourtForTimeslot ? (
                    <tr>
                      <td colSpan="6" className="text-center text-muted py-4">Vui lòng chọn sân chơi để xem khung giờ</td>
                    </tr>
                  ) : timeslotLoading ? (
                    <tr>
                      <td colSpan="6" className="text-center text-muted py-4">Đang tải dữ liệu...</td>
                    </tr>
                  ) : courtTimeslots.length === 0 ? (
                    <tr>
                      <td colSpan="6" className="text-center text-muted py-4">Chưa có khung giờ nào</td>
                    </tr>
                  ) : (
                    courtTimeslots.map(timeslot => (
                      <tr key={timeslot.id}>
                        <td>{getDayLabel(timeslot.dayOfWeek)}</td>
                        <td>{formatTimeDisplay(timeslot.startTime)}</td>
                        <td>{formatTimeDisplay(timeslot.endTime)}</td>
                        <td className="fw-bold">₫{Number(timeslot.price || 0).toLocaleString()}</td>
                        <td>{Number(timeslot.depositRate || 0).toFixed(2)}</td>
                        <td>
                          <Button
                            size="sm"
                            variant="danger"
                            onClick={() => handleDeleteTimeslot(timeslot.id)}
                            disabled={timeslotLoading || loading}
                          >
                            🗑️ Xóa
                          </Button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </Table>
            </Tab>

            {/* Bookings Tab */}
            <Tab eventKey="bookings" title="Đơn Đặt">
              <Table hover responsive className="mt-4">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Ngày Đặt</th>
                    <th>Khách Hàng</th>
                    <th>Sân Chơi</th>
                    <th>Trạng Thái</th>
                    <th>Giá</th>
                    <th>Hành Động</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.length === 0 ? (
                    <tr>
                      <td colSpan="7" className="text-center text-muted py-4">Chưa có đơn đặt nào</td>
                    </tr>
                  ) : (
                    bookings.map(booking => (
                      <tr key={booking.id}>
                        <td className="fw-bold">#{booking.id}</td>
                        <td>{formatDate(booking.bookingDate || booking.createdAt)}</td>
                        <td>{booking.customerName || booking.customer || 'N/A'}</td>
                        <td>{booking.courtName || booking.court || 'N/A'}</td>
                        <td>
                          <Badge bg={
                            booking.status === 'PENDING_PAYMENT' ? 'secondary' :
                              booking.status === 'PENDING_CONFIRM' ? 'warning' :
                                booking.status === 'CONFIRMED' ? 'success' :
                                  booking.status === 'CHECKED_IN' ? 'info' :
                                    booking.status === 'CANCELLED' ? 'danger' :
                                      'secondary'
                          }>
                            {booking.status === 'PENDING_PAYMENT' ? 'Chờ thanh toán cọc' :
                              booking.status === 'PENDING_CONFIRM' ? 'Chờ xác nhận' :
                                booking.status === 'CONFIRMED' ? 'Đã xác nhận' :
                                  booking.status === 'CHECKED_IN' ? 'Đã check-in' :
                                    booking.status === 'CANCELLED' ? 'Đã hủy' :
                                      booking.status}
                          </Badge>
                        </td>
                        <td className="fw-bold">₫{(booking.totalPrice || booking.price || 0).toLocaleString()}</td>
                        <td>
                          <Button
                            size="sm"
                            variant="info"
                            className="me-2"
                            onClick={() => handleViewBooking(booking)}
                            disabled={loading}
                          >
                            Xem
                          </Button>
                          {booking.status === 'PENDING_CONFIRM' && (
                            <>
                              <Button
                                size="sm"
                                variant="success"
                                className="me-2"
                                onClick={() => handleConfirmBooking(booking.id)}
                                disabled={loading}
                              >
                                Xác nhận
                              </Button>
                              <Button
                                size="sm"
                                variant="danger"
                                onClick={() => handleCancelBooking(booking.id)}
                                disabled={loading}
                              >
                                Hủy
                              </Button>
                            </>
                          )}
                          {booking.status === 'CONFIRMED' && (
                            <>
                              <Button
                                size="sm"
                                variant="primary"
                                className="me-2"
                                onClick={() => handleCheckInBooking(booking)}
                                disabled={loading}
                                title="Check-in"
                              >
                                Check-in
                              </Button>
                            </>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </Table>
            </Tab>

            {/* Revenue Tab */}
            <Tab eventKey="revenue" title="Doanh Thu">
              <Row className="mt-4">
                <Col md={6}>
                  <Card className="text-center border-0 shadow-sm">
                    <Card.Body>
                      <h6 className="text-muted">Tháng Này</h6>
                      <h3 className="text-success">₫{(revenues[0]?.currentMonth || 0).toLocaleString()}</h3>
                      <p className="text-muted mb-0">Từ các đơn đặt đã xác nhận</p>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6}>
                  <Card className="text-center border-0 shadow-sm">
                    <Card.Body>
                      <h6 className="text-muted">Tổng Năm 2026</h6>
                      <h3 className="text-primary">₫{(revenues[0]?.currentYear || 0).toLocaleString()}</h3>
                      <p className="text-muted mb-0">Từ đầu năm</p>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
              <Row className="mt-4">
                <Col>
                  <Card className="border-0 shadow-sm">
                    <Card.Body>
                      <h6 className="mb-3">Thống Kê Chi Tiết</h6>
                      <div className="row">
                        <div className="col-md-3">
                          <p className="text-muted mb-1">Tổng Đơn Đặt</p>
                          <h4>{bookings.length}</h4>
                        </div>
                        <div className="col-md-3">
                          <p className="text-muted mb-1">Đã Xác Nhận</p>
                          <h4 className="text-success">{bookings.filter(b => b.status === 'CONFIRMED').length}</h4>
                        </div>
                        <div className="col-md-3">
                          <p className="text-muted mb-1">Chờ Xác Nhận</p>
                          <h4 className="text-warning">{bookings.filter(b => b.status === 'PENDING_CONFIRM').length}</h4>
                        </div>
                        <div className="col-md-3">
                          <p className="text-muted mb-1">Đã Hủy</p>
                          <h4 className="text-danger">{bookings.filter(b => b.status === 'CANCELLED').length}</h4>
                        </div>
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
            </Tab>

          </Tabs>
        </Card.Body>
      </Card>

      {/* Edit Facility Modal */}
      <Modal show={showEditFacility} onHide={handleCloseEditFacilityModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Sân</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {editingFacility && (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Tên Sân</Form.Label>
                <Form.Control
                  placeholder="Nhập tên sân"
                  value={editingFacility.name}
                  onChange={(e) => setEditingFacility({ ...editingFacility, name: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Địa Chỉ</Form.Label>
                <Form.Control
                  placeholder="Nhập địa chỉ"
                  value={editingFacility.address || ''}
                  onChange={(e) => setEditingFacility({ ...editingFacility, address: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Số Điện Thoại</Form.Label>
                <Form.Control
                  placeholder="Nhập số điện thoại"
                  value={editingFacility.phone || ''}
                  onChange={(e) => setEditingFacility({ ...editingFacility, phone: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Mô Tả</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  placeholder="Nhập mô tả sân"
                  value={editingFacility.description || ''}
                  onChange={(e) => setEditingFacility({ ...editingFacility, description: e.target.value })}
                />
              </Form.Group>

              {/* Existing Cover Image */}
              {existingCoverImageUrl && (
                <Form.Group className="mb-3">
                  <Form.Label className="text-info">ℹ️ Ảnh Bìa Hiện Tại</Form.Label>
                  <div style={{ position: 'relative', display: 'inline-block' }}>
                    <img
                      src={existingCoverImageUrl}
                      alt="Current cover"
                      style={{ maxWidth: '200px', maxHeight: '150px', borderRadius: '5px', border: '2px solid #0d6efd' }}
                    />
                    <small className="d-block mt-2">Nhấn chọn file phía dưới để thay đổi ảnh bìa</small>
                  </div>
                </Form.Group>
              )}

              {/* New Cover Image Upload */}
              <Form.Group className="mb-3">
                <Form.Label>Ảnh Bìa Cơ Sở (upload mới để thay đổi)</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  onChange={handleEditFacilityCoverImageChange}
                  disabled={editFacilityImageUploading}
                />
                {editFacilityCoverImagePreview && (
                  <div className="mt-2">
                    <small className="text-success">✓ Ảnh mới được chọn</small>
                    <img
                      src={editFacilityCoverImagePreview}
                      alt="New cover preview"
                      style={{ maxWidth: '200px', maxHeight: '150px', borderRadius: '5px', border: '2px solid #28a745', marginTop: '5px' }}
                    />
                  </div>
                )}
              </Form.Group>

              {/* Existing Description Images */}
              {existingDescImages.length > 0 && (
                <Form.Group className="mb-3">
                  <Form.Label className="text-info">ℹ️ Ảnh Mô Tả Hiện Tại ({existingDescImages.length} ảnh)</Form.Label>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))', gap: '10px' }}>
                    {existingDescImages.map((image) => (
                      <div key={image.id} style={{ position: 'relative' }}>
                        <img
                          src={image.imageUrl}
                          alt={`Existing ${image.id}`}
                          style={{
                            width: '100%',
                            height: '100px',
                            objectFit: 'cover',
                            borderRadius: '5px',
                            border: descImageToRemove.has(image.id) ? '2px solid #dc3545' : '2px solid #0d6efd',
                            opacity: descImageToRemove.has(image.id) ? 0.6 : 1
                          }}
                        />
                        <Button
                          size="sm"
                          variant={descImageToRemove.has(image.id) ? 'danger' : 'warning'}
                          style={{ position: 'absolute', top: '2px', right: '2px', padding: '2px 6px' }}
                          onClick={() => handleMarkExistingDescImageForRemoval(image.id)}
                          title={descImageToRemove.has(image.id) ? 'Hủy xóa' : 'Xóa ảnh này'}
                        >
                          {descImageToRemove.has(image.id) ? '↩️' : '🗑️'}
                        </Button>
                      </div>
                    ))}
                  </div>
                  {descImageToRemove.size > 0 && (
                    <small className="text-danger d-block mt-2">⚠️ {descImageToRemove.size} ảnh sẽ bị xóa</small>
                  )}
                </Form.Group>
              )}

              {/* New Description Images Upload */}
              <Form.Group className="mb-3">
                <Form.Label>Ảnh Mô Tả Cơ Sở (upload mới để thêm, tối đa 5 ảnh)</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handleEditFacilityDescImagesChange}
                  disabled={editFacilityImageUploading}
                />
                {editFacilityDescImagePreviews.length > 0 && (
                  <div className="mt-2">
                    <small className="text-success">✓ {editFacilityDescImagePreviews.length} ảnh mới được chọn</small>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))', gap: '10px', marginTop: '10px' }}>
                      {editFacilityDescImagePreviews.map((preview, idx) => (
                        <div key={idx} style={{ position: 'relative' }}>
                          <img
                            src={preview}
                            alt={`New desc ${idx}`}
                            style={{ width: '100%', height: '100px', objectFit: 'cover', borderRadius: '5px', border: '2px solid #28a745' }}
                          />
                          <Button
                            size="sm"
                            variant="danger"
                            style={{ position: 'absolute', top: '2px', right: '2px', padding: '2px 6px' }}
                            onClick={() => handleRemoveEditFacilityDescImage(idx)}
                          >
                            ✕
                          </Button>
                        </div>
                      ))}
                    </div>
                    <small className="text-muted d-block mt-2">Tổng ảnh mô tả: {existingDescImages.length - descImageToRemove.size + editFacilityDescImagePreviews.length}</small>
                  </div>
                )}
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseEditFacilityModal}>
            Hủy
          </Button>
          <Button
            variant="primary"
            onClick={handleSaveFacility}
            disabled={loading || editFacilityImageUploading}
          >
            {editFacilityImageUploading ? 'Đang tải ảnh...' : 'Lưu Thay Đổi'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Edit Court Modal */}
      <Modal show={showEditCourt} onHide={() => setShowEditCourt(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Sân Chơi</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {editingCourt && (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Chọn Sân</Form.Label>
                <Form.Control
                  as="select"
                  value={editingCourt.facilityId || ''}
                  onChange={(e) => setEditingCourt({ ...editingCourt, facilityId: e.target.value ? parseInt(e.target.value, 10) : '' })}
                >
                  <option value="">-- Chọn sân --</option>
                  {facilities.map(fac => (
                    <option key={fac.id} value={fac.id}>{fac.name}</option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Tên Sân Chơi</Form.Label>
                <Form.Control
                  placeholder="Nhập tên sân chơi"
                  value={editingCourt.name}
                  onChange={(e) => setEditingCourt({ ...editingCourt, name: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Loại Thể Thao</Form.Label>
                <Form.Control
                  as="select"
                  value={editingCourt.sportTypeId || ''}
                  onChange={(e) => setEditingCourt({ ...editingCourt, sportTypeId: e.target.value ? parseInt(e.target.value, 10) : '' })}
                >
                  <option value="">-- Chọn loại thể thao --</option>
                  {sportTypes.map(type => (
                    <option key={type.id} value={type.id}>{type.name}</option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Mặt Sân</Form.Label>
                <Form.Control
                  placeholder="Ví dụ: Cỏ tự nhiên, Cỏ nhân tạo, Cao su..."
                  value={editingCourt.surfaceType || ''}
                  onChange={(e) => setEditingCourt({ ...editingCourt, surfaceType: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Check
                  type="checkbox"
                  label="Sân trong nhà"
                  checked={editingCourt.isIndoor || false}
                  onChange={(e) => setEditingCourt({ ...editingCourt, isIndoor: e.target.checked })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Check
                  type="checkbox"
                  label="Đang hoạt động"
                  checked={editingCourt.isActive ?? true}
                  onChange={(e) => setEditingCourt({ ...editingCourt, isActive: e.target.checked })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Mô Tả</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  placeholder="Nhập mô tả sân chơi"
                  value={editingCourt.description || ''}
                  onChange={(e) => setEditingCourt({ ...editingCourt, description: e.target.value })}
                />
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowEditCourt(false)}>
            Hủy
          </Button>
          <Button
            variant="primary"
            onClick={handleSaveCourt}
            disabled={loading}
          >
            Lưu Thay Đổi
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Add Facility Modal */}
      <Modal show={showAddFacility} onHide={() => setShowAddFacility(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Thêm Cơ Sở Mới</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Tên Cơ Sở</Form.Label>
              <Form.Control
                placeholder="Nhập tên cơ sở"
                value={newFacility.name}
                onChange={(e) => setNewFacility({ ...newFacility, name: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Địa Chỉ Chi Tiết</Form.Label>
              <Form.Control
                placeholder="Ví dụ: 123 Đường A"
                value={newFacility.address}
                onChange={(e) => setNewFacility({ ...newFacility, address: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Quận/Huyện</Form.Label>
              <Form.Control
                placeholder="Ví dụ: Quận 1"
                value={newFacility.district}
                onChange={(e) => setNewFacility({ ...newFacility, district: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Tỉnh/Thành Phố</Form.Label>
              <Form.Control
                placeholder="Ví dụ: TP. Hồ Chí Minh"
                value={newFacility.city}
                onChange={(e) => setNewFacility({ ...newFacility, city: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Số Điện Thoại</Form.Label>
              <Form.Control
                placeholder="Nhập số điện thoại"
                value={newFacility.phone}
                onChange={(e) => setNewFacility({ ...newFacility, phone: e.target.value })}
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Giờ Mở Cửa</Form.Label>
                  <Form.Control
                    type="time"
                    value={newFacility.openTime}
                    onChange={(e) => setNewFacility({ ...newFacility, openTime: e.target.value })}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Giờ Đóng Cửa</Form.Label>
                  <Form.Control
                    type="time"
                    value={newFacility.closeTime}
                    onChange={(e) => setNewFacility({ ...newFacility, closeTime: e.target.value })}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Tỷ Lệ Hoa Hồng (%)</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.1"
                    min="0"
                    value={newFacility.commissionRate}
                    onChange={(e) => setNewFacility({ ...newFacility, commissionRate: e.target.value })}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Hủy Trước (giờ)</Form.Label>
                  <Form.Control
                    type="number"
                    min="1"
                    value={newFacility.cancelBeforeHours}
                    onChange={(e) => setNewFacility({ ...newFacility, cancelBeforeHours: parseInt(e.target.value) })}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Mô Tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="Nhập mô tả cơ sở"
                value={newFacility.description}
                onChange={(e) => setNewFacility({ ...newFacility, description: e.target.value })}
              />
            </Form.Group>

            {/* Cover Image Upload */}
            <Form.Group className="mb-3">
              <Form.Label>Ảnh Bìa Cơ Sở</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={handleAddFacilityCoverImageChange}
                disabled={addFacilityImageUploading}
              />
              {addFacilityCoverImagePreview && (
                <div className="mt-2">
                  <img
                    src={addFacilityCoverImagePreview}
                    alt="Cover preview"
                    style={{ maxWidth: '200px', maxHeight: '150px', borderRadius: '5px' }}
                  />
                </div>
              )}
            </Form.Group>

            {/* Description Images Upload */}
            <Form.Group className="mb-3">
              <Form.Label>Ảnh Mô Tả Cơ Sở (Tối đa 5 ảnh)</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                multiple
                onChange={handleAddFacilityDescImagesChange}
                disabled={addFacilityImageUploading}
              />
              {addFacilityDescImagePreviews.length > 0 && (
                <div className="mt-2">
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))', gap: '10px' }}>
                    {addFacilityDescImagePreviews.map((preview, idx) => (
                      <div key={idx} style={{ position: 'relative' }}>
                        <img
                          src={preview}
                          alt={`Desc ${idx}`}
                          style={{ width: '100%', height: '100px', objectFit: 'cover', borderRadius: '5px' }}
                        />
                        <Button
                          size="sm"
                          variant="danger"
                          style={{ position: 'absolute', top: '2px', right: '2px', padding: '2px 6px' }}
                          onClick={() => handleRemoveAddFacilityDescImage(idx)}
                        >
                          ✕
                        </Button>
                      </div>
                    ))}
                  </div>
                  <small className="text-muted d-block mt-2">Số ảnh: {addFacilityDescImagePreviews.length}/5</small>
                </div>
              )}
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddFacility(false)} disabled={addFacilityImageUploading}>
            Hủy
          </Button>
          <Button
            variant="primary"
            onClick={handleAddFacility}
            disabled={loading || addFacilityImageUploading}
          >
            {addFacilityImageUploading ? 'Đang tải ảnh...' : 'Thêm Cơ Sở'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Add Court Modal */}
      <Modal show={showAddCourt} onHide={() => setShowAddCourt(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Thêm Sân Chơi Mới</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Chọn Sân</Form.Label>
              <Form.Control
                as="select"
                value={newCourt.facilityId}
                onChange={(e) => setNewCourt({ ...newCourt, facilityId: e.target.value })}
              >
                <option value="">-- Chọn sân --</option>
                {facilities.map(fac => (
                  <option key={fac.id} value={fac.id}>{fac.name}</option>
                ))}
              </Form.Control>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Tên Sân Chơi</Form.Label>
              <Form.Control
                placeholder="Ví dụ: Sân 1, Sân 2..."
                value={newCourt.name}
                onChange={(e) => setNewCourt({ ...newCourt, name: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Loại Thể Thao</Form.Label>
              <Form.Control
                as="select"
                value={newCourt.sportTypeId}
                onChange={(e) => setNewCourt({ ...newCourt, sportTypeId: parseInt(e.target.value) })}
              >
                <option value="">-- Chọn loại thể thao --</option>
                {sportTypes.map(type => (
                  <option key={type.id} value={type.id}>{type.name}</option>
                ))}
              </Form.Control>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mặt Sân</Form.Label>
              <Form.Control
                placeholder="Ví dụ: Cỏ tự nhiên, Cỏ nhân tạo, Cao su..."
                value={newCourt.surfaceType}
                onChange={(e) => setNewCourt({ ...newCourt, surfaceType: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Sân trong nhà"
                checked={newCourt.isIndoor}
                onChange={(e) => setNewCourt({ ...newCourt, isIndoor: e.target.checked })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Đang hoạt động"
                checked={newCourt.isActive}
                onChange={(e) => setNewCourt({ ...newCourt, isActive: e.target.checked })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô Tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                placeholder="Nhập mô tả sân chơi"
                value={newCourt.description}
                onChange={(e) => setNewCourt({ ...newCourt, description: e.target.value })}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddCourt(false)}>
            Hủy
          </Button>
          <Button
            variant="primary"
            onClick={handleAddCourt}
            disabled={loading}
          >
            Thêm Sân Chơi
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Booking Detail Modal */}
      <Modal show={showBookingDetail} onHide={() => setShowBookingDetail(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Chi Tiết Đơn Đặt</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedBooking && (
            <div>
              <Row className="mb-3">
                <Col md={6}>
                  <p><strong>Mã Đơn:</strong> #{selectedBooking.id}</p>
                  <p><strong>Khách Hàng:</strong> {selectedBooking.customerName || selectedBooking.customer || 'N/A'}</p>
                  <p><strong>Sân Chơi:</strong> {selectedBooking.courtName || selectedBooking.court || 'N/A'}</p>
                </Col>
                <Col md={6}>
                  <p><strong>Ngày Đặt:</strong> {formatDate(selectedBooking.bookingDate || selectedBooking.createdAt)}</p>
                  <p><strong>Trạng Thái:</strong> <Badge bg={
                    selectedBooking.status === 'PENDING_PAYMENT' ? 'secondary' :
                      selectedBooking.status === 'PENDING_CONFIRM' ? 'warning' :
                        selectedBooking.status === 'CONFIRMED' ? 'success' :
                          selectedBooking.status === 'CHECKED_IN' ? 'info' :
                            selectedBooking.status === 'CANCELLED' ? 'danger' :
                              'secondary'
                  }>{selectedBooking.status}</Badge></p>
                  <p><strong>Giá:</strong> ₫{(selectedBooking.totalPrice || selectedBooking.price || 0).toLocaleString()}</p>
                </Col>
              </Row>
              {selectedBooking.description && (
                <p><strong>Ghi Chú:</strong> {selectedBooking.description}</p>
              )}
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          {selectedBooking && selectedBooking.status === 'PENDING_CONFIRM' && (
            <>
              <Button variant="success" onClick={() => { handleConfirmBooking(selectedBooking.id); setShowBookingDetail(false); }}>
                Xác nhận
              </Button>
              <Button variant="danger" onClick={() => { handleCancelBooking(selectedBooking.id); setShowBookingDetail(false); }}>
                Hủy
              </Button>
            </>
          )}
          {selectedBooking && selectedBooking.status === 'CONFIRMED' && (
            <Button
              variant="primary"
              onClick={() => { handleCheckInBooking(selectedBooking); setShowBookingDetail(false); }}
              disabled={loading}
              title="Check-in"
            >
              Check-in
            </Button>
          )}
          <Button variant="secondary" onClick={() => setShowBookingDetail(false)}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác Nhận Xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Bạn có chắc chắn muốn xóa {deleteTarget.type === 'facility' ? 'cơ sở' : 'sân chơi'} này không?</p>
          {deleteTarget.type === 'facility' && (
            <p className="mb-1 text-warning">Khi xóa cơ sở, toàn bộ sân chơi thuộc cơ sở cũng sẽ bị xóa.</p>
          )}
          <p className="text-danger">Hành động này không thể hoàn tác.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Hủy
          </Button>
          <Button
            variant="danger"
            onClick={confirmDelete}
            disabled={loading}
          >
            Xóa
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Cancel Booking Modal */}
      <Modal show={showCancelBookingModal} onHide={() => setShowCancelBookingModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Hủy Đơn Đặt</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group>
            <Form.Label>Lý do hủy đơn đặt <span className="text-danger">*</span></Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              placeholder="Nhập lý do hủy đơn đặt (bắt buộc)"
              value={cancelReason}
              onChange={(e) => setCancelReason(e.target.value)}
              maxLength={500}
              disabled={loading}
            />
            <small className="text-muted">{cancelReason.length}/500 ký tự</small>
          </Form.Group>
          <Alert variant="info" className="mt-3 mb-0">
            💡 <strong>Lưu ý:</strong> Khách hàng sẽ được hoàn lại tiền cọc và sẽ thấy lý do hủy của bạn.
          </Alert>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => {
              setShowCancelBookingModal(false);
              setCancelReason('');
              setBookingToCancelId(null);
            }}
            disabled={loading}
          >
            Đóng
          </Button>
          <Button
            variant="danger"
            onClick={handleConfirmCancelBooking}
            disabled={loading || !cancelReason.trim()}
          >
            {loading ? 'Đang xử lý...' : 'Xác Nhận Hủy'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default OwnerDashboard;

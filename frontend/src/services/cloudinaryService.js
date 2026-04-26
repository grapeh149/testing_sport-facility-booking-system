import apiClient from './api';

const cloudinaryService = {
  // Upload ảnh lên Cloudinary thông qua backend
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  // Upload nhiều ảnh
  uploadImages: (files) => {
    const formData = new FormData();
    files.forEach((file, index) => {
      formData.append(`files`, file);
    });
    return apiClient.post('/api/upload/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  // Xóa ảnh từ Cloudinary thông qua backend
  deleteImage: (imageUrl) => {
    return apiClient.post('/api/upload/delete', { imageUrl });
  },
};

export default cloudinaryService;

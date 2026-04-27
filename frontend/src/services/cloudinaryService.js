import api from './api';

export const cloudinaryService = {
  uploadFacilityImage: async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await api.post('/api/upload', formData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to upload facility image');
    }
  },
};

export default cloudinaryService;

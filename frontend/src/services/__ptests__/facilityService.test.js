import facilityService from '../facilityService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    patch: jest.fn(),
  },
}));

describe('facilityService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('calls facility listing and image endpoints', () => {
    facilityService.getFacilities(2, 6, 'abc');
    facilityService.getFacilityDetail(5);
    facilityService.getFacilityImages(7);
    facilityService.updateFacilityCoverImage(8, 'https://image.test');

    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities', { params: { page: 2, size: 6, search: 'abc' } });
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities/5');
    expect(apiClient.get).toHaveBeenCalledWith('/api/facility-images/facility/7');
    expect(apiClient.patch).toHaveBeenCalledWith('/api/facilities/8/cover-image', { coverImageUrl: 'https://image.test' });
  });

  test('calls getFacilities with default pagination', () => {
    facilityService.getFacilities();
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities', { params: { page: 0, size: 10, search: '' } });
  });

  test('calls getFacilities with search parameter', () => {
    facilityService.getFacilities(0, 10, 'volleyball');
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities', { params: { page: 0, size: 10, search: 'volleyball' } });
  });

  test('calls getFacilityDetail with different ids', () => {
    const facilityIds = [5, 10, 25, 100];

    facilityIds.forEach(id => {
      facilityService.getFacilityDetail(id);
    });

    facilityIds.forEach((id, idx) => {
      expect(apiClient.get).toHaveBeenNthCalledWith(idx + 1, `/api/facilities/${id}`);
    });
  });

  test('calls getOwnerFacilities endpoint', () => {
    facilityService.getOwnerFacilities(12);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facilities/owner/12');
  });

  test('calls getOwnerFacilities with multiple ownerIds', () => {
    const ownerIds = [1, 5, 10, 20];

    ownerIds.forEach(ownerId => {
      facilityService.getOwnerFacilities(ownerId);
    });

    ownerIds.forEach((ownerId, idx) => {
      expect(apiClient.get).toHaveBeenNthCalledWith(idx + 1, `/api/facilities/owner/${ownerId}`);
    });
  });

  test('calls createFacility endpoint with data and ownerId', () => {
    const facilityData = {
      name: 'New Facility',
      address: '123 Main St',
      phone: '0123456789'
    };
    
    facilityService.createFacility(facilityData, 5);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/facilities', facilityData, { params: { ownerId: 5 } });
  });

  test('calls updateFacility endpoint', () => {
    const updateData = { name: 'Updated Facility', phone: '9876543210' };
    
    facilityService.updateFacility(8, updateData);
    
    expect(apiClient.put).toHaveBeenCalledWith('/api/facilities/8', updateData);
  });

  test('calls deleteFacility endpoint', () => {
    facilityService.deleteFacility(15);
    
    expect(apiClient.delete).toHaveBeenCalledWith('/api/facilities/15');
  });

  test('calls updateFacilityCoverImage with different urls', () => {
    const imageUrls = [
      'https://cloudinary.com/image1.jpg',
      'https://cloudinary.com/image2.jpg',
      'https://cdn.example.com/facility.png'
    ];

    imageUrls.forEach((url, idx) => {
      facilityService.updateFacilityCoverImage(10 + idx, url);
    });

    imageUrls.forEach((url, idx) => {
      expect(apiClient.patch).toHaveBeenNthCalledWith(
        idx + 1,
        `/api/facilities/${10 + idx}/cover-image`,
        { coverImageUrl: url }
      );
    });
  });

  test('calls getFacilityImages endpoint', () => {
    facilityService.getFacilityImages(20);
    
    expect(apiClient.get).toHaveBeenCalledWith('/api/facility-images/facility/20');
  });

  test('calls addFacilityImage endpoint', () => {
    facilityService.addFacilityImage(12, 'https://image.jpg', 1);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/facility-images/facility/12', {
      imageUrl: 'https://image.jpg',
      sortOrder: 1
    });
  });

  test('calls addFacilityImages endpoint for bulk upload', () => {
    const imageUrls = [
      'https://image1.jpg',
      'https://image2.jpg',
      'https://image3.jpg'
    ];
    
    facilityService.addFacilityImages(18, imageUrls);
    
    expect(apiClient.post).toHaveBeenCalledWith('/api/facility-images/facility/18/bulk', {
      imageUrls
    });
  });

  test('calls deleteFacilityImage endpoint', () => {
    facilityService.deleteFacilityImage(42);
    
    expect(apiClient.delete).toHaveBeenCalledWith('/api/facility-images/42');
  });

  test('calls deleteAllFacilityImages endpoint', () => {
    facilityService.deleteAllFacilityImages(25);
    
    expect(apiClient.delete).toHaveBeenCalledWith('/api/facility-images/facility/25/all');
  });

  test('calls getFacilities with various pagination parameters', () => {
    const paginationCases = [
      { page: 0, size: 5 },
      { page: 1, size: 10 },
      { page: 5, size: 20 }
    ];

    paginationCases.forEach(({ page, size }) => {
      facilityService.getFacilities(page, size);
    });

    paginationCases.forEach(({ page, size }, idx) => {
      expect(apiClient.get).toHaveBeenNthCalledWith(
        idx + 1,
        '/api/facilities',
        { params: { page, size, search: '' } }
      );
    });
  });

  test('creates facility with complex data structure', () => {
    const complexFacilityData = {
      name: 'Premium Sports Complex',
      address: '456 Park Ave',
      district: 'District 1',
      city: 'Ho Chi Minh',
      phone: '0987654321',
      description: 'Premium facility',
      openTime: '06:00',
      closeTime: '22:00',
      commissionRate: 15,
      cancelBeforeHours: 3,
      autoConfirm: true
    };

    facilityService.createFacility(complexFacilityData, 3);

    expect(apiClient.post).toHaveBeenCalledWith('/api/facilities', complexFacilityData, { params: { ownerId: 3 } });
  });

  test('updates facility with multiple fields', () => {
    const updateData = {
      name: 'Updated Complex',
      phone: '0111111111',
      description: 'Updated description',
      commissionRate: 12,
      openTime: '07:00',
      closeTime: '23:00'
    };

    facilityService.updateFacility(30, updateData);

    expect(apiClient.put).toHaveBeenCalledWith('/api/facilities/30', updateData);
  });

  test('adds multiple images with proper sorting', () => {
    const images = [
      { imageUrl: 'https://image1.jpg', sortOrder: 1 },
      { imageUrl: 'https://image2.jpg', sortOrder: 2 },
      { imageUrl: 'https://image3.jpg', sortOrder: 3 }
    ];

    images.forEach(img => {
      facilityService.addFacilityImage(5, img.imageUrl, img.sortOrder);
    });

    images.forEach((img, idx) => {
      expect(apiClient.post).toHaveBeenNthCalledWith(
        idx + 1,
        '/api/facility-images/facility/5',
        { imageUrl: img.imageUrl, sortOrder: img.sortOrder }
      );
    });
  });

  test('deletes multiple facility images', () => {
    const imageIds = [101, 102, 103, 104, 105];

    imageIds.forEach(id => {
      facilityService.deleteFacilityImage(id);
    });

    imageIds.forEach((id, idx) => {
      expect(apiClient.delete).toHaveBeenNthCalledWith(idx + 1, `/api/facility-images/${id}`);
    });
  });

  test('bulk upload with multiple image urls', () => {
    const bulkImageUrls = [
      'https://cdn.com/facility1-1.jpg',
      'https://cdn.com/facility1-2.jpg',
      'https://cdn.com/facility1-3.jpg',
      'https://cdn.com/facility1-4.jpg',
      'https://cdn.com/facility1-5.jpg'
    ];

    facilityService.addFacilityImages(7, bulkImageUrls);

    expect(apiClient.post).toHaveBeenCalledWith('/api/facility-images/facility/7/bulk', {
      imageUrls: bulkImageUrls
    });
  });
});
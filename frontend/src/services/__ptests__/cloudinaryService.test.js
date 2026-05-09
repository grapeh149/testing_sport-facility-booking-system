import cloudinaryService from '../cloudinaryService';
import apiClient from '../api';

jest.mock('../api', () => ({
  __esModule: true,
  default: {
    post: jest.fn(),
  },
}));

describe('cloudinaryService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('uploads file and returns response data', async () => {
    apiClient.post.mockResolvedValue({ data: 'https://cdn.test/image.jpg' });

    const file = new File(['file'], 'avatar.png', { type: 'image/png' });
    await expect(cloudinaryService.uploadFacilityImage(file)).resolves.toBe('https://cdn.test/image.jpg');

    expect(apiClient.post).toHaveBeenCalledWith('/api/upload', expect.any(FormData));
  });

  test('throws friendly error when upload fails', async () => {
    apiClient.post.mockRejectedValue({ response: { data: { message: 'Upload failed' } } });

    const file = new File(['file'], 'avatar.png', { type: 'image/png' });
    await expect(cloudinaryService.uploadFacilityImage(file)).rejects.toThrow('Upload failed');
  });
});
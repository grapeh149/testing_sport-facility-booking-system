describe('apiClient', () => {
  const loadApiModule = () => {
    jest.resetModules();

    const requestUse = jest.fn();
    const responseUse = jest.fn();
    const mockApiClient = {
      interceptors: {
        request: { use: requestUse },
        response: { use: responseUse },
      },
    };

    jest.doMock('axios', () => ({
      __esModule: true,
      default: {
        create: jest.fn(() => mockApiClient),
      },
    }));

    require('../api');
    return { requestUse, responseUse };
  };

  beforeEach(() => {
    localStorage.clear();
    delete window.location;
    window.location = { href: '' };
  });

  afterEach(() => {
    jest.dontMock('axios');
  });

  test('registers request and response interceptors', () => {
    const { requestUse, responseUse } = loadApiModule();

    expect(requestUse).toHaveBeenCalledTimes(1);
    expect(responseUse).toHaveBeenCalledTimes(1);
  });

  test('adds JSON content type and authorization header for normal requests', () => {
    const { requestUse } = loadApiModule();
    localStorage.setItem('token', 'token-123');

    const requestHandler = requestUse.mock.calls[0][0];
    const result = requestHandler({ headers: {} });

    expect(result.headers['Content-Type']).toBe('application/json');
    expect(result.headers.Authorization).toBe('Bearer token-123');
  });

  test('removes content type for FormData requests', () => {
    const { requestUse } = loadApiModule();

    const requestHandler = requestUse.mock.calls[0][0];
    const formData = new FormData();
    formData.append('file', new Blob(['test']));

    const result = requestHandler({
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    expect(result.headers['Content-Type']).toBeUndefined();
  });

  test('clears auth storage and redirects on 401 response error', async () => {
    const { responseUse } = loadApiModule();
    localStorage.setItem('token', 'token-123');
    localStorage.setItem('user', '{}');

    const responseErrorHandler = responseUse.mock.calls[0][1];
    await expect(responseErrorHandler({ response: { status: 401 } })).rejects.toEqual({
      response: { status: 401 },
    });

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(window.location.href).toBe('/login');
  });
});
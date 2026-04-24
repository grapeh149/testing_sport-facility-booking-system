import React from 'react';
import { AuthProvider, useAuth } from './AuthContext';

jest.mock('../services/api', () => ({
  post: jest.fn(),
  get: jest.fn(),
  put: jest.fn(),
  setAuthToken: jest.fn()
}));

const localStorageMock = (() => {
  let store = {};
  return {
    getItem: (key) => store[key] || null,
    setItem: (key, value) => {
      store[key] = value.toString();
    },
    removeItem: (key) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    }
  };
})();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
});

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('AuthProvider should be importable', () => {
    expect(AuthProvider).toBeDefined();
  });

  test('useAuth hook should be importable', () => {
    expect(useAuth).toBeDefined();
  });

  test('localStorage should be available', () => {
    expect(localStorage).toBeDefined();
  });
});


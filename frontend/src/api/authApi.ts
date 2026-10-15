import { apiClient } from './client';
import { AdminUser, LoginResponse } from '@/types';

export const authApi = {
  login: async (credentials: { email: string; password: string }): Promise<LoginResponse> => {
    const res = await apiClient.post<LoginResponse>('/api/v1/admin/auth/login', credentials);
    return res.data;
  },

  refresh: async (): Promise<LoginResponse> => {
    const res = await apiClient.post<LoginResponse>('/api/v1/admin/auth/refresh');
    return res.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/api/v1/admin/auth/logout');
  },

  getMe: async (): Promise<AdminUser> => {
    const res = await apiClient.get<AdminUser>('/api/v1/admin/auth/me');
    return res.data;
  },

  changePassword: async (data: { currentPassword: string; newPassword: string }): Promise<void> => {
    await apiClient.post('/api/v1/admin/auth/change-password', data);
  },
};

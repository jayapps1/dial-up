import { apiClient } from './client';
import { AdminUser, Page } from '@/types';

export const userApi = {
  getUsers: async (page = 0, size = 10): Promise<Page<AdminUser>> => {
    const res = await apiClient.get<Page<AdminUser>>('/api/v1/admin/users', {
      params: { page, size },
    });
    return res.data;
  },

  getUserById: async (userId: string): Promise<AdminUser> => {
    const res = await apiClient.get<AdminUser>(`/api/v1/admin/users/${userId}`);
    return res.data;
  },

  createUser: async (data: {
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
    agencyId?: string;
    region?: string;
    stationId?: string;
    roleCodes?: string[];
  }): Promise<{ user: AdminUser; tempPassword: string }> => {
    const res = await apiClient.post<{ user: AdminUser; tempPassword: string }>('/api/v1/admin/users', data);
    return res.data;
  },
};

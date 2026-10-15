import { apiClient } from './client';
import { Agency, AgencyType } from '@/types';

export const agencyApi = {
  getAgencies: async (): Promise<Agency[]> => {
    const res = await apiClient.get<Agency[]>('/api/v1/admin/agencies');
    return res.data;
  },

  getAgencyById: async (agencyId: string): Promise<Agency> => {
    const res = await apiClient.get<Agency>(`/api/v1/admin/agencies/${agencyId}`);
    return res.data;
  },

  createAgency: async (data: { name: string; code: string; type: AgencyType; emergencyNumber?: string }): Promise<Agency> => {
    const res = await apiClient.post<Agency>('/api/v1/admin/agencies', data);
    return res.data;
  },

  updateAgency: async (id: string, data: { name: string; type: AgencyType; emergencyNumber?: string }): Promise<Agency> => {
    const res = await apiClient.put<Agency>(`/api/v1/admin/agencies/${id}`, data);
    return res.data;
  },

  verifyAgency: async (id: string): Promise<Agency> => {
    const res = await apiClient.patch<Agency>(`/api/v1/admin/agencies/${id}/verify`);
    return res.data;
  },

  toggleAgencyStatus: async (id: string): Promise<Agency> => {
    const res = await apiClient.patch<Agency>(`/api/v1/admin/agencies/${id}/status`);
    return res.data;
  },
};

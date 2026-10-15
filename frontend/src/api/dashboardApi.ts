import { apiClient } from './client';
import { DashboardSummary, EmergencyActivityPoint } from '@/types';

export const dashboardApi = {
  getSummary: async (): Promise<DashboardSummary> => {
    const res = await apiClient.get<DashboardSummary>('/api/v1/admin/dashboard/summary');
    return res.data;
  },

  getActivity: async (period = '7D'): Promise<EmergencyActivityPoint[]> => {
    const res = await apiClient.get<EmergencyActivityPoint[]>('/api/v1/admin/dashboard/activity', {
      params: { period },
    });
    return res.data;
  },
};

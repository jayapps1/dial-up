import { apiClient } from './client';
import { DistrictRef, RegionRef } from '@/types';

export const referenceApi = {
  getRegions: async (): Promise<RegionRef[]> => {
    const res = await apiClient.get<RegionRef[]>('/api/v1/admin/reference/regions');
    return res.data;
  },

  getDistrictsByRegion: async (regionId: string): Promise<DistrictRef[]> => {
    const res = await apiClient.get<DistrictRef[]>(`/api/v1/admin/reference/regions/${regionId}/districts`);
    return res.data;
  },

  searchDistricts: async (regionId: string, search?: string): Promise<DistrictRef[]> => {
    const res = await apiClient.get<DistrictRef[]>('/api/v1/admin/reference/districts', {
      params: { regionId, search },
    });
    return res.data;
  },
};

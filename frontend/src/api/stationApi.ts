import { apiClient } from './client';
import { Page, Station, StationContact, StationContactType, StationDetails } from '@/types';

export const stationApi = {
  getAdminStations: async (page = 0, size = 10): Promise<Page<Station>> => {
    const res = await apiClient.get<Page<Station>>('/api/v1/admin/stations', {
      params: { page, size },
    });
    return res.data;
  },

  searchStations: async (query?: string): Promise<Station[]> => {
    const res = await apiClient.get<Station[]>('/api/v1/admin/stations/search', {
      params: { q: query },
    });
    return res.data;
  },

  getStationDetails: async (stationId: string): Promise<StationDetails> => {
    const res = await apiClient.get<StationDetails>(`/api/v1/admin/stations/${stationId}`);
    return res.data;
  },

  getStationContacts: async (stationId: string): Promise<StationContact[]> => {
    const res = await apiClient.get<StationContact[]>(`/api/v1/admin/stations/${stationId}/contacts`);
    return res.data;
  },

  createStation: async (data: {
    agencyId: string;
    name: string;
    code: string;
    region: string;
    district: string;
    locality?: string;
    latitude: number;
    longitude: number;
    locationAccuracyMeters?: number;
  }): Promise<Station> => {
    const res = await apiClient.post<Station>('/api/v1/admin/stations', data);
    return res.data;
  },

  updateStation: async (
    id: string,
    data: {
      name: string;
      region: string;
      district: string;
      locality?: string;
      latitude: number;
      longitude: number;
      locationAccuracyMeters?: number;
    }
  ): Promise<Station> => {
    const res = await apiClient.put<Station>(`/api/v1/admin/stations/${id}`, data);
    return res.data;
  },

  verifyStation: async (id: string): Promise<Station> => {
    const res = await apiClient.patch<Station>(`/api/v1/admin/stations/${id}/verify`);
    return res.data;
  },

  toggleStationStatus: async (id: string): Promise<Station> => {
    const res = await apiClient.patch<Station>(`/api/v1/admin/stations/${id}/status`);
    return res.data;
  },

  createStationContact: async (
    stationId: string,
    data: {
      phoneNumber: string;
      type: StationContactType;
      priority: number;
      publicContact: boolean;
      available24Hour: boolean;
    }
  ): Promise<StationContact> => {
    const res = await apiClient.post<StationContact>(`/api/v1/admin/stations/${stationId}/contacts`, data);
    return res.data;
  },

  updateStationContact: async (
    contactId: string,
    data: {
      phoneNumber: string;
      type: StationContactType;
      priority: number;
      publicContact: boolean;
      available24Hour: boolean;
    }
  ): Promise<StationContact> => {
    const res = await apiClient.put<StationContact>(`/api/v1/admin/stations/contacts/${contactId}`, data);
    return res.data;
  },

  verifyStationContact: async (contactId: string): Promise<StationContact> => {
    const res = await apiClient.patch<StationContact>(`/api/v1/admin/stations/contacts/${contactId}/verify`);
    return res.data;
  },
};

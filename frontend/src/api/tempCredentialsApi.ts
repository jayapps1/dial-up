import { apiClient } from './client';

export interface TemporaryCredential {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  roles: string[];
  agencyId?: string;
  agencyName?: string;
  region?: string;
  stationId?: string;
  stationName?: string;
  mustChangePassword: boolean;
  temporaryPasswordIssuedAt: string;
  temporaryPasswordExpiresAt: string;
  lastLoginAt?: string;
}

export interface ResetTempPasswordResponse {
  userId: string;
  email: string;
  temporaryPassword: string;
  mustChangePassword: boolean;
  issuedAt: string;
  expiresAt: string;
}

export const tempCredentialsApi = {
  getPendingCredentials: async (): Promise<TemporaryCredential[]> => {
    const res = await apiClient.get<TemporaryCredential[]>('/api/v1/admin/temporary-credentials');
    return res.data;
  },

  resetTemporaryPassword: async (userId: string): Promise<ResetTempPasswordResponse> => {
    const res = await apiClient.post<ResetTempPasswordResponse>(`/api/v1/admin/temporary-credentials/users/${userId}/reset-password`);
    return res.data;
  },
};

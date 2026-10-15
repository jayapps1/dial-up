import { apiClient } from './client';
import { AdminUser } from '@/types';

export interface AdminAccessRequest {
  id: string;
  requestType: string;
  agencyId: string;
  requestedRole: string;
  candidateFirstName: string;
  candidateLastName: string;
  candidateEmail: string;
  candidatePhone?: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  requestedBy: string;
  reviewedBy?: string;
  reviewedAt?: string;
  rejectionReason?: string;
  createdAt: string;
}

export interface ApproveRequestResponse {
  request: AdminAccessRequest;
  createdUser: AdminUser;
  temporaryPassword: string;
}

export const adminRequestApi = {
  getRequests: async (): Promise<AdminAccessRequest[]> => {
    const res = await apiClient.get<AdminAccessRequest[]>('/api/v1/admin/admin-access-requests');
    return res.data;
  },

  submitRequest: async (data: {
    candidateFirstName: string;
    candidateLastName: string;
    candidateEmail: string;
    candidatePhone?: string;
    reason: string;
  }): Promise<AdminAccessRequest> => {
    const res = await apiClient.post<AdminAccessRequest>('/api/v1/admin/admin-access-requests', data);
    return res.data;
  },

  approveRequest: async (requestId: string): Promise<ApproveRequestResponse> => {
    const res = await apiClient.post<ApproveRequestResponse>(`/api/v1/admin/admin-access-requests/${requestId}/approve`);
    return res.data;
  },

  rejectRequest: async (requestId: string, reason?: string): Promise<AdminAccessRequest> => {
    const res = await apiClient.post<AdminAccessRequest>(`/api/v1/admin/admin-access-requests/${requestId}/reject`, null, {
      params: { reason },
    });
    return res.data;
  },
};

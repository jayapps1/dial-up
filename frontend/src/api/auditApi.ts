import { apiClient } from './client';
import { AuditLog, Page } from '@/types';

export const auditApi = {
  getAuditLogs: async (page = 0, size = 10): Promise<Page<AuditLog>> => {
    const res = await apiClient.get<Page<AuditLog>>('/api/v1/admin/audit-logs', {
      params: { page, size },
    });
    return res.data;
  },
};

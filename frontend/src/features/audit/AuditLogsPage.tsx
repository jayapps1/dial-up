import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Chip,
  Skeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { auditApi } from '@/api/auditApi';

export const AuditLogsPage: React.FC = () => {
  const { data: logsPage, isLoading } = useQuery({
    queryKey: ['auditLogs', 0],
    queryFn: () => auditApi.getAuditLogs(0, 10),
  });

  const logs = logsPage?.content || [];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          System Audit Logs
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Security and operational audit trail
        </Typography>
      </Box>

      <Card>
        <CardContent sx={{ p: 0 }}>
          {isLoading ? (
            <Box sx={{ p: 3 }}>
              <Skeleton variant="rectangular" height={250} />
            </Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Timestamp</TableCell>
                    <TableCell>Action</TableCell>
                    <TableCell>Entity</TableCell>
                    <TableCell>Description</TableCell>
                    <TableCell>IP Address</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {logs.map((log) => (
                    <TableRow key={log.id} hover>
                      <TableCell sx={{ fontSize: '0.85rem' }}>
                        {new Date(log.createdAt).toLocaleString()}
                      </TableCell>
                      <TableCell>
                        <Chip label={log.action} size="small" color="primary" variant="outlined" />
                      </TableCell>
                      <TableCell>{log.entityType}</TableCell>
                      <TableCell>{log.description || 'N/A'}</TableCell>
                      <TableCell sx={{ fontFamily: 'monospace' }}>{log.ipAddress || '—'}</TableCell>
                    </TableRow>
                  ))}
                  {logs.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                        No audit logs found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

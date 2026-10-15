import React, { useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Skeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { userRequestApi, UserManagementRequest } from '@/api/userRequestApi';
import { useAuthStore } from '@/auth/useAuthStore';

export const UserRequestsPage: React.FC = () => {
  const [rejectDialogReq, setRejectDialogReq] = useState<UserManagementRequest | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');

  const queryClient = useQueryClient();
  const { hasRole } = useAuthStore();

  const { data: requests, isLoading } = useQuery({
    queryKey: ['userRequests'],
    queryFn: userRequestApi.getRequests,
  });

  const approveMutation = useMutation({
    mutationFn: userRequestApi.approveRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userRequests'] });
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });

  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) => userRequestApi.rejectRequest(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userRequests'] });
      setRejectDialogReq(null);
      setRejectionReason('');
    },
  });

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          User Removal & Transfer Requests
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Review and approve agency administrator requests for user deactivation or account transfer
        </Typography>
      </Box>

      <Card>
        <CardContent sx={{ p: 0 }}>
          {isLoading ? (
            <Box sx={{ p: 3 }}><Skeleton variant="rectangular" height={200} /></Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Target User</TableCell>
                    <TableCell>Agency Scope</TableCell>
                    <TableCell>Request Type</TableCell>
                    <TableCell>Reason / Justification</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {(requests || []).map((req) => (
                    <TableRow key={req.id} hover>
                      <TableCell sx={{ fontWeight: 'bold' }}>
                        {req.targetUserName} ({req.targetUserEmail})
                      </TableCell>
                      <TableCell>{req.agencyName || req.agencyId}</TableCell>
                      <TableCell>
                        <Chip label={req.requestType} color="primary" size="small" variant="outlined" />
                      </TableCell>
                      <TableCell>{req.reason}</TableCell>
                      <TableCell>
                        <Chip
                          label={req.status}
                          color={req.status === 'APPROVED' ? 'success' : req.status === 'REJECTED' ? 'error' : 'warning'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="right">
                        {req.status === 'PENDING' && hasRole('SYSTEM_ADMIN') && (
                          <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                            <Button
                              size="small"
                              variant="contained"
                              color="success"
                              startIcon={<CheckCircleIcon />}
                              onClick={() => approveMutation.mutate(req.id)}
                            >
                              Approve
                            </Button>
                            <Button
                              size="small"
                              variant="outlined"
                              color="error"
                              startIcon={<CancelIcon />}
                              onClick={() => setRejectDialogReq(req)}
                            >
                              Reject
                            </Button>
                          </Box>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                  {(requests || []).length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                        No user removal or transfer requests found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Rejection Dialog */}
      <Dialog open={Boolean(rejectDialogReq)} onClose={() => setRejectDialogReq(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Reject User Request</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Rejection Reason"
            value={rejectionReason}
            onChange={(e) => setRejectionReason(e.target.value)}
            margin="normal"
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRejectDialogReq(null)}>Cancel</Button>
          <Button
            variant="contained"
            color="error"
            disabled={!rejectionReason.trim()}
            onClick={() => rejectDialogReq && rejectMutation.mutate({ id: rejectDialogReq.id, reason: rejectionReason })}
          >
            Confirm Rejection
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

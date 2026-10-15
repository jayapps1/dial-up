import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
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
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminRequestApi, AdminAccessRequest } from '@/api/adminRequestApi';
import { useAuthStore } from '@/auth/useAuthStore';

export const AdminRequestsPage: React.FC = () => {
  const [rejectDialogReq, setRejectDialogReq] = useState<AdminAccessRequest | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [approvedPasswordDialog, setApprovedPasswordDialog] = useState<string | null>(null);

  const queryClient = useQueryClient();
  const { hasRole } = useAuthStore();

  const { data: requests, isLoading } = useQuery({
    queryKey: ['adminRequests'],
    queryFn: adminRequestApi.getRequests,
  });

  const approveMutation = useMutation({
    mutationFn: adminRequestApi.approveRequest,
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['adminRequests'] });
      setApprovedPasswordDialog(res.temporaryPassword);
    },
  });

  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) => adminRequestApi.rejectRequest(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminRequests'] });
      setRejectDialogReq(null);
      setRejectionReason('');
    },
  });

  const handleCopyPassword = () => {
    if (approvedPasswordDialog) {
      navigator.clipboard.writeText(approvedPasswordDialog);
      alert('Temporary password copied to clipboard!');
    }
  };

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Agency Admin Access Requests
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Review and approve NHQ requests for additional Agency Administrator user accounts
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
                    <TableCell>Candidate Name</TableCell>
                    <TableCell>Candidate Email</TableCell>
                    <TableCell>Requested Role</TableCell>
                    <TableCell>Reason / Justification</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {(requests || []).map((req) => (
                    <TableRow key={req.id} hover>
                      <TableCell sx={{ fontWeight: 'bold' }}>
                        {req.candidateFirstName} {req.candidateLastName}
                      </TableCell>
                      <TableCell>{req.candidateEmail}</TableCell>
                      <TableCell>
                        <Chip label={req.requestedRole} color="primary" size="small" variant="outlined" />
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
                        No administrator access requests found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Rejection Reason Dialog */}
      <Dialog open={Boolean(rejectDialogReq)} onClose={() => setRejectDialogReq(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Reject Access Request</DialogTitle>
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

      {/* One-time Approved Password Dialog */}
      <Dialog open={Boolean(approvedPasswordDialog)} onClose={() => setApprovedPasswordDialog(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Agency Admin Account Created</DialogTitle>
        <DialogContent>
          <Alert severity="warning" sx={{ mb: 2 }}>
            This temporary password will be shown <strong>only once</strong>. Share it securely with the candidate.
          </Alert>

          <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Typography variant="h6" fontWeight="bold" fontFamily="monospace">
              {approvedPasswordDialog}
            </Typography>
            <IconButton onClick={handleCopyPassword} color="primary">
              <ContentCopyIcon />
            </IconButton>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" onClick={() => setApprovedPasswordDialog(null)}>
            Close & Done
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

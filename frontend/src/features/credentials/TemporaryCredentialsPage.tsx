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
  InputAdornment,
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
import SearchIcon from '@mui/icons-material/Search';
import LockResetIcon from '@mui/icons-material/LockReset';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tempCredentialsApi, TemporaryCredential } from '@/api/tempCredentialsApi';
import { useAuthStore } from '@/auth/useAuthStore';

export const TemporaryCredentialsPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [resetConfirmUser, setResetConfirmUser] = useState<TemporaryCredential | null>(null);
  const [newTempPasswordDialog, setNewTempPasswordDialog] = useState<string | null>(null);

  const queryClient = useQueryClient();
  const { hasPermission } = useAuthStore();

  const { data: credentials, isLoading } = useQuery({
    queryKey: ['tempCredentials'],
    queryFn: tempCredentialsApi.getPendingCredentials,
  });

  const resetMutation = useMutation({
    mutationFn: tempCredentialsApi.resetTemporaryPassword,
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['tempCredentials'] });
      setResetConfirmUser(null);
      setNewTempPasswordDialog(res.temporaryPassword);
    },
  });

  const handleCopyPassword = () => {
    if (newTempPasswordDialog) {
      navigator.clipboard.writeText(newTempPasswordDialog);
      alert('Temporary password copied to clipboard!');
    }
  };

  const filteredCredentials = (credentials || []).filter(
    (item) =>
      item.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const isExpired = (expiresAt?: string) => {
    if (!expiresAt) return false;
    return new Date(expiresAt).getTime() < Date.now();
  };

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Temporary Credentials Management
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage administrator accounts that have not yet completed their first password change
        </Typography>
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent sx={{ p: 2 }}>
          <TextField
            placeholder="Search pending credentials by name or email..."
            size="small"
            fullWidth
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </CardContent>
      </Card>

      <Card>
        <CardContent sx={{ p: 0 }}>
          {isLoading ? (
            <Box sx={{ p: 3 }}><Skeleton variant="rectangular" height={250} /></Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Administrator</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Role</TableCell>
                    <TableCell>Agency Scope</TableCell>
                    <TableCell>Issued At</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredCredentials.map((item) => {
                    const expired = isExpired(item.temporaryPasswordExpiresAt);
                    const isLegacy = !item.temporaryPasswordIssuedAt;

                    return (
                      <TableRow key={item.userId} hover>
                        <TableCell sx={{ fontWeight: 'bold' }}>
                          {item.firstName} {item.lastName}
                        </TableCell>
                        <TableCell>{item.email}</TableCell>
                        <TableCell>
                          <Chip label={item.roles[0] || 'User'} size="small" color="primary" variant="outlined" />
                        </TableCell>
                        <TableCell>{item.agencyName || item.agencyId || 'Platform-Wide'}</TableCell>
                        <TableCell>
                          {isLegacy ? (
                            <Typography variant="caption" color="text.secondary">
                              Unknown / Legacy Account
                            </Typography>
                          ) : (
                            new Date(item.temporaryPasswordIssuedAt).toLocaleString()
                          )}
                        </TableCell>
                        <TableCell>
                          {isLegacy ? (
                            <Chip label="Reset Required" color="error" size="small" />
                          ) : expired ? (
                            <Chip label="Expired" color="error" size="small" />
                          ) : (
                            <Chip label="Temporary Password Active" color="warning" size="small" />
                          )}
                        </TableCell>
                        <TableCell align="right">
                          {hasPermission('ADMIN_USER_UPDATE') && (
                            <Button
                              size="small"
                              variant="outlined"
                              color="primary"
                              startIcon={<LockResetIcon />}
                              onClick={() => setResetConfirmUser(item)}
                            >
                              {isLegacy ? 'Generate Temp Password' : 'Reset Temp Password'}
                            </Button>
                          )}
                        </TableCell>
                      </TableRow>
                    );
                  })}
                  {filteredCredentials.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={7} align="center" sx={{ py: 3 }}>
                        No pending temporary credentials found. All administrators have changed their passwords.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Confirmation Dialog */}
      <Dialog open={Boolean(resetConfirmUser)} onClose={() => setResetConfirmUser(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Generate / Reset Temporary Password</DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary">
            Generate a new temporary password for <strong>{resetConfirmUser?.email}</strong>? The previous password will stop working immediately.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setResetConfirmUser(null)}>Cancel</Button>
          <Button
            variant="contained"
            color="primary"
            onClick={() => resetConfirmUser && resetMutation.mutate(resetConfirmUser.userId)}
          >
            Generate Password
          </Button>
        </DialogActions>
      </Dialog>

      {/* One-Time Password Display Dialog */}
      <Dialog open={Boolean(newTempPasswordDialog)} onClose={() => setNewTempPasswordDialog(null)} maxWidth="xs" fullWidth>
        <DialogTitle>New Temporary Password Generated</DialogTitle>
        <DialogContent>
          <Alert severity="warning" sx={{ mb: 2 }}>
            This temporary password will be shown <strong>only once</strong>. Share it securely with the administrator.
          </Alert>

          <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Typography variant="h6" fontWeight="bold" fontFamily="monospace">
              {newTempPasswordDialog}
            </Typography>
            <IconButton onClick={handleCopyPassword} color="primary">
              <ContentCopyIcon />
            </IconButton>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" onClick={() => setNewTempPasswordDialog(null)}>
            Close & Done
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

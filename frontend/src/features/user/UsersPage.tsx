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
  MenuItem,
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
import AddIcon from '@mui/icons-material/Add';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { userApi } from '@/api/userApi';
import { agencyApi } from '@/api/agencyApi';
import { useAuthStore } from '@/auth/useAuthStore';
import { useNavigate } from 'react-router-dom';

export const UsersPage: React.FC = () => {
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [createdTempPassword, setCreatedTempPassword] = useState<string | null>(null);

  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [selectedRole, setSelectedRole] = useState('AGENCY_ADMIN');
  const [agencyId, setAgencyId] = useState('');

  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { user, hasRole, hasPermission } = useAuthStore();

  const { data: usersPage, isLoading } = useQuery({
    queryKey: ['users', 0],
    queryFn: () => userApi.getUsers(0, 10),
  });

  const { data: agencies } = useQuery({
    queryKey: ['agencies'],
    queryFn: agencyApi.getAgencies,
    enabled: hasPermission('AGENCY_READ'),
  });

  const createUserMutation = useMutation({
    mutationFn: userApi.createUser,
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setCreateDialogOpen(false);
      setCreatedTempPassword(res.tempPassword);
    },
  });

  const handleCreateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    createUserMutation.mutate({
      email,
      firstName,
      lastName,
      phoneNumber,
      agencyId: agencyId || user?.agencyId,
      roleCodes: [selectedRole],
    });
  };

  const handleCopyPassword = () => {
    if (createdTempPassword) {
      navigator.clipboard.writeText(createdTempPassword);
      alert('Temporary password copied to clipboard!');
    }
  };

  const users = usersPage?.content || [];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Admin User Accounts
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage system administrators, agency admins, and control room operators
          </Typography>
        </div>

        {hasPermission('ADMIN_USER_CREATE') && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateDialogOpen(true)}>
            Add Admin User
          </Button>
        )}
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
                    <TableCell>Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Roles</TableCell>
                    <TableCell>Agency Scope</TableCell>
                    <TableCell>Password Status</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.map((userItem) => (
                    <TableRow
                      key={userItem.id}
                      hover
                      sx={{ cursor: 'pointer' }}
                      onClick={() => navigate(`/users/${userItem.id}`)}
                    >
                      <TableCell sx={{ fontWeight: 'bold' }}>
                        {userItem.firstName} {userItem.lastName}
                      </TableCell>
                      <TableCell>{userItem.email}</TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                          {userItem.roles.map((role) => (
                            <Chip key={role} label={role} size="small" color="primary" />
                          ))}
                        </Box>
                      </TableCell>
                      <TableCell>{userItem.agencyName || userItem.agencyId || 'Platform-Wide'}</TableCell>
                      <TableCell>
                        {userItem.mustChangePassword ? (
                          <Chip label="Must Change Password" color="warning" size="small" variant="outlined" />
                        ) : (
                          <Chip label="Password Set" color="success" size="small" variant="outlined" />
                        )}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={userItem.active ? 'Active' : 'Inactive'}
                          color={userItem.active ? 'success' : 'default'}
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                  {users.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                        No admin user accounts found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Create Admin User Dialog */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleCreateSubmit}>
          <DialogTitle>Create Admin User</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Email Address"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="First Name"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Last Name"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Phone Number"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              margin="normal"
            />

            <TextField
              select
              fullWidth
              label="Role Assignment"
              value={selectedRole}
              onChange={(e) => setSelectedRole(e.target.value)}
              margin="normal"
              required
            >
              {hasRole('SYSTEM_ADMIN') && <MenuItem value="AGENCY_ADMIN">AGENCY_ADMIN</MenuItem>}
              <MenuItem value="REGIONAL_ADMIN">REGIONAL_ADMIN</MenuItem>
              <MenuItem value="STATION_ADMIN">STATION_ADMIN</MenuItem>
              <MenuItem value="CONTROL_ROOM_OPERATOR">CONTROL_ROOM_OPERATOR</MenuItem>
            </TextField>

            {hasRole('SYSTEM_ADMIN') && (
              <TextField
                select
                fullWidth
                label="Assign Agency"
                value={agencyId}
                onChange={(e) => setAgencyId(e.target.value)}
                margin="normal"
              >
                {(agencies || []).map((agency) => (
                  <MenuItem key={agency.id} value={agency.id}>
                    {agency.name}
                  </MenuItem>
                ))}
              </TextField>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
            <Button type="submit" variant="contained">
              Create User
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* One-time Temp Password Dialog */}
      <Dialog open={Boolean(createdTempPassword)} onClose={() => setCreatedTempPassword(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Administrator Account Created</DialogTitle>
        <DialogContent>
          <Alert severity="warning" sx={{ mb: 2 }}>
            This temporary password will be shown <strong>only once</strong>. Provide it to the new administrator.
          </Alert>

          <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Typography variant="h6" fontWeight="bold" fontFamily="monospace">
              {createdTempPassword}
            </Typography>
            <IconButton onClick={handleCopyPassword} color="primary">
              <ContentCopyIcon />
            </IconButton>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" onClick={() => setCreatedTempPassword(null)}>
            Close & Done
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

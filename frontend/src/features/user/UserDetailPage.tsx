import React from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  Skeleton,
  Typography,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import PersonIcon from '@mui/icons-material/Person';
import LockResetIcon from '@mui/icons-material/LockReset';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { userApi } from '@/api/userApi';
import { useAuthStore } from '@/auth/useAuthStore';

export const UserDetailPage: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const { hasPermission } = useAuthStore();

  const { data: userDetails, isLoading } = useQuery({
    queryKey: ['user', userId],
    queryFn: () => userApi.getUserById(userId!),
    enabled: Boolean(userId),
  });

  if (isLoading) {
    return (
      <Box>
        <Skeleton variant="rectangular" height={300} />
      </Box>
    );
  }

  if (!userDetails) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h6" color="error">
          User account not found or access restricted.
        </Typography>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/users')} sx={{ mt: 2 }}>
          Back to Users
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/users')} sx={{ mb: 3 }}>
        Back to Users List
      </Button>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card sx={{ mb: 3 }}>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
                <PersonIcon color="primary" sx={{ fontSize: 48 }} />
                <div>
                  <Typography variant="h4" fontWeight="bold">
                    {userDetails.firstName} {userDetails.lastName}
                  </Typography>
                  <Typography variant="body1" color="text.secondary">
                    {userDetails.email}
                  </Typography>
                </div>
              </Box>

              <Divider sx={{ mb: 3 }} />

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">
                    Role Assignments
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mt: 0.5 }}>
                    {userDetails.roles.map((r) => (
                      <Chip key={r} label={r} color="primary" size="small" />
                    ))}
                  </Box>
                </Grid>

                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">
                    Agency Scope
                  </Typography>
                  <Typography variant="body1" fontWeight="bold" sx={{ mt: 0.5 }}>
                    {userDetails.agencyName || userDetails.agencyId || 'Platform-Wide (System)'}
                  </Typography>
                </Grid>

                <Grid item xs={6} sx={{ mt: 2 }}>
                  <Typography variant="caption" color="text.secondary">
                    Account Status
                  </Typography>
                  <Box sx={{ mt: 0.5 }}>
                    <Chip
                      label={userDetails.active ? 'Active Account' : 'Deactivated'}
                      color={userDetails.active ? 'success' : 'default'}
                      size="small"
                    />
                  </Box>
                </Grid>

                <Grid item xs={6} sx={{ mt: 2 }}>
                  <Typography variant="caption" color="text.secondary">
                    Password Status
                  </Typography>
                  <Box sx={{ mt: 0.5 }}>
                    {userDetails.mustChangePassword ? (
                      <Chip label="Must Change Temporary Password" color="warning" size="small" variant="outlined" />
                    ) : (
                      <Chip label="Password Set" color="success" size="small" variant="outlined" />
                    )}
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Account Security Actions
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {hasPermission('ADMIN_USER_UPDATE') && (
                <Button
                  fullWidth
                  variant="outlined"
                  startIcon={<LockResetIcon />}
                  onClick={() => navigate('/temporary-credentials')}
                  sx={{ mb: 2 }}
                >
                  Manage Temporary Password
                </Button>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

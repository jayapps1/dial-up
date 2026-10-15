import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  Snackbar,
  TextField,
  Typography,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuthStore } from '@/auth/useAuthStore';
import { authApi } from '@/api/authApi';

const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: z.string().min(8, 'New password must be at least 8 characters'),
    confirmPassword: z.string().min(1, 'Please confirm your new password'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

type PasswordFormData = z.infer<typeof passwordSchema>;

export const ProfilePage: React.FC = () => {
  const user = useAuthStore((state) => state.user);
  const roles = useAuthStore((state) => state.roles);

  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<PasswordFormData>({
    resolver: zodResolver(passwordSchema),
  });

  const onPasswordChange = async (data: PasswordFormData) => {
    setErrorMessage(null);
    setIsSubmitting(true);
    try {
      await authApi.changePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      });
      setToastMessage('Password changed successfully.');
      reset();
    } catch (e: any) {
      setErrorMessage(e.response?.data?.message || 'Failed to change password.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Admin Profile
      </Typography>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Account Details
              </Typography>
              <Divider sx={{ my: 2 }} />

              <Typography variant="subtitle2" color="text.secondary">Name</Typography>
              <Typography variant="body1" fontWeight={500} sx={{ mb: 2 }}>
                {user?.firstName} {user?.lastName}
              </Typography>

              <Typography variant="subtitle2" color="text.secondary">Email</Typography>
              <Typography variant="body1" fontWeight={500} sx={{ mb: 2 }}>
                {user?.email}
              </Typography>

              <Typography variant="subtitle2" color="text.secondary">Phone</Typography>
              <Typography variant="body1" fontWeight={500} sx={{ mb: 2 }}>
                {user?.phoneNumber || 'N/A'}
              </Typography>

              <Typography variant="subtitle2" color="text.secondary">Roles</Typography>
              <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 0.5 }}>
                {roles.map((role) => (
                  <Chip key={role} label={role} color="primary" size="small" />
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={7}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Change Password
              </Typography>
              <Divider sx={{ my: 2 }} />

              {errorMessage && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {errorMessage}
                </Alert>
              )}

              <form onSubmit={handleSubmit(onPasswordChange)}>
                <TextField
                  fullWidth
                  label="Current Password"
                  type="password"
                  margin="normal"
                  {...register('currentPassword')}
                  error={Boolean(errors.currentPassword)}
                  helperText={errors.currentPassword?.message}
                />

                <TextField
                  fullWidth
                  label="New Password"
                  type="password"
                  margin="normal"
                  {...register('newPassword')}
                  error={Boolean(errors.newPassword)}
                  helperText={errors.newPassword?.message}
                />

                <TextField
                  fullWidth
                  label="Confirm New Password"
                  type="password"
                  margin="normal"
                  {...register('confirmPassword')}
                  error={Boolean(errors.confirmPassword)}
                  helperText={errors.confirmPassword?.message}
                />

                <Button
                  type="submit"
                  variant="contained"
                  disabled={isSubmitting}
                  sx={{ mt: 2 }}
                >
                  {isSubmitting ? 'Updating...' : 'Update Password'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Snackbar
        open={Boolean(toastMessage)}
        autoHideDuration={4000}
        onClose={() => setToastMessage(null)}
        message={toastMessage}
      />
    </Box>
  );
};

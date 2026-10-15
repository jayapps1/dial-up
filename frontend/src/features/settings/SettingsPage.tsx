import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Divider,
  Grid,
  Switch,
  Typography,
} from '@mui/material';

export const SettingsPage: React.FC = () => {
  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          System Settings
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Platform-wide configuration and operational policies
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                Security & Session Policies
              </Typography>
              <Divider sx={{ my: 2 }} />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 1 }}>
                <div>
                  <Typography variant="subtitle1" fontWeight="bold">Require HttpOnly Refresh Cookie</Typography>
                  <Typography variant="body2" color="text.secondary">Delivers 7-day refresh tokens strictly via HttpOnly cookies</Typography>
                </div>
                <Switch defaultChecked disabled />
              </Box>

              <Divider sx={{ my: 2 }} />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 1 }}>
                <div>
                  <Typography variant="subtitle1" fontWeight="bold">Account Lockout Policy</Typography>
                  <Typography variant="body2" color="text.secondary">Automatically locks admin account after 5 failed login attempts</Typography>
                </div>
                <Switch defaultChecked disabled />
              </Box>

              <Divider sx={{ my: 2 }} />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 1 }}>
                <div>
                  <Typography variant="subtitle1" fontWeight="bold">Strict Agency Scope Enforcement</Typography>
                  <Typography variant="body2" color="text.secondary">Prevents cross-agency resource tampering at the API layer</Typography>
                </div>
                <Switch defaultChecked disabled />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

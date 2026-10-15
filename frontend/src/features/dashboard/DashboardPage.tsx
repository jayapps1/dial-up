import React from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Grid,
  Skeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import BusinessIcon from '@mui/icons-material/Business';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';
import WarningIcon from '@mui/icons-material/Warning';
import AddIcon from '@mui/icons-material/Add';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import { useQuery } from '@tanstack/react-query';
import { agencyApi } from '@/api/agencyApi';
import { reportApi } from '@/api/reportApi';
import { dashboardApi } from '@/api/dashboardApi';
import { useAuthStore } from '@/auth/useAuthStore';
import { useNavigate } from 'react-router-dom';
import { EmergencyActivityChart } from './EmergencyActivityChart';

export const DashboardPage: React.FC = () => {
  const { user, hasRole, hasPermission } = useAuthStore();
  const navigate = useNavigate();

  const { data: summary, isLoading: loadingSummary } = useQuery({
    queryKey: ['dashboardSummary'],
    queryFn: dashboardApi.getSummary,
  });

  const { data: reportsPage, isLoading: loadingReports } = useQuery({
    queryKey: ['reports', 0, 5],
    queryFn: () => reportApi.getReports(0, 5),
    enabled: hasPermission('REPORT_READ'),
  });

  const reports = reportsPage?.content || [];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
        <div>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Welcome back, {user?.firstName}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {hasRole('SYSTEM_ADMIN')
              ? 'Platform-Wide System Emergency Command Overview'
              : `Agency Dashboard — ${user?.email}`}
          </Typography>
        </div>

        {/* Quick Action Buttons */}
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          {hasPermission('AGENCY_CREATE') && (
            <Button variant="contained" size="small" startIcon={<AddIcon />} onClick={() => navigate('/agencies')}>
              New Agency
            </Button>
          )}
          {hasPermission('STATION_CREATE') && (
            <Button variant="contained" color="secondary" size="small" startIcon={<LocalOfferIcon />} onClick={() => navigate('/stations')}>
              New Station
            </Button>
          )}
          {hasPermission('ADMIN_USER_CREATE') && (
            <Button variant="outlined" size="small" startIcon={<PersonAddIcon />} onClick={() => navigate('/users')}>
              Add User
            </Button>
          )}
        </Box>
      </Box>

      {/* KPI Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {hasPermission('AGENCY_READ') && (
          <Grid item xs={12} sm={6} md={2.4}>
            <StatCard
              title="Agencies"
              value={loadingSummary ? <Skeleton width={40} /> : summary?.totalAgencies || 0}
              icon={<BusinessIcon color="primary" />}
              subtitle="Verified Agencies"
            />
          </Grid>
        )}

        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Active Stations"
            value={loadingSummary ? <Skeleton width={40} /> : summary?.activeStations || 0}
            icon={<LocalOfferIcon color="primary" />}
            subtitle="Operational Units"
          />
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Reports Today"
            value={loadingSummary ? <Skeleton width={40} /> : summary?.reportsToday || 0}
            icon={<ReportProblemIcon color="primary" />}
            subtitle="Submitted Today"
          />
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Critical Incidents"
            value={loadingSummary ? <Skeleton width={40} /> : summary?.criticalIncidents || 0}
            icon={<WarningIcon color="error" />}
            subtitle="High Priority"
          />
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            title="Unacknowledged"
            value={loadingSummary ? <Skeleton width={40} /> : summary?.unacknowledgedReports || 0}
            icon={<ReportProblemIcon color="secondary" />}
            subtitle="Awaiting Review"
          />
        </Grid>
      </Grid>

      {/* Recharts Activity Line Chart */}
      <EmergencyActivityChart />

      {/* Recent Incidents Table */}
      {hasPermission('REPORT_READ') && (
        <Card>
          <CardContent sx={{ p: 3 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Recent Operational Incidents
            </Typography>

            {loadingReports ? (
              <Skeleton variant="rectangular" height={200} />
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Reference</TableCell>
                      <TableCell>Category</TableCell>
                      <TableCell>Severity</TableCell>
                      <TableCell>Mode</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Submitted Time</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {reports.map((report) => (
                      <TableRow
                        key={report.id}
                        hover
                        sx={{ cursor: 'pointer' }}
                        onClick={() => navigate(`/reports/${report.id}`)}
                      >
                        <TableCell sx={{ fontWeight: 'bold' }}>{report.publicReference}</TableCell>
                        <TableCell>{report.categoryName || 'Incident'}</TableCell>
                        <TableCell>
                          <Chip
                            label={report.severity}
                            color={report.severity === 'CRITICAL' ? 'error' : 'default'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={report.mode}
                            color={report.mode === 'SILENT' ? 'secondary' : 'default'}
                            size="small"
                            variant="outlined"
                          />
                        </TableCell>
                        <TableCell>
                          <Chip label={report.status} size="small" />
                        </TableCell>
                        <TableCell>{new Date(report.createdAt).toLocaleString()}</TableCell>
                      </TableRow>
                    ))}
                    {reports.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={6} align="center">
                          No recent incident reports found.
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

const StatCard: React.FC<{
  title: string;
  value: React.ReactNode;
  icon: React.ReactNode;
  subtitle: string;
}> = ({ title, value, icon, subtitle }) => (
  <Card sx={{ height: '100%' }}>
    <CardContent sx={{ p: 2.5 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
        <Typography variant="caption" color="text.secondary" fontWeight="bold">
          {title}
        </Typography>
        {icon}
      </Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        {value}
      </Typography>
      <Typography variant="caption" color="text.secondary">
        {subtitle}
      </Typography>
    </CardContent>
  </Card>
);

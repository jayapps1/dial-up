import React from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  Link,
  Skeleton,
  Typography,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import MapIcon from '@mui/icons-material/Map';
import ShieldIcon from '@mui/icons-material/Shield';
import { useQuery } from '@tanstack/react-query';
import { reportApi } from '@/api/reportApi';
import { useNavigate, useParams } from 'react-router-dom';

export const ReportDetailsPage: React.FC = () => {
  const { reportId } = useParams<{ reportId: string }>();
  const navigate = useNavigate();

  const { data: report, isLoading } = useQuery({
    queryKey: ['reportDetails', reportId],
    queryFn: () => reportApi.getReportDetails(reportId!),
    enabled: Boolean(reportId),
  });

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <Skeleton variant="rectangular" height={300} />
      </Box>
    );
  }

  if (!report) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Report not found.</Alert>
      </Box>
    );
  }

  const mapUrl =
    report.latitude && report.longitude
      ? `https://maps.google.com/?q=${report.latitude},${report.longitude}`
      : null;

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/reports')}>
          Back to Reports
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          <Card sx={{ mb: 3 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="overline" color="text.secondary">
                Public Reference
              </Typography>
              <Typography variant="h4" fontWeight="bold" color="primary" gutterBottom>
                {report.publicReference}
              </Typography>

              <Box sx={{ display: 'flex', gap: 1, my: 2 }}>
                <Chip label={`Status: ${report.status}`} color="primary" />
                <Chip label={`Severity: ${report.severity}`} color={report.severity === 'CRITICAL' ? 'error' : 'default'} />
                <Chip label={`Mode: ${report.mode}`} variant="outlined" />
              </Box>

              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Category: <strong>{report.categoryName}</strong> | Created: <strong>{new Date(report.createdAt).toLocaleString()}</strong>
              </Typography>

              <Divider sx={{ my: 2 }} />

              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Description Notes
              </Typography>
              <Typography variant="body1" sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                {report.description || 'No description provided.'}
              </Typography>
            </CardContent>
          </Card>

          {/* Vehicle Info */}
          {(report.vehicleRegistrationNumber || report.vehicleType) && (
            <Card sx={{ mb: 3 }}>
              <CardContent sx={{ p: 3 }}>
                <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                  Vehicle Information
                </Typography>
                <Divider sx={{ my: 1.5 }} />
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">Plate Number</Typography>
                    <Typography variant="body2" fontWeight="bold">{report.vehicleRegistrationNumber || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">Vehicle Type</Typography>
                    <Typography variant="body2" fontWeight="bold">{report.vehicleType || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">Colour</Typography>
                    <Typography variant="body2" fontWeight="bold">{report.vehicleColour || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">Direction of Travel</Typography>
                    <Typography variant="body2" fontWeight="bold">{report.directionOfTravel || 'N/A'}</Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          )}

          {/* Location Details */}
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle1" fontWeight="bold">
                  Location Details
                </Typography>
                {mapUrl && (
                  <Link href={mapUrl} target="_blank" rel="noopener" underline="none">
                    <Button size="small" startIcon={<MapIcon />}>Open in Map</Button>
                  </Link>
                )}
              </Box>
              <Divider sx={{ my: 1.5 }} />

              {report.latitude && report.longitude ? (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <LocationOnIcon color="primary" />
                  <Typography variant="body2">
                    Coordinates: <strong>{report.latitude}, {report.longitude}</strong>
                    {report.locationAccuracyMeters && ` (±${report.locationAccuracyMeters}m)`}
                  </Typography>
                </Box>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  No GPS coordinates attached.
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={5}>
          {/* Target Agencies */}
          <Card sx={{ mb: 3 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Target Agency Assignments
              </Typography>
              <Divider sx={{ my: 1.5 }} />
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                {report.targetAgencyTypes?.map((agency) => (
                  <Chip
                    key={agency}
                    icon={<ShieldIcon />}
                    label={agency}
                    color="primary"
                    variant="outlined"
                    sx={{ justifyContent: 'flex-start', py: 2 }}
                  />
                ))}
              </Box>
            </CardContent>
          </Card>

          {/* Status History Timeline */}
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Status History Timeline
              </Typography>
              <Divider sx={{ my: 1.5 }} />

              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                {report.statusHistory?.map((hist, idx) => (
                  <Box key={idx} sx={{ pl: 2, borderLeft: '2px solid', borderColor: 'primary.main' }}>
                    <Typography variant="subtitle2" fontWeight="bold">
                      {hist.status}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {new Date(hist.timestamp).toLocaleString()}
                    </Typography>
                    {hist.note && (
                      <Typography variant="body2" color="text.primary" sx={{ mt: 0.5 }}>
                        {hist.note}
                      </Typography>
                    )}
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

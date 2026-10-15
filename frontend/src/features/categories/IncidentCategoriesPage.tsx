import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export const IncidentCategoriesPage: React.FC = () => {
  const categories = [
    { id: '1', code: 'FIRE', name: 'Fire & Explosion', severity: 'HIGH', supportsSilent: true, supportsGeneral: true, agencies: ['FIRE'] },
    { id: '2', code: 'ROBBERY', name: 'Armed Robbery / Assault', severity: 'CRITICAL', supportsSilent: true, supportsGeneral: true, agencies: ['POLICE'] },
    { id: '3', code: 'MEDICAL', name: 'Medical Emergency', severity: 'HIGH', supportsSilent: true, supportsGeneral: true, agencies: ['AMBULANCE'] },
    { id: '4', code: 'ACCIDENT', name: 'Road Traffic Accident', severity: 'HIGH', supportsSilent: true, supportsGeneral: true, agencies: ['POLICE', 'AMBULANCE', 'FIRE'] },
    { id: '5', code: 'HAZARD', name: 'Public Safety Hazard', severity: 'MEDIUM', supportsSilent: false, supportsGeneral: true, agencies: ['DISASTER_MANAGEMENT', 'FIRE'] },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Incident Categories
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Configure emergency report categories and default multi-agency routing
        </Typography>
      </Box>

      <Card>
        <CardContent sx={{ p: 0 }}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Code</TableCell>
                  <TableCell>Category Name</TableCell>
                  <TableCell>Default Severity</TableCell>
                  <TableCell>Silent Mode</TableCell>
                  <TableCell>General Mode</TableCell>
                  <TableCell>Assigned Agencies</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {categories.map((cat) => (
                  <TableRow key={cat.id} hover>
                    <TableCell sx={{ fontWeight: 'bold' }}>{cat.code}</TableCell>
                    <TableCell>{cat.name}</TableCell>
                    <TableCell>
                      <Chip
                        label={cat.severity}
                        color={cat.severity === 'CRITICAL' ? 'error' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{cat.supportsSilent ? <CheckCircleIcon color="primary" fontSize="small" /> : '—'}</TableCell>
                    <TableCell>{cat.supportsGeneral ? <CheckCircleIcon color="primary" fontSize="small" /> : '—'}</TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                        {cat.agencies.map((agency) => (
                          <Chip key={agency} label={agency} size="small" variant="outlined" />
                        ))}
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>
    </Box>
  );
};

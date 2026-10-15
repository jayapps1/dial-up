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

export const RolesPage: React.FC = () => {
  const roles = [
    { code: 'SYSTEM_ADMIN', name: 'System Administrator', description: 'Full platform-wide administrative access', permCount: 27 },
    { code: 'AGENCY_ADMIN', name: 'Agency Administrator', description: 'Management access within assigned agency', permCount: 17 },
    { code: 'REGIONAL_ADMIN', name: 'Regional Administrator', description: 'Management access within assigned region and agency', permCount: 12 },
    { code: 'STATION_ADMIN', name: 'Station Administrator', description: 'Management access within assigned station', permCount: 8 },
    { code: 'CONTROL_ROOM_OPERATOR', name: 'Control Room Operator', description: 'Operational report handling and assignment', permCount: 6 },
    { code: 'REPORT_REVIEWER', name: 'Report Reviewer', description: 'Review and classify incoming incident reports', permCount: 4 },
    { code: 'AUDITOR', name: 'System Auditor', description: 'Read-only compliance and audit log access', permCount: 5 },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Roles & Permissions
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Role-Based Access Control (RBAC) definitions and permission mappings
        </Typography>
      </Box>

      <Card>
        <CardContent sx={{ p: 0 }}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Role Code</TableCell>
                  <TableCell>Role Name</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell>Permissions Count</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {roles.map((role) => (
                  <TableRow key={role.code} hover>
                    <TableCell sx={{ fontWeight: 'bold' }}>{role.code}</TableCell>
                    <TableCell>{role.name}</TableCell>
                    <TableCell>{role.description}</TableCell>
                    <TableCell>
                      <Chip label={`${role.permCount} Permissions`} color="primary" size="small" />
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

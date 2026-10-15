import React, { useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
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
import CheckIcon from '@mui/icons-material/Check';
import UpdateIcon from '@mui/icons-material/Update';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { reportApi } from '@/api/reportApi';
import { AgencyType, ReportStatus } from '@/types';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/auth/useAuthStore';

export const ReportsPage: React.FC = () => {
  const [page] = useState(0);
  const [statusDialogReportId, setStatusDialogReportId] = useState<string | null>(null);
  const [transferDialogReportId, setTransferDialogReportId] = useState<string | null>(null);
  const [newStatus, setNewStatus] = useState<ReportStatus>('RECEIVED');
  const [transferAgency, setTransferAgency] = useState<AgencyType>('POLICE');
  const [statusNote, setStatusNote] = useState('');

  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { hasPermission } = useAuthStore();

  const { data: reportsPage, isLoading } = useQuery({
    queryKey: ['reports', page],
    queryFn: () => reportApi.getReports(page, 10),
  });

  const acknowledgeMutation = useMutation({
    mutationFn: (reportId: string) => reportApi.acknowledgeReport(reportId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reports'] });
    },
  });

  const updateStatusMutation = useMutation({
    mutationFn: ({ id, status, note }: { id: string; status: ReportStatus; note: string }) =>
      reportApi.updateReportStatus(id, { status, note }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reports'] });
      setStatusDialogReportId(null);
      setStatusNote('');
    },
  });

  const transferMutation = useMutation({
    mutationFn: ({ id, agency, note }: { id: string; agency: AgencyType; note: string }) =>
      reportApi.transferReport(id, { targetAgencyType: agency, note }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reports'] });
      setTransferDialogReportId(null);
      setStatusNote('');
    },
  });

  const reports = reportsPage?.content || [];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Incident Reports Management
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Operational incoming reports, assignment, and status updates
        </Typography>
      </Box>

      <Card>
        <CardContent sx={{ p: 0 }}>
          {isLoading ? (
            <Box sx={{ p: 3 }}>
              <Skeleton variant="rectangular" height={300} />
            </Box>
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
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {reports.map((report) => (
                    <TableRow key={report.id} hover>
                      <TableCell
                        sx={{ fontWeight: 'bold', cursor: 'pointer', color: 'primary.main' }}
                        onClick={() => navigate(`/reports/${report.id}`)}
                      >
                        {report.publicReference}
                      </TableCell>
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
                      <TableCell align="right">
                        <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'flex-end' }}>
                          {hasPermission('REPORT_ACKNOWLEDGE') && report.status === 'SUBMITTED' && (
                            <Button
                              size="small"
                              variant="contained"
                              color="primary"
                              startIcon={<CheckIcon />}
                              onClick={() => acknowledgeMutation.mutate(report.id)}
                            >
                              Acknowledge
                            </Button>
                          )}

                          {hasPermission('REPORT_UPDATE_STATUS') && (
                            <Button
                              size="small"
                              variant="outlined"
                              startIcon={<UpdateIcon />}
                              onClick={() => setStatusDialogReportId(report.id)}
                            >
                              Status
                            </Button>
                          )}

                          {hasPermission('REPORT_TRANSFER') && (
                            <Button
                              size="small"
                              variant="outlined"
                              color="secondary"
                              startIcon={<SwapHorizIcon />}
                              onClick={() => setTransferDialogReportId(report.id)}
                            >
                              Transfer
                            </Button>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                  {reports.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={7} align="center" sx={{ py: 3 }}>
                        No incident reports found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>

      {/* Update Status Dialog */}
      <Dialog open={Boolean(statusDialogReportId)} onClose={() => setStatusDialogReportId(null)}>
        <DialogTitle>Update Report Status</DialogTitle>
        <DialogContent sx={{ pt: 1, minWidth: 320 }}>
          <TextField
            select
            fullWidth
            label="New Status"
            value={newStatus}
            onChange={(e) => setNewStatus(e.target.value as ReportStatus)}
            margin="normal"
          >
            <MenuItem value="RECEIVED">RECEIVED</MenuItem>
            <MenuItem value="ACKNOWLEDGED">ACKNOWLEDGED</MenuItem>
            <MenuItem value="ASSIGNED">ASSIGNED</MenuItem>
            <MenuItem value="IN_PROGRESS">IN_PROGRESS</MenuItem>
            <MenuItem value="RESOLVED">RESOLVED</MenuItem>
            <MenuItem value="CLOSED">CLOSED</MenuItem>
            <MenuItem value="REJECTED">REJECTED</MenuItem>
            <MenuItem value="DUPLICATE">DUPLICATE</MenuItem>
          </TextField>

          <TextField
            fullWidth
            label="Operational Note"
            multiline
            rows={2}
            value={statusNote}
            onChange={(e) => setStatusNote(e.target.value)}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStatusDialogReportId(null)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={() =>
              statusDialogReportId &&
              updateStatusMutation.mutate({ id: statusDialogReportId, status: newStatus, note: statusNote })
            }
          >
            Update
          </Button>
        </DialogActions>
      </Dialog>

      {/* Transfer Agency Dialog */}
      <Dialog open={Boolean(transferDialogReportId)} onClose={() => setTransferDialogReportId(null)}>
        <DialogTitle>Transfer Report to Agency</DialogTitle>
        <DialogContent sx={{ pt: 1, minWidth: 320 }}>
          <TextField
            select
            fullWidth
            label="Target Agency"
            value={transferAgency}
            onChange={(e) => setTransferAgency(e.target.value as AgencyType)}
            margin="normal"
          >
            <MenuItem value="FIRE">Ghana National Fire Service</MenuItem>
            <MenuItem value="POLICE">Ghana Police Service</MenuItem>
            <MenuItem value="AMBULANCE">National Ambulance Service</MenuItem>
            <MenuItem value="DISASTER_MANAGEMENT">NADMO</MenuItem>
          </TextField>

          <TextField
            fullWidth
            label="Transfer Reason / Note"
            multiline
            rows={2}
            value={statusNote}
            onChange={(e) => setStatusNote(e.target.value)}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTransferDialogReportId(null)}>Cancel</Button>
          <Button
            variant="contained"
            color="secondary"
            onClick={() =>
              transferDialogReportId &&
              transferMutation.mutate({ id: transferDialogReportId, agency: transferAgency, note: statusNote })
            }
          >
            Transfer
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

import React, { useState } from 'react';
import {
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  FormControlLabel,
  Grid,
  MenuItem,
  Skeleton,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PhoneIcon from '@mui/icons-material/Phone';
import MyLocationIcon from '@mui/icons-material/MyLocation';
import AddIcon from '@mui/icons-material/Add';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { agencyApi } from '@/api/agencyApi';
import { stationApi } from '@/api/stationApi';
import { referenceApi } from '@/api/referenceApi';
import { DistrictRef, RegionRef, Station, StationContact, StationContactType, StationLevel } from '@/types';
import { useAuthStore } from '@/auth/useAuthStore';

export const StationsPage: React.FC = () => {
  const [selectedAgencyId, setSelectedAgencyId] = useState<string>('b1111111-1111-1111-1111-111111111111');
  const [selectedStationId, setSelectedStationId] = useState<string | null>(null);

  const [createStationOpen, setCreateStationOpen] = useState(false);
  const [addContactOpen, setAddContactOpen] = useState(false);

  const [stationName, setStationName] = useState('');
  const [stationCode, setStationCode] = useState('');
  const [stationLevel, setStationLevel] = useState<StationLevel>('DISTRICT_STATION');
  const [selectedRegion, setSelectedRegion] = useState<RegionRef | null>(null);
  const [selectedDistrict, setSelectedDistrict] = useState<DistrictRef | null>(null);
  const [locality, setLocality] = useState('');
  const [latitude, setLatitude] = useState<number>(5.55);
  const [longitude, setLongitude] = useState<number>(-0.2);
  const [accuracy, setAccuracy] = useState<number | undefined>(10);

  const [phoneNumber, setPhoneNumber] = useState('');
  const [contactType, setContactType] = useState<StationContactType>('WATCHROOM');
  const [priority, setPriority] = useState(1);
  const [isPublic, setIsPublic] = useState(true);
  const [is24Hour, setIs24Hour] = useState(true);

  const queryClient = useQueryClient();
  const { hasPermission } = useAuthStore();

  const { data: regions } = useQuery({
    queryKey: ['regions'],
    queryFn: referenceApi.getRegions,
  });

  const { data: districts } = useQuery({
    queryKey: ['districts', selectedRegion?.id],
    queryFn: () => referenceApi.getDistrictsByRegion(selectedRegion!.id),
    enabled: Boolean(selectedRegion?.id),
  });

  const { data: agencies } = useQuery({
    queryKey: ['agencies'],
    queryFn: agencyApi.getAgencies,
  });

  const { data: stationsPage, isLoading: loadingStations } = useQuery({
    queryKey: ['adminStations', 0],
    queryFn: () => stationApi.getAdminStations(0, 20),
  });

  const { data: stationDetails, isLoading: loadingDetails } = useQuery({
    queryKey: ['stationDetails', selectedStationId],
    queryFn: () => stationApi.getStationDetails(selectedStationId!),
    enabled: Boolean(selectedStationId),
  });

  const createStationMutation = useMutation({
    mutationFn: stationApi.createStation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminStations'] });
      setCreateStationOpen(false);
    },
  });

  const verifyStationMutation = useMutation({
    mutationFn: stationApi.verifyStation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminStations'] });
      queryClient.invalidateQueries({ queryKey: ['stationDetails', selectedStationId] });
    },
  });

  const createContactMutation = useMutation({
    mutationFn: ({ stationId, data }: { stationId: string; data: any }) =>
      stationApi.createStationContact(stationId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stationDetails', selectedStationId] });
      setAddContactOpen(false);
    },
  });

  const verifyContactMutation = useMutation({
    mutationFn: stationApi.verifyStationContact,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stationDetails', selectedStationId] });
    },
  });

  const handleCaptureLocation = () => {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLatitude(position.coords.latitude);
          setLongitude(position.coords.longitude);
          setAccuracy(position.coords.accuracy);
        },
        (error) => {
          alert('Unable to capture location: ' + error.message);
        }
      );
    } else {
      alert('Browser geolocation is not supported.');
    }
  };

  const handleOpenCreateStation = () => {
    setStationName('');
    setStationCode('');
    setStationLevel('DISTRICT_STATION');
    setSelectedRegion(null);
    setSelectedDistrict(null);
    setLocality('');
    setLatitude(5.55);
    setLongitude(-0.2);
    setAccuracy(10);
    setCreateStationOpen(true);
  };

  const handleCreateStationSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedRegion) {
      alert('Please select a region.');
      return;
    }
    createStationMutation.mutate({
      agencyId: selectedAgencyId,
      name: stationName,
      code: stationCode,
      region: selectedRegion.name,
      district: selectedDistrict ? selectedDistrict.name : 'Central District',
      locality,
      latitude,
      longitude,
      locationAccuracyMeters: accuracy,
    });
  };

  const handleCreateContactSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedStationId) {
      createContactMutation.mutate({
        stationId: selectedStationId,
        data: {
          phoneNumber,
          type: contactType,
          priority,
          publicContact: isPublic,
          available24Hour: is24Hour,
        },
      });
    }
  };

  const stations = stationsPage?.content || [];
  const station = stationDetails?.station;
  const contacts = stationDetails?.contacts || [];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Station & Contact Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage stations, administrative levels (NHQ, RHQ, District), Ghana regions/districts, and contacts
          </Typography>
        </div>

        {hasPermission('STATION_CREATE') && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenCreateStation}>
            New Station
          </Button>
        )}
      </Box>

      <Card sx={{ mb: 3 }}>
        <CardContent sx={{ p: 2 }}>
          <TextField
            select
            label="Filter by Agency"
            value={selectedAgencyId}
            onChange={(e) => setSelectedAgencyId(e.target.value)}
            fullWidth
            size="small"
          >
            {(agencies || []).map((agency) => (
              <MenuItem key={agency.id} value={agency.id}>
                {agency.name} ({agency.code})
              </MenuItem>
            ))}
          </TextField>
        </CardContent>
      </Card>

      <Grid container spacing={3}>
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent sx={{ p: 0 }}>
              <Box sx={{ p: 2 }}>
                <Typography variant="h6" fontWeight="bold">
                  Stations List
                </Typography>
              </Box>
              <Divider />

              {loadingStations ? (
                <Box sx={{ p: 2 }}><Skeleton variant="rectangular" height={200} /></Box>
              ) : (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Station</TableCell>
                        <TableCell>Level</TableCell>
                        <TableCell>Region</TableCell>
                        <TableCell>Verified</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {stations.map((s) => (
                        <TableRow
                          key={s.id}
                          hover
                          selected={selectedStationId === s.id}
                          sx={{ cursor: 'pointer' }}
                          onClick={() => setSelectedStationId(s.id)}
                        >
                          <TableCell sx={{ fontWeight: 'bold' }}>{s.name}</TableCell>
                          <TableCell>
                            <Chip label={s.stationLevel || 'DISTRICT_STATION'} size="small" variant="outlined" color="primary" />
                          </TableCell>
                          <TableCell>{s.region}</TableCell>
                          <TableCell>
                            {s.verified ? (
                              <CheckCircleIcon color="primary" fontSize="small" />
                            ) : (
                              <Chip label="Unverified" size="small" />
                            )}
                          </TableCell>
                        </TableRow>
                      ))}
                      {stations.length === 0 && (
                        <TableRow>
                          <TableCell colSpan={4} align="center" sx={{ py: 3 }}>
                            No stations found.
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={7}>
          {selectedStationId ? (
            loadingDetails ? (
              <Skeleton variant="rectangular" height={300} />
            ) : station ? (
              <Box>
                <Card sx={{ mb: 3 }}>
                  <CardContent sx={{ p: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h5" fontWeight="bold">
                        {station.name}
                      </Typography>
                      <Box sx={{ display: 'flex', gap: 1 }}>
                        {!station.verified && hasPermission('STATION_VERIFY') && (
                          <Button
                            size="small"
                            variant="outlined"
                            color="success"
                            startIcon={<VerifiedUserIcon />}
                            onClick={() => verifyStationMutation.mutate(station.id)}
                          >
                            Verify Station
                          </Button>
                        )}
                      </Box>
                    </Box>

                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                      Code: <strong>{station.code}</strong> | Level: <strong>{station.stationLevel || 'DISTRICT_STATION'}</strong> | Region: <strong>{station.region}</strong> | District: <strong>{station.district}</strong>
                    </Typography>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                      <LocationOnIcon color="primary" />
                      <Typography variant="body2" fontWeight="bold">
                        Coordinates: {station.latitude}, {station.longitude} (Accuracy ±{station.locationAccuracyMeters || 10}m)
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>

                {/* Station Contacts Section */}
                <Card>
                  <CardContent sx={{ p: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6" fontWeight="bold">
                        Multiple Station Contacts
                      </Typography>
                      {hasPermission('STATION_CONTACT_CREATE') && (
                        <Button
                          size="small"
                          variant="contained"
                          startIcon={<AddIcon />}
                          onClick={() => {
                            setPhoneNumber('');
                            setContactType('WATCHROOM');
                            setPriority(1);
                            setIsPublic(true);
                            setIs24Hour(true);
                            setAddContactOpen(true);
                          }}
                        >
                          Add Contact
                        </Button>
                      )}
                    </Box>
                    <Divider sx={{ mb: 2 }} />

                    <TableContainer>
                      <Table size="small">
                        <TableHead>
                          <TableRow>
                            <TableCell>Priority</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Phone Number</TableCell>
                            <TableCell>24/7</TableCell>
                            <TableCell>Public</TableCell>
                            <TableCell>Verified</TableCell>
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {contacts.map((c) => (
                            <TableRow key={c.id}>
                              <TableCell sx={{ fontWeight: 'bold' }}>#{c.priority}</TableCell>
                              <TableCell><Chip label={c.type} size="small" variant="outlined" /></TableCell>
                              <TableCell sx={{ fontWeight: 'bold' }}>
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                  <PhoneIcon fontSize="small" color="primary" />
                                  {c.phoneNumber}
                                </Box>
                              </TableCell>
                              <TableCell>{c.is24Hour ? <Chip label="24/7" size="small" color="success" /> : 'No'}</TableCell>
                              <TableCell>{c.active ? <Chip label="Public" size="small" color="primary" /> : 'Internal'}</TableCell>
                              <TableCell>
                                {c.verified ? (
                                  <CheckCircleIcon color="primary" fontSize="small" />
                                ) : hasPermission('STATION_CONTACT_VERIFY') ? (
                                  <Button size="small" onClick={() => verifyContactMutation.mutate(c.id)}>Verify</Button>
                                ) : (
                                  'Unverified'
                                )}
                              </TableCell>
                            </TableRow>
                          ))}
                          {contacts.length === 0 && (
                            <TableRow>
                              <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                                No contacts assigned to this station yet.
                              </TableCell>
                            </TableRow>
                          )}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  </CardContent>
                </Card>
              </Box>
            ) : null
          ) : (
            <Card sx={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', p: 4 }}>
              <Typography color="text.secondary">
                Select a station from the list to manage details and contact numbers.
              </Typography>
            </Card>
          )}
        </Grid>
      </Grid>

      {/* Create Station Dialog */}
      <Dialog open={createStationOpen} onClose={() => setCreateStationOpen(false)} maxWidth="sm" fullWidth>
        <form onSubmit={handleCreateStationSubmit}>
          <DialogTitle>Create New Emergency Station</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Station Code (Unique)"
              value={stationCode}
              onChange={(e) => setStationCode(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Station Name"
              value={stationName}
              onChange={(e) => setStationName(e.target.value)}
              margin="normal"
              required
            />

            <TextField
              select
              fullWidth
              label="Administrative Station Level"
              value={stationLevel}
              onChange={(e) => setStationLevel(e.target.value as StationLevel)}
              margin="normal"
              required
            >
              <MenuItem value="NHQ">National Headquarters (NHQ)</MenuItem>
              <MenuItem value="RHQ">Regional Headquarters (RHQ)</MenuItem>
              <MenuItem value="DISTRICT_STATION">District Station</MenuItem>
            </TextField>

            <Grid container spacing={2} sx={{ mt: 0.5 }}>
              <Grid item xs={6}>
                <Autocomplete
                  options={regions || []}
                  getOptionLabel={(option) => option.name}
                  value={selectedRegion}
                  onChange={(_, newValue) => {
                    setSelectedRegion(newValue);
                    setSelectedDistrict(null);
                  }}
                  renderInput={(params) => <TextField {...params} label="Region *" required />}
                />
              </Grid>
              <Grid item xs={6}>
                <Autocomplete
                  options={districts || []}
                  getOptionLabel={(option) => option.name}
                  value={selectedDistrict}
                  onChange={(_, newValue) => setSelectedDistrict(newValue)}
                  disabled={!selectedRegion}
                  renderInput={(params) => <TextField {...params} label="District *" required={stationLevel === 'DISTRICT_STATION'} />}
                />
              </Grid>
            </Grid>

            <TextField
              fullWidth
              label="Locality / Town"
              value={locality}
              onChange={(e) => setLocality(e.target.value)}
              margin="normal"
            />

            <Box sx={{ mt: 2, p: 2, border: '1px dashed', borderColor: 'divider', borderRadius: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle2" fontWeight="bold">
                  Geographic Coordinates
                </Typography>
                <Button
                  size="small"
                  variant="outlined"
                  startIcon={<MyLocationIcon />}
                  onClick={handleCaptureLocation}
                >
                  Use Current Location
                </Button>
              </Box>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Latitude"
                    type="number"
                    value={latitude}
                    onChange={(e) => setLatitude(parseFloat(e.target.value))}
                    size="small"
                    required
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label="Longitude"
                    type="number"
                    value={longitude}
                    onChange={(e) => setLongitude(parseFloat(e.target.value))}
                    size="small"
                    required
                  />
                </Grid>
              </Grid>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCreateStationOpen(false)}>Cancel</Button>
            <Button type="submit" variant="contained">
              Create Station
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Add Contact Dialog */}
      <Dialog open={addContactOpen} onClose={() => setAddContactOpen(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleCreateContactSubmit}>
          <DialogTitle>Add Station Contact Number</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Phone Number"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              select
              fullWidth
              label="Contact Type"
              value={contactType}
              onChange={(e) => setContactType(e.target.value as StationContactType)}
              margin="normal"
              required
            >
              <MenuItem value="WATCHROOM">Watchroom</MenuItem>
              <MenuItem value="CONTROL_ROOM">Control Room</MenuItem>
              <MenuItem value="EMERGENCY">Emergency Direct</MenuItem>
              <MenuItem value="ALTERNATIVE">Alternative Line</MenuItem>
              <MenuItem value="ADMINISTRATIVE">Administrative</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </TextField>
            <TextField
              fullWidth
              label="Priority (e.g. 1 = Preferred)"
              type="number"
              value={priority}
              onChange={(e) => setPriority(parseInt(e.target.value) || 1)}
              margin="normal"
              required
            />
            <FormControlLabel
              control={<Switch checked={isPublic} onChange={(e) => setIsPublic(e.target.checked)} />}
              label="Publicly Visible"
            />
            <FormControlLabel
              control={<Switch checked={is24Hour} onChange={(e) => setIs24Hour(e.target.checked)} />}
              label="Available 24/7"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setAddContactOpen(false)}>Cancel</Button>
            <Button type="submit" variant="contained">
              Add Contact
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

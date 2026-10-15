import React, { useState } from 'react';
import { Box, ButtonGroup, Button, Card, CardContent, Skeleton, Typography } from '@mui/material';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { useQuery } from '@tanstack/react-query';
import { dashboardApi } from '@/api/dashboardApi';

export const EmergencyActivityChart: React.FC = () => {
  const [period, setPeriod] = useState<'24H' | '7D' | '30D' | '12M'>('7D');

  const { data: activityData, isLoading } = useQuery({
    queryKey: ['dashboardActivity', period],
    queryFn: () => dashboardApi.getActivity(period),
  });

  return (
    <Card sx={{ mb: 4 }}>
      <CardContent sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
          <div>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Emergency Activity Trend
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Real-time emergency incident volume over time
            </Typography>
          </div>

          <ButtonGroup size="small" variant="outlined">
            <Button
              variant={period === '24H' ? 'contained' : 'outlined'}
              onClick={() => setPeriod('24H')}
            >
              Today
            </Button>
            <Button
              variant={period === '7D' ? 'contained' : 'outlined'}
              onClick={() => setPeriod('7D')}
            >
              7 Days
            </Button>
            <Button
              variant={period === '30D' ? 'contained' : 'outlined'}
              onClick={() => setPeriod('30D')}
            >
              30 Days
            </Button>
            <Button
              variant={period === '12M' ? 'contained' : 'outlined'}
              onClick={() => setPeriod('12M')}
            >
              12 Months
            </Button>
          </ButtonGroup>
        </Box>

        {isLoading ? (
          <Skeleton variant="rectangular" height={280} />
        ) : (
          <Box sx={{ width: '100%', height: 280 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={activityData || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="label" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Line
                  type="monotone"
                  dataKey="incidentCount"
                  name="Incidents"
                  stroke="#003366"
                  strokeWidth={3}
                  dot={{ r: 4 }}
                  activeDot={{ r: 6 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

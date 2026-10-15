import React, { useState } from 'react';
import {
  AppBar,
  Avatar,
  Box,
  Chip,
  CssBaseline,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  ListSubheader,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DashboardIcon from '@mui/icons-material/Dashboard';
import BusinessIcon from '@mui/icons-material/Business';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import CategoryIcon from '@mui/icons-material/Category';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';
import PeopleIcon from '@mui/icons-material/People';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import HistoryIcon from '@mui/icons-material/History';
import SettingsIcon from '@mui/icons-material/Settings';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/auth/useAuthStore';
import { authApi } from '@/api/authApi';

const DRAWER_WIDTH = 260;

interface AdminLayoutProps {
  toggleTheme: () => void;
  currentMode: 'light' | 'dark';
}

export const AdminLayout: React.FC<AdminLayoutProps> = ({ toggleTheme, currentMode }) => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();

  const { user, roles, logout, hasPermission } = useAuthStore();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    handleMenuClose();
    try {
      await authApi.logout();
    } catch (e) {
      // ignore logout error
    } finally {
      logout();
      navigate('/login');
    }
  };

  const navGroups = [
    {
      subheader: 'Overview',
      items: [{ label: 'Dashboard', icon: <DashboardIcon />, path: '/', permission: 'REPORT_READ' }],
    },
    {
      subheader: 'Operations',
      items: [{ label: 'Incident Reports', icon: <ReportProblemIcon />, path: '/reports', permission: 'REPORT_READ' }],
    },
    {
      subheader: 'Emergency Network',
      items: [
        { label: 'Agencies', icon: <BusinessIcon />, path: '/agencies', permission: 'AGENCY_READ' },
        { label: 'Stations & Contacts', icon: <LocalOfferIcon />, path: '/stations', permission: 'STATION_READ' },
        { label: 'Incident Categories', icon: <CategoryIcon />, path: '/incident-categories', permission: 'INCIDENT_CATEGORY_READ' },
      ],
    },
    {
      subheader: 'Administration',
      items: [
        { label: 'Admin Users', icon: <PeopleIcon />, path: '/users', permission: 'ADMIN_USER_READ' },
        { label: 'Temporary Credentials', icon: <VpnKeyIcon />, path: '/temporary-credentials', permission: 'ADMIN_USER_READ' },
        { label: 'Admin Requests', icon: <AdminPanelSettingsIcon />, path: '/admin-requests', permission: 'ADMIN_USER_READ' },
        { label: 'Roles & Permissions', icon: <AdminPanelSettingsIcon />, path: '/roles', permission: 'ROLE_READ' },
        { label: 'Audit Logs', icon: <HistoryIcon />, path: '/audit-logs', permission: 'AUDIT_LOG_READ' },
      ],
    },
    {
      subheader: 'System',
      items: [{ label: 'System Settings', icon: <SettingsIcon />, path: '/settings', permission: 'SYSTEM_SETTINGS_MANAGE' }],
    },
  ];

  const logoSrc = currentMode === 'dark' ? '/brand/gec-logo-dark.png' : '/brand/gec-logo-transparent.png';

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Toolbar sx={{ px: 2 }}>
        <Box
          component="img"
          src={logoSrc}
          alt="Ghana Emergency Connect"
          sx={{ maxHeight: 40, maxWidth: '100%', objectFit: 'contain' }}
        />
      </Toolbar>
      <Divider />
      <Box sx={{ flexGrow: 1, overflowY: 'auto', py: 1 }}>
        {navGroups.map((group) => {
          const visibleItems = group.items.filter((item) => hasPermission(item.permission));
          if (visibleItems.length === 0) return null;

          return (
            <List key={group.subheader} subheader={<ListSubheader sx={{ bgcolor: 'transparent', fontWeight: 'bold' }}>{group.subheader}</ListSubheader>}>
              {visibleItems.map((item) => {
                const isSelected = location.pathname === item.path;
                return (
                  <ListItem key={item.path} disablePadding sx={{ mb: 0.5, px: 1 }}>
                    <ListItemButton
                      selected={isSelected}
                      onClick={() => {
                        navigate(item.path);
                        if (isMobile) setMobileOpen(false);
                      }}
                      sx={{
                        borderRadius: 2,
                        '&.Mui-selected': {
                          backgroundColor: 'primary.main',
                          color: 'primary.contrastText',
                          '& .MuiListItemIcon-root': {
                            color: 'primary.contrastText',
                          },
                        },
                      }}
                    >
                      <ListItemIcon sx={{ minWidth: 40, color: isSelected ? 'inherit' : 'text.secondary' }}>
                        {item.icon}
                      </ListItemIcon>
                      <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: isSelected ? 600 : 400 }} />
                    </ListItemButton>
                  </ListItem>
                );
              })}
            </List>
          );
        })}
      </Box>
      <Divider />
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Typography variant="caption" color="text.secondary">
          Ghana Emergency Connect v1.0
        </Typography>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { md: `${DRAWER_WIDTH}px` },
          boxShadow: 1,
          backgroundColor: 'background.paper',
          color: 'text.primary',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="subtitle1" fontWeight="bold" sx={{ flexGrow: 1 }}>
            Admin Portal
          </Typography>

          <IconButton onClick={toggleTheme} color="inherit" sx={{ mr: 1 }}>
            {currentMode === 'dark' ? <LightModeIcon /> : <DarkModeIcon />}
          </IconButton>

          {user && (
            <Chip
              label={roles[0] || 'User'}
              color="primary"
              size="small"
              variant="outlined"
              sx={{ mr: 2, display: { xs: 'none', sm: 'inline-flex' } }}
            />
          )}

          <IconButton onClick={handleMenuOpen} size="small">
            <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.main' }}>
              {user?.firstName?.[0] || 'A'}
            </Avatar>
          </IconButton>

          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          >
            <MenuItem onClick={() => { handleMenuClose(); navigate('/profile'); }}>
              <ListItemIcon><PersonIcon fontSize="small" /></ListItemIcon>
              Profile & Password
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout} sx={{ color: 'error.main' }}>
              <ListItemIcon><LogoutIcon fontSize="small" color="error" /></ListItemIcon>
              Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
        >
          {drawerContent}
        </Drawer>

        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
          open
        >
          {drawerContent}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          minHeight: '100vh',
          backgroundColor: 'background.default',
          mt: 8,
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
};

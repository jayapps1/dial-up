import { create } from 'zustand';
import { AdminUser } from '@/types';

interface AuthState {
  accessToken: string | null;
  user: AdminUser | null;
  roles: string[];
  permissions: string[];
  isAuthenticated: boolean;
  isInitializing: boolean;
  setAuth: (token: string, user: AdminUser, roles?: string[], permissions?: string[]) => void;
  setInitializing: (loading: boolean) => void;
  logout: () => void;
  hasPermission: (permission: string) => boolean;
  hasRole: (role: string) => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  user: null,
  roles: [],
  permissions: [],
  isAuthenticated: false,
  isInitializing: true,

  setAuth: (token, user, roles = [], permissions = []) => {
    set({
      accessToken: token,
      user,
      roles: roles.length > 0 ? roles : user.roles || [],
      permissions: permissions.length > 0 ? permissions : [],
      isAuthenticated: true,
      isInitializing: false,
    });
  },

  setInitializing: (loading) => {
    set({ isInitializing: loading });
  },

  logout: () => {
    set({
      accessToken: null,
      user: null,
      roles: [],
      permissions: [],
      isAuthenticated: false,
      isInitializing: false,
    });
  },

  hasPermission: (permission) => {
    const { permissions, roles } = get();
    if (roles.includes('SYSTEM_ADMIN')) return true;
    return permissions.includes(permission);
  },

  hasRole: (role) => {
    const { roles } = get();
    return roles.includes(role) || roles.includes('SYSTEM_ADMIN');
  },
}));

# Ghana Emergency Connect — Web Admin Portal

A responsive, role-aware Web Admin Portal for **Ghana Emergency Connect**, built with React 18, TypeScript, Vite, Material UI, TanStack Query, and Zustand.

---

## Technical Stack

- **Framework:** React 18 / 19 with TypeScript & Vite
- **UI System:** Material UI (`@mui/material`, `@mui/icons-material`)
- **Routing:** React Router v6 (`react-router-dom`)
- **State Management:**
  - **Server State:** TanStack React Query (`@tanstack/react-query`)
  - **Auth State:** Zustand (`zustand` in-memory store)
- **Forms & Validation:** React Hook Form (`react-hook-form`) + Zod (`zod`)
- **HTTP Client:** Axios (`axios`) with `withCredentials: true`

---

## Environment Configuration

Copy `.env.example` to `.env.development`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

> [!IMPORTANT]
> The frontend never stores access tokens or passwords in `localStorage`. Access tokens remain held in memory, and refresh tokens are handled via HttpOnly `gec_refresh_token` cookies.

---

## Key Features

- **Role-Aware Navigation & Guards**:
  - `ProtectedRoute`: Guarantees user is authenticated.
  - `PermissionRoute`: Enforces fine-grained permission checks (`STATION_READ`, `REPORT_READ`, etc.) displaying a 403 Access Denied screen if unauthorized.
- **Role-Aware Dashboard**:
  - System Admin view (platform-wide stats).
  - Agency Admin view (agency-scoped stats).
  - Control Room Operator view (incident queue).
- **Incident Reports Management**:
  - Operational incoming reports table with status badges (`SUBMITTED`, `ACKNOWLEDGED`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `REJECTED`, `TRANSFERRED`).
  - Actions: **Acknowledge**, **Assign Station**, **Update Status**, and **Transfer Agency**.
  - Report details with map links, vehicle details, target agencies, and status history timeline.
- **Emergency Stations & GPS Capture**:
  - Multiple station contacts per station with priority sorting, public/private badges, and 24/7 indicators.
  - *"Use Current Location"* button (`navigator.geolocation`) requiring explicit user action.
- **Dark & Light Mode Theme**:
  - Material UI appearance mode toggle with persistent setting.

---

## Development & Build Instructions

1. Install dependencies:
```bash
npm install
```

2. Run development server:
```bash
npm run dev
```

3. Build production bundle:
```bash
npm run build
```

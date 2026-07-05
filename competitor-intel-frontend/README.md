# AI-Powered Competitor Intelligence Platform — Frontend

Enterprise React 18 frontend for the AI-Powered Competitor Intelligence Platform.
Fully integrated with the Spring Boot 3 REST API backend.

---

## Screenshots

> _Add screenshots here after running the application_

| Dashboard | Competitors | Events |
|-----------|-------------|--------|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Competitors](docs/screenshots/competitors.png) | ![Events](docs/screenshots/events.png) |

---

## Tech Stack

| Concern | Technology |
|---|---|
| Framework | React 18 |
| Build Tool | Vite 5 |
| Language | TypeScript 5 |
| Styling | Tailwind CSS 3 |
| Routing | React Router DOM 6 |
| State / Cache | TanStack React Query 5 |
| HTTP Client | Axios 1.7 |
| Forms | React Hook Form 7 + Zod |
| Charts | Recharts 2 |
| Animations | Framer Motion 11 |
| Icons | Lucide React |

---

## Project Structure

```
src/
├── components/
│   ├── dashboard/        # KPI cards, charts, widgets
│   ├── layout/           # Sidebar, TopNav, MainLayout, MobileDrawer
│   └── ui/               # Button, Input, Card, Modal, Table, Badge, etc.
├── context/
│   ├── AuthContext.tsx   # JWT auth state, login/logout
│   └── ThemeContext.tsx  # Dark/light mode
├── hooks/                # React Query hooks per domain
│   ├── useAlerts.ts
│   ├── useCompetitors.ts
│   ├── useDashboard.ts
│   ├── useEvents.ts
│   ├── useProfile.ts
│   ├── useReports.ts
│   └── useSources.ts
├── pages/                # One file per route (lazy-loaded)
│   ├── LoginPage.tsx
│   ├── DashboardPage.tsx
│   ├── CompetitorsPage.tsx
│   ├── EventsPage.tsx
│   ├── SourcesPage.tsx
│   ├── AlertsPage.tsx
│   ├── ReportsPage.tsx
│   ├── SettingsPage.tsx
│   ├── ProfilePage.tsx
│   └── NotFoundPage.tsx
├── routes/
│   ├── AppRoutes.tsx     # Lazy route definitions + Suspense
│   └── ProtectedRoute.tsx
├── services/             # One service per backend module
│   ├── api.ts            # Axios instance + interceptors + error util
│   ├── auth.service.ts
│   ├── competitor.service.ts
│   ├── intelligence.service.ts
│   ├── source.service.ts
│   ├── alert.service.ts
│   ├── dashboard.service.ts
│   ├── report.service.ts
│   └── user.service.ts
├── types/
│   └── index.ts          # All TypeScript interfaces matching backend DTOs
└── utils/
    ├── cn.ts             # Tailwind class merger
    └── formatters.ts     # Date, number, byte formatters
```

---

## Prerequisites

- Node.js 18+
- npm 9+
- Spring Boot backend running on `http://localhost:8080`

---

## Installation

```bash
# Clone the repository
git clone <repo-url>
cd competitor-intel-frontend

# Install dependencies
npm install

# Copy environment template
cp .env.example .env
# Edit .env and set VITE_API_BASE_URL to your backend URL
```

---

## Environment Variables

Copy `.env.example` to `.env` and set:

| Variable | Description | Default |
|---|---|---|
| `VITE_API_BASE_URL` | Spring Boot backend base URL | `http://localhost:8080` |
| `VITE_API_TIMEOUT` | Axios request timeout (ms) | `15000` |

> **.env is gitignored** — never commit it. Use `.env.example` as the template.

---

## Running the App

```bash
# Development (hot reload)
npm run dev
# → http://localhost:5173

# Production build
npm run build

# Preview production build locally
npm run preview
```

---

## API Integration

All API calls go through `src/services/api.ts` — a single Axios instance that:

- Reads `VITE_API_BASE_URL` from the environment
- Automatically attaches `Authorization: Bearer <token>` from `localStorage`
- Intercepts `401` responses → clears session → redirects to `/login`
- Normalises errors via `extractApiError()`

### Service Modules

| File | Backend Endpoints |
|---|---|
| `auth.service.ts` | `POST /api/v1/auth/login`, `/auth/refresh` |
| `competitor.service.ts` | `GET/POST/PUT/DELETE /api/v1/competitors` |
| `intelligence.service.ts` | `GET/POST /api/v1/events` |
| `source.service.ts` | `GET/POST/PUT/DELETE /api/v1/sources` |
| `alert.service.ts` | `GET/POST/DELETE /api/v1/alerts/rules`, `/alerts/notifications` |
| `dashboard.service.ts` | `GET /api/v1/dashboard/stats` |
| `report.service.ts` | `GET /api/v1/reports`, export endpoints |
| `user.service.ts` | `GET/PUT /api/v1/users/me`, `/users/{id}` |

---

## Authentication

- JWT stored in `localStorage` (key: `ci_token`)
- `AuthContext` restores session on page reload
- `ProtectedRoute` redirects unauthenticated users to `/login`
- Login page sends credentials to `POST /api/v1/auth/login`

**Default credentials (dev seed data):**

| Username | Password | Role |
|---|---|---|
| admin | Admin123! | ROLE_ADMIN |
| analyst | Analyst123! | ROLE_ANALYST |
| viewer | Viewer123! | ROLE_VIEWER |

---

## Features

- **Dashboard** — KPI cards, category bar chart, sentiment donut, trend area chart, top competitors, recent events table, alerts widget
- **Competitors** — Card grid with search, status/industry filter, pagination, create/edit modal, archive/delete
- **Intelligence Events** — Table with category/sentiment/status filters, pagination, AI enrich action, flag/unflag
- **Sources** — Table with scrape trigger, create modal, delete
- **Alerts** — Notifications tab (acknowledge / acknowledge-all) + Rules tab (create, enable/disable, delete)
- **Reports** — List with CSV/PDF export download, delete
- **Profile** — Edit name/email, change password
- **Settings** — Theme toggle, notification preferences
- **Dark / Light Mode** — Persisted in localStorage
- **Responsive** — Mobile sidebar drawer

---

## Building for Production

```bash
npm run build
# Output in dist/ — serve with any static file server or nginx
```

---

## License

Enterprise License — © 2024 Competitor Intelligence Platform

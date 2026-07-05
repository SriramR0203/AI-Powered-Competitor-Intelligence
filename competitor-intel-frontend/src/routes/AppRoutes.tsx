import { lazy, Suspense } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { MainLayout }     from '../components/layout/MainLayout'
import { PageSpinner }    from '../components/ui/Spinner'

// Lazy-loaded pages for code splitting
const LoginPage       = lazy(() => import('../pages/LoginPage').then(m => ({ default: m.LoginPage })))
const DashboardPage   = lazy(() => import('../pages/DashboardPage').then(m => ({ default: m.DashboardPage })))
const CompetitorsPage = lazy(() => import('../pages/CompetitorsPage').then(m => ({ default: m.CompetitorsPage })))
const EventsPage      = lazy(() => import('../pages/EventsPage').then(m => ({ default: m.EventsPage })))
const SourcesPage     = lazy(() => import('../pages/SourcesPage').then(m => ({ default: m.SourcesPage })))
const AlertsPage      = lazy(() => import('../pages/AlertsPage').then(m => ({ default: m.AlertsPage })))
const ReportsPage     = lazy(() => import('../pages/ReportsPage').then(m => ({ default: m.ReportsPage })))
const SettingsPage    = lazy(() => import('../pages/SettingsPage').then(m => ({ default: m.SettingsPage })))
const ProfilePage     = lazy(() => import('../pages/ProfilePage').then(m => ({ default: m.ProfilePage })))
const NotFoundPage    = lazy(() => import('../pages/NotFoundPage').then(m => ({ default: m.NotFoundPage })))

export function AppRoutes() {
  return (
    <Suspense fallback={<PageSpinner />}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard"   element={<DashboardPage />} />
            <Route path="/competitors" element={<CompetitorsPage />} />
            <Route path="/events"      element={<EventsPage />} />
            <Route path="/sources"     element={<SourcesPage />} />
            <Route path="/alerts"      element={<AlertsPage />} />
            <Route path="/reports"     element={<ReportsPage />} />
            <Route path="/settings"    element={<SettingsPage />} />
            <Route path="/profile"     element={<ProfilePage />} />
          </Route>
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  )
}

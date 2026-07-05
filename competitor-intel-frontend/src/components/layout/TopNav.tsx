import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Bell, Sun, Moon, Menu } from 'lucide-react'
import { useTheme } from '../../context/ThemeContext'
import { useAuth } from '../../context/AuthContext'
import { useUnreadCount } from '../../hooks/useAlerts'
import { Avatar } from '../ui/Avatar'

const ROUTE_LABELS: Record<string, string> = {
  '/dashboard':   'Dashboard',
  '/competitors': 'Competitors',
  '/events':      'Intelligence Events',
  '/sources':     'Sources',
  '/alerts':      'Alerts',
  '/reports':     'Reports',
  '/settings':    'Settings',
  '/profile':     'Profile',
}

interface TopNavProps {
  onMenuClick: () => void
}

export function TopNav({ onMenuClick }: TopNavProps) {
  const { theme, toggleTheme } = useTheme()
  const { user, logout }       = useAuth()
  const { data: unread = 0 }   = useUnreadCount()
  const navigate               = useNavigate()
  const location               = useLocation()
  const [menuOpen, setMenuOpen] = useState(false)

  const pageTitle = ROUTE_LABELS[location.pathname] ?? 'Competitor Intelligence'

  return (
    <header className="h-16 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800 flex items-center px-4 gap-3 flex-shrink-0">
      {/* Mobile menu trigger */}
      <button
        onClick={onMenuClick}
        aria-label="Open sidebar"
        className="p-2 rounded-xl text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors lg:hidden"
      >
        <Menu size={20} />
      </button>

      {/* Page title */}
      <h1 className="text-base font-semibold text-gray-900 dark:text-white hidden sm:block">
        {pageTitle}
      </h1>

      <div className="flex items-center gap-1 ml-auto">
        {/* Theme toggle */}
        <button
          onClick={toggleTheme}
          aria-label="Toggle theme"
          className="p-2 rounded-xl text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
        >
          {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        {/* Notification bell */}
        <button
          onClick={() => navigate('/alerts')}
          aria-label={`Alerts${unread > 0 ? ` — ${unread} unread` : ''}`}
          className="relative p-2 rounded-xl text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
        >
          <Bell size={18} />
          {unread > 0 && (
            <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center leading-none">
              {unread > 9 ? '9+' : unread}
            </span>
          )}
        </button>

        {/* User menu */}
        <div className="relative ml-1">
          <button
            onClick={() => setMenuOpen(v => !v)}
            aria-label="User menu"
            aria-expanded={menuOpen}
            className="flex items-center gap-2 p-1.5 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          >
            <Avatar name={user?.fullName ?? 'User'} size="sm" />
          </button>

          {menuOpen && (
            <>
              {/* Click-outside backdrop */}
              <div
                className="fixed inset-0 z-30"
                aria-hidden="true"
                onClick={() => setMenuOpen(false)}
              />

              <div className="absolute right-0 top-full mt-1 w-48 z-40 bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-xl py-1 overflow-hidden">
                {/* User info header */}
                <div className="px-4 py-2 border-b border-gray-100 dark:border-gray-700">
                  <p className="text-sm font-semibold text-gray-900 dark:text-white truncate">
                    {user?.fullName}
                  </p>
                  <p className="text-xs text-gray-500 truncate">{user?.email}</p>
                </div>

                {/* Nav links */}
                {[
                  { label: 'Profile',  href: '/profile' },
                  { label: 'Settings', href: '/settings' },
                ].map(({ label, href }) => (
                  <button
                    key={href}
                    onClick={() => { navigate(href); setMenuOpen(false) }}
                    className="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                  >
                    {label}
                  </button>
                ))}

                {/* Sign out */}
                <div className="border-t border-gray-100 dark:border-gray-700 mt-1">
                  <button
                    onClick={logout}
                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                  >
                    Sign out
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </header>
  )
}

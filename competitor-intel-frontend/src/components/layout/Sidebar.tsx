import { NavLink } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  LayoutDashboard, Building2, Newspaper, Radio, Bell,
  FileText, Settings, TrendingUp, LogOut, ChevronLeft, ChevronRight,
} from 'lucide-react'
import { useState } from 'react'
import { cn } from '../../utils/cn'
import { Avatar } from '../ui/Avatar'
import { useAuth } from '../../context/AuthContext'

const NAV_ITEMS = [
  { to: '/dashboard',   icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/competitors', icon: Building2,        label: 'Competitors' },
  { to: '/events',      icon: Newspaper,        label: 'Events' },
  { to: '/sources',     icon: Radio,            label: 'Sources' },
  { to: '/alerts',      icon: Bell,             label: 'Alerts' },
  { to: '/reports',     icon: FileText,         label: 'Reports' },
]

const BOTTOM_ITEMS = [
  { to: '/settings', icon: Settings, label: 'Settings' },
]

interface SidebarProps {
  collapsed:  boolean
  onToggle:   () => void
  onClose?:   () => void
  isMobile?:  boolean
}

export function Sidebar({ collapsed, onToggle, onClose, isMobile = false }: SidebarProps) {
  const { user, logout } = useAuth()
  const isCollapsed = collapsed && !isMobile

  return (
    <motion.aside
      animate={{ width: isCollapsed ? 72 : 256 }}
      transition={{ type: 'spring', stiffness: 300, damping: 30 }}
      className="h-full bg-gray-900 dark:bg-gray-950 flex flex-col overflow-hidden"
    >
      {/* Logo row */}
      <div className={cn(
        'flex items-center h-16 px-4 border-b border-gray-800 flex-shrink-0',
        isCollapsed ? 'justify-center' : 'gap-3',
      )}>
        <div className="w-8 h-8 rounded-xl bg-brand-600 flex items-center justify-center flex-shrink-0">
          <TrendingUp size={16} className="text-white" />
        </div>

        {!isCollapsed && (
          <motion.span
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-sm font-bold text-white truncate"
          >
            Competitor Intel
          </motion.span>
        )}

        {!isMobile && (
          <button
            onClick={onToggle}
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            className="ml-auto p-1 rounded-lg text-gray-400 hover:text-white hover:bg-gray-800 transition-colors"
          >
            {collapsed ? <ChevronRight size={14} /> : <ChevronLeft size={14} />}
          </button>
        )}

        {isMobile && (
          <button
            onClick={onClose}
            aria-label="Close sidebar"
            className="ml-auto p-1 rounded-lg text-gray-400 hover:text-white"
          >
            <ChevronLeft size={16} />
          </button>
        )}
      </div>

      {/* Primary navigation */}
      <nav aria-label="Main navigation" className="flex-1 py-4 px-3 space-y-0.5 overflow-y-auto">
        {NAV_ITEMS.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            onClick={isMobile ? onClose : undefined}
            className={({ isActive }) => cn(
              'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
              isActive
                ? 'bg-brand-600 text-white shadow-sm'
                : 'text-gray-400 hover:text-white hover:bg-gray-800',
              isCollapsed && 'justify-center',
            )}
          >
            <Icon size={18} className="flex-shrink-0" />
            {!isCollapsed && <span className="truncate">{label}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Bottom: settings + logout + user info */}
      <div className="px-3 py-3 border-t border-gray-800 space-y-0.5">
        {BOTTOM_ITEMS.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            onClick={isMobile ? onClose : undefined}
            className={({ isActive }) => cn(
              'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all',
              isActive
                ? 'bg-brand-600 text-white'
                : 'text-gray-400 hover:text-white hover:bg-gray-800',
              isCollapsed && 'justify-center',
            )}
          >
            <Icon size={18} className="flex-shrink-0" />
            {!isCollapsed && <span>{label}</span>}
          </NavLink>
        ))}

        <button
          onClick={logout}
          aria-label="Sign out"
          className={cn(
            'w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium',
            'text-gray-400 hover:text-red-400 hover:bg-gray-800 transition-all',
            isCollapsed && 'justify-center',
          )}
        >
          <LogOut size={18} className="flex-shrink-0" />
          {!isCollapsed && <span>Sign Out</span>}
        </button>

        {!isCollapsed && user && (
          <div className="flex items-center gap-3 px-3 py-2.5 mt-1">
            <Avatar name={user.fullName} size="sm" />
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-white truncate">{user.fullName}</p>
              <p className="text-xs text-gray-500 truncate">{user.email}</p>
            </div>
          </div>
        )}
      </div>
    </motion.aside>
  )
}

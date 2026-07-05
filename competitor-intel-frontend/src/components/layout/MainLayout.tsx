import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Sidebar } from './Sidebar'
import { TopNav } from './TopNav'
import { MobileDrawer } from './MobileDrawer'

export function MainLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const [drawerOpen, setDrawerOpen] = useState(false)

  return (
    <div className="flex h-screen bg-gray-50 dark:bg-gray-900 overflow-hidden">
      {/* Desktop sidebar */}
      <div className="hidden lg:flex flex-shrink-0">
        <Sidebar collapsed={collapsed} onToggle={() => setCollapsed(v => !v)} />
      </div>

      {/* Mobile drawer */}
      <MobileDrawer open={drawerOpen} onClose={() => setDrawerOpen(false)} />

      {/* Main area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopNav onMenuClick={() => setDrawerOpen(true)} />
        <motion.main
          key="main"
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.25 }}
          className="flex-1 overflow-y-auto px-4 py-5 sm:px-6"
        >
          <Outlet />
        </motion.main>
      </div>
    </div>
  )
}

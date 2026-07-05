import { AnimatePresence, motion } from 'framer-motion'
import { Sidebar } from './Sidebar'

interface MobileDrawerProps { open: boolean; onClose: () => void }

export function MobileDrawer({ open, onClose }: MobileDrawerProps) {
  return (
    <AnimatePresence>
      {open && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <motion.div
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="absolute inset-0 bg-black/60 backdrop-blur-sm"
            onClick={onClose}
          />
          <motion.div
            initial={{ x: -280 }} animate={{ x: 0 }} exit={{ x: -280 }}
            transition={{ type: 'spring', stiffness: 350, damping: 35 }}
            className="absolute inset-y-0 left-0 w-64 shadow-2xl"
          >
            <Sidebar collapsed={false} onToggle={onClose} onClose={onClose} isMobile />
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  )
}

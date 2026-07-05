import { motion } from 'framer-motion'
import type { LucideIcon } from 'lucide-react'
import { cn } from '../../utils/cn'

interface KpiCardProps {
  title: string
  value: string | number
  change?: string
  changeType?: 'up' | 'down' | 'neutral'
  icon: LucideIcon
  iconColor?: string
  bgColor?: string
  loading?: boolean
}

export function KpiCard({ title, value, change, changeType = 'neutral', icon: Icon, iconColor = 'text-brand-600', bgColor = 'bg-brand-50 dark:bg-brand-950/30', loading }: KpiCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      className="card p-5 hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{title}</p>
          {loading ? (
            <div className="mt-2 h-8 w-20 bg-gray-100 dark:bg-gray-700 rounded animate-pulse" />
          ) : (
            <p className="mt-1.5 text-2xl font-bold text-gray-900 dark:text-white">{value}</p>
          )}
          {change && (
            <p className={cn('mt-1 text-xs font-medium', changeType === 'up' ? 'text-emerald-600' : changeType === 'down' ? 'text-red-500' : 'text-gray-400')}>
              {changeType === 'up' ? '↑' : changeType === 'down' ? '↓' : '–'} {change}
            </p>
          )}
        </div>
        <div className={cn('w-11 h-11 rounded-2xl flex items-center justify-center flex-shrink-0', bgColor)}>
          <Icon size={20} className={iconColor} />
        </div>
      </div>
    </motion.div>
  )
}

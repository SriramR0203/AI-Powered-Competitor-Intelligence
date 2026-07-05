import React from 'react'
import { cn } from '../../utils/cn'

type Variant = 'default' | 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'orange'

const variantMap: Record<Variant, string> = {
  default: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300',
  success: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-400',
  warning: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-400',
  danger:  'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-400',
  info:    'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-400',
  purple:  'bg-purple-100 text-purple-700 dark:bg-purple-900/40 dark:text-purple-400',
  orange:  'bg-orange-100 text-orange-700 dark:bg-orange-900/40 dark:text-orange-400',
}

interface BadgeProps {
  children: React.ReactNode
  variant?: Variant
  className?: string
  dot?: boolean
}

export function Badge({ children, variant = 'default', className, dot }: BadgeProps) {
  return (
    <span className={cn('badge', variantMap[variant], className)}>
      {dot && <span className="w-1.5 h-1.5 rounded-full bg-current" />}
      {children}
    </span>
  )
}

export function statusBadge(status: string): React.ReactElement {
  const map: Record<string, Variant> = {
    ACTIVE: 'success', INACTIVE: 'default', ARCHIVED: 'warning', PENDING_REVIEW: 'info',
    ENRICHED: 'success', RAW: 'default', PROCESSING: 'info', FAILED: 'danger', SKIPPED: 'warning',
    SUCCESS: 'success', PARTIAL_SUCCESS: 'warning', RUNNING: 'info', PENDING: 'warning', SENT: 'success',
    ACKNOWLEDGED: 'success', DISMISSED: 'default',
  }
  return <Badge variant={map[status] ?? 'default'} dot>{status.replace(/_/g, ' ')}</Badge>
}

export function severityBadge(severity: string): React.ReactElement {
  const map: Record<string, Variant> = {
    LOW: 'info', MEDIUM: 'warning', HIGH: 'orange', CRITICAL: 'danger',
  }
  return <Badge variant={map[severity] ?? 'default'}>{severity}</Badge>
}

export function sentimentBadge(sentiment: string): React.ReactElement {
  const map: Record<string, Variant> = {
    VERY_POSITIVE: 'success', POSITIVE: 'success',
    NEUTRAL: 'default', NEGATIVE: 'danger', VERY_NEGATIVE: 'danger',
  }
  return <Badge variant={map[sentiment] ?? 'default'}>{sentiment.replace(/_/g, ' ')}</Badge>
}

import React from 'react'
import { cn } from '../../utils/cn'

interface CardProps { children: React.ReactNode; className?: string; padding?: boolean }

export function Card({ children, className, padding = true }: CardProps) {
  return (
    <div className={cn('card', padding && 'p-5', className)}>
      {children}
    </div>
  )
}

interface CardHeaderProps { title: string; subtitle?: string; action?: React.ReactNode }

export function CardHeader({ title, subtitle, action }: CardHeaderProps) {
  return (
    <div className="flex items-start justify-between mb-4">
      <div>
        <h3 className="text-sm font-semibold text-gray-900 dark:text-white">{title}</h3>
        {subtitle && <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{subtitle}</p>}
      </div>
      {action && <div className="ml-4 flex-shrink-0">{action}</div>}
    </div>
  )
}

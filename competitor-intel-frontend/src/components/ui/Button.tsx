import React from 'react'
import { cn } from '../../utils/cn'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  icon?: React.ReactNode
}

const variants = {
  primary:   'btn-primary',
  secondary: 'btn-secondary',
  ghost:     'inline-flex items-center justify-center gap-2 rounded-xl px-3 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all',
  danger:    'inline-flex items-center justify-center gap-2 rounded-xl bg-red-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-red-700 transition-all disabled:opacity-50',
}
const sizes = { sm: 'text-xs px-3 py-1.5', md: '', lg: 'text-base px-5 py-3' }

export function Button({ variant = 'primary', size = 'md', loading, icon, children, className, disabled, ...props }: ButtonProps) {
  return (
    <button className={cn(variants[variant], sizes[size], className)} disabled={disabled || loading} {...props}>
      {loading ? (
        <span className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
      ) : icon}
      {children}
    </button>
  )
}

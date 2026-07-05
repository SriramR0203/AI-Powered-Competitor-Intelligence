import React from 'react'
import { cn } from '../../utils/cn'

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string; error?: string; icon?: React.ReactNode
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, icon, className, ...props }, ref) => (
    <div className="w-full">
      {label && <label className="label">{label}</label>}
      <div className="relative">
        {icon && <span className="absolute inset-y-0 left-3 flex items-center text-gray-400">{icon}</span>}
        <input
          ref={ref}
          className={cn('input', icon && 'pl-10', error && 'border-red-500 focus:ring-red-500', className)}
          {...props}
        />
      </div>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Input.displayName = 'Input'

interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string; error?: string
}

export const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, error, className, ...props }, ref) => (
    <div className="w-full">
      {label && <label className="label">{label}</label>}
      <textarea ref={ref} className={cn('input resize-none', error && 'border-red-500', className)} {...props} />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Textarea.displayName = 'Textarea'

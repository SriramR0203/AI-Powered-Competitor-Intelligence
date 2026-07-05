import React from 'react'
import { cn } from '../../utils/cn'

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string; error?: string; options: { value: string; label: string }[]
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className, ...props }, ref) => (
    <div className="w-full">
      {label && <label className="label">{label}</label>}
      <select ref={ref} className={cn('input', error && 'border-red-500', className)} {...props}>
        {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Select.displayName = 'Select'

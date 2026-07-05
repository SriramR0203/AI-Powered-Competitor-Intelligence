import { cn } from '../../utils/cn'

interface SpinnerProps { size?: 'sm' | 'md' | 'lg'; className?: string }

const sizes = { sm: 'w-4 h-4 border-2', md: 'w-8 h-8 border-2', lg: 'w-12 h-12 border-4' }

export function Spinner({ size = 'md', className }: SpinnerProps) {
  return (
    <span className={cn('inline-block rounded-full border-brand-600 border-t-transparent animate-spin', sizes[size], className)} />
  )
}

export function PageSpinner() {
  return (
    <div className="flex items-center justify-center h-64">
      <Spinner size="lg" />
    </div>
  )
}

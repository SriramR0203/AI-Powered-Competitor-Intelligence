import { cn } from '../../utils/cn'

interface AvatarProps { name: string; size?: 'sm' | 'md' | 'lg'; src?: string | null; className?: string }

const sizes = { sm: 'w-7 h-7 text-xs', md: 'w-9 h-9 text-sm', lg: 'w-12 h-12 text-base' }

const colors = [
  'bg-blue-500','bg-purple-500','bg-pink-500','bg-emerald-500','bg-amber-500','bg-rose-500',
]

function getColor(name: string) {
  return colors[name.charCodeAt(0) % colors.length]
}

function initials(name: string) {
  return name.split(' ').map(p => p[0]).slice(0, 2).join('').toUpperCase()
}

export function Avatar({ name, size = 'md', src, className }: AvatarProps) {
  if (src) return <img src={src} alt={name} className={cn('rounded-full object-cover', sizes[size], className)} />
  return (
    <span className={cn('inline-flex items-center justify-center rounded-full text-white font-semibold flex-shrink-0', getColor(name), sizes[size], className)}>
      {initials(name)}
    </span>
  )
}

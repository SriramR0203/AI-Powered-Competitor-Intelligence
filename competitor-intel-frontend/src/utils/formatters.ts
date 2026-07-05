import { format, formatDistanceToNow, parseISO } from 'date-fns'

export function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return '—'
  try { return format(parseISO(dateStr), 'MMM d, yyyy') }
  catch { return dateStr }
}

export function formatDateTime(dateStr: string | null | undefined): string {
  if (!dateStr) return '—'
  try { return format(parseISO(dateStr), 'MMM d, yyyy HH:mm') }
  catch { return dateStr }
}

export function formatRelative(dateStr: string | null | undefined): string {
  if (!dateStr) return '—'
  try { return formatDistanceToNow(parseISO(dateStr), { addSuffix: true }) }
  catch { return dateStr }
}

export function formatNumber(n: number | null | undefined): string {
  if (n == null) return '—'
  return new Intl.NumberFormat().format(n)
}

export function formatBytes(bytes: number | null | undefined): string {
  if (bytes == null) return '—'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

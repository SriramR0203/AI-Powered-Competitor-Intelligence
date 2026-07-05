import { useNavigate } from 'react-router-dom'
import { AlertTriangle, ChevronRight } from 'lucide-react'
import { Card, CardHeader } from '../ui/Card'
import { severityBadge } from '../ui/Badge'
import { formatRelative } from '../../utils/formatters'
import type { AlertNotification } from '../../types'

interface Props { notifications: AlertNotification[] }

export function AlertsWidget({ notifications }: Props) {
  const navigate = useNavigate()
  const pending = notifications.filter(n => n.status === 'PENDING' || n.status === 'SENT')

  return (
    <Card>
      <CardHeader
        title="Active Alerts"
        subtitle={`${pending.length} requiring attention`}
        action={
          <button onClick={() => navigate('/alerts')} className="text-xs text-brand-600 hover:underline flex items-center gap-1">
            View all <ChevronRight size={12} />
          </button>
        }
      />
      <div className="space-y-2">
        {pending.length === 0 && (
          <div className="flex flex-col items-center py-8 text-gray-400">
            <AlertTriangle size={24} className="mb-2 text-emerald-500" />
            <p className="text-sm">All clear — no pending alerts</p>
          </div>
        )}
        {pending.slice(0, 5).map(n => (
          <div key={n.id} className="flex items-start gap-3 p-3 rounded-xl bg-gray-50 dark:bg-gray-700/40 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer" onClick={() => navigate('/alerts')}>
            <div className="w-2 h-2 rounded-full bg-amber-500 mt-1.5 flex-shrink-0" />
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-gray-900 dark:text-white truncate">{n.title}</p>
              <p className="text-xs text-gray-500 mt-0.5 truncate">{n.competitorName} · {formatRelative(n.createdAt)}</p>
            </div>
            {severityBadge(n.severity)}
          </div>
        ))}
      </div>
    </Card>
  )
}

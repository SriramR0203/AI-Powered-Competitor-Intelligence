import { useState } from 'react'
import { motion } from 'framer-motion'
import { Building2, Newspaper, Bell, TrendingUp, RefreshCw } from 'lucide-react'
import { useDashboard } from '../hooks/useDashboard'
import { useEvents } from '../hooks/useEvents'
import { useAlertNotifications } from '../hooks/useAlerts'
import { KpiCard }                  from '../components/dashboard/KpiCard'
import { CategoryChart }            from '../components/dashboard/CategoryChart'
import { SentimentChart }           from '../components/dashboard/SentimentChart'
import { TrendChart }               from '../components/dashboard/TrendChart'
import { CompetitorActivityChart }  from '../components/dashboard/CompetitorActivityChart'
import { RecentEventsTable }        from '../components/dashboard/RecentEventsTable'
import { AlertsWidget }             from '../components/dashboard/AlertsWidget'
import { PageSpinner } from '../components/ui/Spinner'
import { Button } from '../components/ui/Button'
import { cn } from '../utils/cn'

export function DashboardPage() {
  const [days, setDays]  = useState(30)
  const { data: stats, isLoading, isError, refetch } = useDashboard(days)
  const { data: eventsPage }  = useEvents({ page: 0, size: 7, sortBy: 'publishedAt', sortDir: 'desc' })
  const { data: notifPage }   = useAlertNotifications(0)

  if (isLoading) return <PageSpinner />

  if (isError) return (
    <div className="flex flex-col items-center justify-center h-64 gap-3">
      <p className="text-red-500 font-medium">Failed to load dashboard</p>
      <Button variant="secondary" icon={<RefreshCw size={14} />} onClick={() => refetch()}>Retry</Button>
    </div>
  )

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-5 max-w-screen-2xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Dashboard</h2>
          <p className="text-sm text-gray-500">AI-powered competitive intelligence overview</p>
        </div>
        <div className="flex gap-1 bg-gray-100 dark:bg-gray-800 p-1 rounded-xl">
          {([7, 30, 90] as const).map(d => (
            <button key={d} onClick={() => setDays(d)}
              className={cn('px-3 py-1.5 text-xs font-medium rounded-lg transition-all',
                days === d ? 'bg-white dark:bg-gray-700 text-gray-900 dark:text-white shadow-sm' : 'text-gray-500 hover:text-gray-700')}>
              {d}d
            </button>
          ))}
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard title="Active Competitors" value={stats?.activeCompetitors ?? 0} icon={Building2} iconColor="text-blue-600" bgColor="bg-blue-50 dark:bg-blue-950/30" />
        <KpiCard title={`Events (${days}d)`} value={stats?.eventsLast30Days ?? 0} change={`${stats?.eventsLast7Days ?? 0} this week`} changeType="up" icon={Newspaper} iconColor="text-emerald-600" bgColor="bg-emerald-50 dark:bg-emerald-950/30" />
        <KpiCard title="Pending Alerts" value={stats?.pendingAlerts ?? 0} changeType={(stats?.pendingAlerts ?? 0) > 0 ? 'down' : 'neutral'} icon={Bell} iconColor="text-amber-600" bgColor="bg-amber-50 dark:bg-amber-950/30" />
        <KpiCard title="Enriched Events" value={stats?.enrichedEvents ?? 0} changeType="up" icon={TrendingUp} iconColor="text-purple-600" bgColor="bg-purple-50 dark:bg-purple-950/30" />
      </div>

      {/* Charts row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2">
          <CategoryChart data={stats?.eventsByCategory ?? {}} />
        </div>
        <SentimentChart data={stats?.eventsBySentiment ?? {}} />
      </div>

      {/* Charts row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2">
          <TrendChart data={stats?.eventsTrend ?? []} />
        </div>
        <CompetitorActivityChart data={stats?.topCompetitors ?? []} />
      </div>

      {/* Tables */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2">
          <RecentEventsTable events={eventsPage?.content ?? []} />
        </div>
        <AlertsWidget notifications={notifPage?.content ?? []} />
      </div>
    </motion.div>
  )
}

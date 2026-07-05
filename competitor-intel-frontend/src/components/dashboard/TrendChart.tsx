import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { Card, CardHeader } from '../ui/Card'
import { format, parseISO } from 'date-fns'

interface Props { data: { date: string; count: number }[] }

export function TrendChart({ data }: Props) {
  const chartData = data.map(d => ({
    date: format(parseISO(d.date), 'MMM d'),
    Events: d.count,
  }))

  return (
    <Card>
      <CardHeader title="Events Trend" subtitle="Weekly cadence" />
      <ResponsiveContainer width="100%" height={200}>
        <AreaChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
          <defs>
            <linearGradient id="eventsGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%"  stopColor="#3b82f6" stopOpacity={0.3} />
              <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(156,163,175,0.15)" />
          <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#9ca3af' }} />
          <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} allowDecimals={false} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: 12, fontSize: 12 }}
            labelStyle={{ color: '#f3f4f6', fontWeight: 600 }}
            itemStyle={{ color: '#93c5fd' }}
          />
          <Area type="monotone" dataKey="Events" stroke="#3b82f6" strokeWidth={2}
            fill="url(#eventsGrad)" dot={{ fill: '#3b82f6', r: 3 }} />
        </AreaChart>
      </ResponsiveContainer>
    </Card>
  )
}

import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { Card, CardHeader } from '../ui/Card'

interface Props { data: Record<string, number> }

export function CategoryChart({ data }: Props) {
  const chartData = Object.entries(data)
    .sort(([, a], [, b]) => b - a)
    .map(([cat, count]) => ({
      name: cat.replace(/_/g, ' ').split(' ').map(w => w[0] + w.slice(1).toLowerCase()).join(' '),
      count,
    }))

  return (
    <Card>
      <CardHeader title="Events by Category" subtitle="Last 30 days" />
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={chartData} margin={{ top: 0, right: 0, left: -20, bottom: 40 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(156,163,175,0.2)" />
          <XAxis dataKey="name" tick={{ fontSize: 10, fill: '#9ca3af' }} angle={-35} textAnchor="end" interval={0} />
          <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} allowDecimals={false} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: 12, fontSize: 12 }}
            labelStyle={{ color: '#f3f4f6', fontWeight: 600 }}
            itemStyle={{ color: '#93c5fd' }}
          />
          <Bar dataKey="count" name="Events" fill="#3b82f6" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </Card>
  )
}

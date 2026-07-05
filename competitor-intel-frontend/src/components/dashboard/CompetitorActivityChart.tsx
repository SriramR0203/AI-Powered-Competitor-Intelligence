import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts'
import { Card, CardHeader } from '../ui/Card'

const COLORS = ['#3b82f6', '#6366f1', '#8b5cf6', '#a78bfa', '#c4b5fd']

interface Props { data: { id: number; name: string; eventCount: number }[] }

export function CompetitorActivityChart({ data }: Props) {
  return (
    <Card>
      <CardHeader title="Top Competitors by Activity" subtitle="Events generated" />
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data} layout="vertical" margin={{ top: 0, right: 20, left: 60, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(156,163,175,0.15)" horizontal={false} />
          <XAxis type="number" tick={{ fontSize: 11, fill: '#9ca3af' }} allowDecimals={false} />
          <YAxis type="category" dataKey="name" tick={{ fontSize: 12, fill: '#9ca3af' }} width={58} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: 12, fontSize: 12 }}
            itemStyle={{ color: '#93c5fd' }}
          />
          <Bar dataKey="eventCount" name="Events" radius={[0, 4, 4, 0]}>
            {data.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </Card>
  )
}

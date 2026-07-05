import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Card, CardHeader } from '../ui/Card'

const COLORS: Record<string, string> = {
  'VERY POSITIVE': '#10b981', 'POSITIVE': '#34d399',
  'NEUTRAL': '#9ca3af', 'NEGATIVE': '#f87171', 'VERY NEGATIVE': '#ef4444',
}

interface Props { data: Record<string, number> }

export function SentimentChart({ data }: Props) {
  const chartData = Object.entries(data).map(([name, value]) => ({
    name: name.replace(/_/g, ' '),
    value,
  }))

  return (
    <Card>
      <CardHeader title="Sentiment Distribution" subtitle="All enriched events" />
      <ResponsiveContainer width="100%" height={220}>
        <PieChart>
          <Pie data={chartData} cx="50%" cy="50%" innerRadius={55} outerRadius={85}
            dataKey="value" nameKey="name" paddingAngle={2}>
            {chartData.map((entry, i) => (
              <Cell key={i} fill={COLORS[entry.name] ?? '#6b7280'} stroke="none" />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: 12, fontSize: 12 }}
            itemStyle={{ color: '#f3f4f6' }}
          />
          <Legend iconSize={8} iconType="circle" wrapperStyle={{ fontSize: 11 }} />
        </PieChart>
      </ResponsiveContainer>
    </Card>
  )
}

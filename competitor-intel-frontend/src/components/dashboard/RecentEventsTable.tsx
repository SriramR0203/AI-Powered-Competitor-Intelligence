import { useNavigate } from 'react-router-dom'
import { Table, Thead, Th, Tbody, Tr, Td } from '../ui/Table'
import { sentimentBadge, statusBadge } from '../ui/Badge'
import { Card, CardHeader } from '../ui/Card'
import { formatRelative } from '../../utils/formatters'
import type { IntelligenceEvent } from '../../types'

interface Props { events: IntelligenceEvent[] }

export function RecentEventsTable({ events }: Props) {
  const navigate = useNavigate()
  return (
    <Card padding={false}>
      <div className="p-5">
        <CardHeader title="Recent Events" subtitle={`${events.length} latest`} />
      </div>
      <Table>
        <Thead>
          <Tr>
            <Th>Title</Th><Th>Competitor</Th><Th>Category</Th><Th>Sentiment</Th><Th>Status</Th><Th>When</Th>
          </Tr>
        </Thead>
        <Tbody>
          {events.slice(0, 7).map(event => (
            <Tr key={event.id} onClick={() => navigate('/events')}>
              <Td><span className="font-medium text-gray-900 dark:text-white line-clamp-1 max-w-xs">{event.title}</span></Td>
              <Td><span className="text-brand-600 font-medium">{event.competitorName}</span></Td>
              <Td><span className="text-xs text-gray-500">{event.category.replace(/_/g, ' ')}</span></Td>
              <Td>{sentimentBadge(event.sentiment)}</Td>
              <Td>{statusBadge(event.processingStatus)}</Td>
              <Td><span className="text-gray-400 text-xs">{formatRelative(event.publishedAt)}</span></Td>
            </Tr>
          ))}
        </Tbody>
      </Table>
    </Card>
  )
}

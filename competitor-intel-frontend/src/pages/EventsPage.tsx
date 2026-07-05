import { useState } from 'react'
import { motion } from 'framer-motion'
import { Search, Newspaper, Zap, Flag, RefreshCw, X } from 'lucide-react'
import { useEvents, useEnrichEvent, useProcessRaw, useFlagEvent } from '../hooks/useEvents'
import { Table, Thead, Th, Tbody, Tr, Td } from '../components/ui/Table'
import { sentimentBadge, statusBadge } from '../components/ui/Badge'
import { Card } from '../components/ui/Card'
import { PageSpinner } from '../components/ui/Spinner'
import { EmptyState } from '../components/ui/EmptyState'
import { Button } from '../components/ui/Button'
import { formatRelative } from '../utils/formatters'
import type { EventFilters } from '../services/intelligence.service'

const CATEGORIES = ['PRODUCT_LAUNCH','PRICING_CHANGE','PARTNERSHIP','ACQUISITION','LEADERSHIP_CHANGE','FUNDING','LEGAL','MARKETING_CAMPAIGN','TECHNOLOGY','HIRING','GENERAL_NEWS','UNCLASSIFIED']
const SENTIMENTS  = ['VERY_POSITIVE','POSITIVE','NEUTRAL','NEGATIVE','VERY_NEGATIVE']
const STATUSES    = ['RAW','PROCESSING','ENRICHED','FAILED']

export function EventsPage() {
  const [filters, setFilters] = useState<EventFilters>({ page: 0, size: 20 })
  const [search,  setSearch]  = useState('')

  const { data, isLoading, isError, refetch } = useEvents(filters)
  const enrich     = useEnrichEvent()
  const processRaw = useProcessRaw()
  const flagEvent  = useFlagEvent()

  const applySearch = () => setFilters(f => ({ ...f, search, page: 0 }))
  const clearFilter = () => { setSearch(''); setFilters({ page: 0, size: 20 }) }

  if (isLoading) return <PageSpinner />
  if (isError)   return (
    <div className="flex flex-col items-center justify-center h-64 gap-3">
      <p className="text-red-500">Failed to load events</p>
      <Button variant="secondary" icon={<RefreshCw size={14} />} onClick={() => refetch()}>Retry</Button>
    </div>
  )

  const events = data?.content ?? []

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="max-w-screen-xl mx-auto space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Intelligence Events</h2>
          <p className="text-sm text-gray-500">{data?.totalElements ?? 0} events collected</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" size="sm" icon={<Zap size={14} />}
            loading={processRaw.isPending} onClick={() => processRaw.mutate()}>
            Enrich All
          </Button>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-2 items-center">
        <div className="relative">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input value={search} onChange={e => setSearch(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && applySearch()}
            placeholder="Search events…" className="input pl-9 text-sm w-56" />
        </div>
        <select className="input text-sm w-44"
          onChange={e => setFilters(f => ({ ...f, category: e.target.value, page: 0 }))}>
          <option value="">All Categories</option>
          {CATEGORIES.map(c => <option key={c} value={c}>{c.replace(/_/g,' ')}</option>)}
        </select>
        <select className="input text-sm w-36"
          onChange={e => setFilters(f => ({ ...f, sentiment: e.target.value, page: 0 }))}>
          <option value="">All Sentiments</option>
          {SENTIMENTS.map(s => <option key={s} value={s}>{s.replace(/_/g,' ')}</option>)}
        </select>
        <select className="input text-sm w-36"
          onChange={e => setFilters(f => ({ ...f, processingStatus: e.target.value, page: 0 }))}>
          <option value="">All Statuses</option>
          {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
        </select>
        {(filters.search || filters.category || filters.sentiment || filters.processingStatus) && (
          <button onClick={clearFilter} className="p-2 text-gray-400 hover:text-red-500 transition-colors"><X size={14} /></button>
        )}
      </div>

      <Card padding={false}>
        {events.length === 0 ? (
          <EmptyState icon={<Newspaper size={24} />} title="No events found" description="Adjust your filters or trigger a scrape." className="py-20" />
        ) : (
          <Table>
            <Thead>
              <Tr>
                <Th>Title</Th><Th>Competitor</Th><Th>Category</Th>
                <Th>Sentiment</Th><Th>Status</Th><Th>Published</Th><Th>Actions</Th>
              </Tr>
            </Thead>
            <Tbody>
              {events.map(event => (
                <Tr key={event.id}>
                  <Td>
                    <div className="flex items-start gap-2 max-w-sm">
                      {event.flagged && <Flag size={12} className="text-red-500 flex-shrink-0 mt-0.5" />}
                      <div>
                        <p className="font-medium text-gray-900 dark:text-white line-clamp-2 text-sm">{event.title}</p>
                        {event.aiSummary && <p className="text-xs text-gray-400 mt-0.5 line-clamp-1">{event.aiSummary}</p>}
                      </div>
                    </div>
                  </Td>
                  <Td><span className="text-brand-600 font-medium text-xs">{event.competitorName}</span></Td>
                  <Td><span className="text-xs text-gray-500">{event.category.replace(/_/g,' ')}</span></Td>
                  <Td>{sentimentBadge(event.sentiment)}</Td>
                  <Td>{statusBadge(event.processingStatus)}</Td>
                  <Td><span className="text-xs text-gray-400">{formatRelative(event.publishedAt)}</span></Td>
                  <Td>
                    <div className="flex gap-1">
                      {event.processingStatus === 'RAW' && (
                        <Button variant="ghost" size="sm" icon={<Zap size={12} />}
                          loading={enrich.isPending && enrich.variables === event.id}
                          onClick={() => enrich.mutate(event.id)} />
                      )}
                      <Button variant="ghost" size="sm" icon={<Flag size={12} />}
                        className={event.flagged ? 'text-red-500' : ''}
                        onClick={() => event.flagged
                          ? flagEvent.mutate({ id: event.id, reason: '' })
                          : flagEvent.mutate({ id: event.id, reason: 'Flagged for review' })} />
                    </div>
                  </Td>
                </Tr>
              ))}
            </Tbody>
          </Table>
        )}
      </Card>

      {(data?.totalPages ?? 0) > 1 && (
        <div className="flex justify-center gap-2">
          <Button variant="secondary" size="sm" disabled={data?.first}
            onClick={() => setFilters(f => ({ ...f, page: (f.page ?? 1) - 1 }))}>Previous</Button>
          <span className="px-3 py-1.5 text-sm text-gray-500">
            Page {(filters.page ?? 0) + 1} of {data?.totalPages}
          </span>
          <Button variant="secondary" size="sm" disabled={data?.last}
            onClick={() => setFilters(f => ({ ...f, page: (f.page ?? 0) + 1 }))}>Next</Button>
        </div>
      )}
    </motion.div>
  )
}

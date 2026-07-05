import { useState } from 'react'
import { motion } from 'framer-motion'
import { Plus, Radio, RefreshCw, Trash2, X } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useSources, useCreateSource, useDeleteSource, useTriggerScrape } from '../hooks/useSources'
import { useActiveCompetitors } from '../hooks/useCompetitors'
import { Table, Thead, Th, Tbody, Tr, Td } from '../components/ui/Table'
import { statusBadge } from '../components/ui/Badge'
import { Card }           from '../components/ui/Card'
import { Button }         from '../components/ui/Button'
import { Input }          from '../components/ui/Input'
import { Select }         from '../components/ui/Select'
import { Modal }          from '../components/ui/Modal'
import { ConfirmDialog }  from '../components/ui/ConfirmDialog'
import { PageSpinner }    from '../components/ui/Spinner'
import { EmptyState }     from '../components/ui/EmptyState'
import { Tooltip }        from '../components/ui/Tooltip'
import { formatRelative } from '../utils/formatters'
import type { CreateSourcePayload }   from '../services/source.service'
import type { IntelligenceSource }    from '../types'
import { extractApiError }            from '../services/api'

const schema = z.object({
  competitorId:       z.coerce.number().min(1, 'Required'),
  name:               z.string().min(1, 'Required'),
  url:                z.string().url('Must be a valid URL'),
  sourceType:         z.string().min(1, 'Required'),
  scrapeIntervalHours: z.coerce.number().min(1).max(168).optional(),
  cssSelector:        z.string().optional(),
  requiresJavascript: z.boolean().optional(),
  notes:              z.string().optional(),
})
type FormData = z.infer<typeof schema>

const SOURCE_TYPES = ['WEBSITE','RSS_FEED','NEWS_API','SOCIAL_MEDIA','PRESS_RELEASE','BLOG','JOB_BOARD']

export function SourcesPage() {
  const [search,       setSearch]       = useState('')
  const [modal,        setModal]        = useState(false)
  const [apiErr,       setApiErr]       = useState('')
  const [deleteTarget, setDeleteTarget] = useState<IntelligenceSource | null>(null)

  const { data, isLoading, isError, refetch } = useSources({ size: 50 })
  const { data: competitors = [] }            = useActiveCompetitors()
  const createMut = useCreateSource()
  const deleteMut = useDeleteSource()
  const scrapeMut = useTriggerScrape()

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } =
    useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: FormData) => {
    setApiErr('')
    try {
      await createMut.mutateAsync(data as CreateSourcePayload)
      setModal(false); reset({})
    } catch (err) { setApiErr(extractApiError(err)) }
  }

  const sources = (data?.content ?? []).filter(s =>
    s.name.toLowerCase().includes(search.toLowerCase()) ||
    s.competitorName.toLowerCase().includes(search.toLowerCase()))

  if (isLoading) return <PageSpinner />
  if (isError)   return (
    <div className="flex flex-col items-center justify-center h-64 gap-3">
      <p className="text-red-500">Failed to load sources</p>
      <Button variant="secondary" icon={<RefreshCw size={14} />} onClick={() => refetch()}>Retry</Button>
    </div>
  )

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="max-w-screen-xl mx-auto space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Intelligence Sources</h2>
          <p className="text-sm text-gray-500">{data?.totalElements ?? 0} monitoring sources</p>
        </div>
        <Button icon={<Plus size={15} />} size="sm" onClick={() => { setApiErr(''); reset({}); setModal(true) }}>Add Source</Button>
      </div>

      <div className="flex gap-2">
        <div className="relative max-w-xs">
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search sources…" className="input text-sm pl-4" />
        </div>
        {search && <button onClick={() => setSearch('')} className="p-2 text-gray-400 hover:text-red-500"><X size={14} /></button>}
      </div>

      <Card padding={false}>
        {sources.length === 0 ? (
          <EmptyState icon={<Radio size={24} />} title="No sources found"
            action={<Button icon={<Plus size={14} />} size="sm" onClick={() => setModal(true)}>Add Source</Button>} />
        ) : (
          <Table>
            <Thead>
              <Tr><Th>Name / URL</Th><Th>Competitor</Th><Th>Type</Th><Th>Interval</Th><Th>Last Scraped</Th><Th>Failures</Th><Th>Status</Th><Th>Actions</Th></Tr>
            </Thead>
            <Tbody>
              {sources.map(s => (
                <Tr key={s.id}>
                  <Td>
                    <p className="font-medium text-sm text-gray-900 dark:text-white">{s.name}</p>
                    <a href={s.url} target="_blank" rel="noopener noreferrer" className="text-xs text-gray-400 hover:text-brand-600 truncate block max-w-xs">{s.url}</a>
                  </Td>
                  <Td><span className="text-brand-600 font-medium text-xs">{s.competitorName}</span></Td>
                  <Td><span className="text-xs text-gray-500 bg-gray-100 dark:bg-gray-700 rounded px-1.5 py-0.5">{s.sourceType.replace(/_/g,' ')}</span></Td>
                  <Td><span className="text-xs text-gray-500">Every {s.scrapeIntervalHours}h</span></Td>
                  <Td><span className="text-xs text-gray-400">{formatRelative(s.lastScrapedAt)}</span></Td>
                  <Td><span className={`text-xs font-bold ${s.consecutiveFailures > 0 ? 'text-red-500' : 'text-emerald-500'}`}>{s.consecutiveFailures}</span></Td>
                  <Td>{statusBadge(s.active ? 'ACTIVE' : 'INACTIVE')}</Td>
                  <Td>
                    <div className="flex gap-1">
                      <Tooltip content="Trigger scrape now">
                        <Button variant="ghost" size="sm" icon={<RefreshCw size={13} />}
                          loading={scrapeMut.isPending && scrapeMut.variables === s.id}
                          onClick={() => scrapeMut.mutate(s.id)} />
                      </Tooltip>
                      <Tooltip content="Delete source">
                        <Button variant="ghost" size="sm" icon={<Trash2 size={13} />}
                          className="text-red-400 hover:text-red-500"
                          aria-label={`Delete source ${s.name}`}
                          onClick={() => setDeleteTarget(s)} />
                      </Tooltip>
                    </div>
                  </Td>
                </Tr>
              ))}
            </Tbody>
          </Table>
        )}
      </Card>

      <Modal open={modal} onClose={() => setModal(false)} title="Add Intelligence Source" size="md">
        {apiErr && <p className="mb-4 text-sm text-red-500 bg-red-50 dark:bg-red-900/20 rounded-xl p-3">{apiErr}</p>}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Select label="Competitor" {...register('competitorId')}
            options={[{ value: '', label: 'Select competitor…' }, ...competitors.map(c => ({ value: String(c.id), label: c.name }))]}
            error={errors.competitorId?.message} />
          <Input label="Source Name" {...register('name')} error={errors.name?.message} />
          <Input label="URL" {...register('url')} error={errors.url?.message} />
          <div className="grid grid-cols-2 gap-4">
            <Select label="Source Type" {...register('sourceType')}
              options={[{ value: '', label: 'Select type…' }, ...SOURCE_TYPES.map(t => ({ value: t, label: t.replace(/_/g,' ') }))]}
              error={errors.sourceType?.message} />
            <Input label="Scrape Interval (hours)" type="number" min={1} max={168} {...register('scrapeIntervalHours')} />
          </div>
          <Input label="CSS Selector (optional)" {...register('cssSelector')} />
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="secondary" type="button" onClick={() => setModal(false)}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>Add Source</Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Source"
        message={`Are you sure you want to delete "${deleteTarget?.name}"? This cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMut.isPending}
        onConfirm={() => {
          if (!deleteTarget) return
          deleteMut.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }}
        onCancel={() => setDeleteTarget(null)}
      />
    </motion.div>
  )
}

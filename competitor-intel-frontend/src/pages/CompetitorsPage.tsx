import { useState, useCallback } from 'react'
import { motion } from 'framer-motion'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Plus, Search, Globe, Star, RefreshCw,
  Pencil, Archive, Trash2, X, Building2,
} from 'lucide-react'
import {
  useCompetitors, useIndustries,
  useCreateCompetitor, useUpdateCompetitor,
  useArchiveCompetitor, useDeleteCompetitor,
} from '../hooks/useCompetitors'
import { Card }             from '../components/ui/Card'
import { statusBadge }      from '../components/ui/Badge'
import { Button }           from '../components/ui/Button'
import { Input, Textarea }  from '../components/ui/Input'
import { Modal }            from '../components/ui/Modal'
import { ConfirmDialog }    from '../components/ui/ConfirmDialog'
import { Avatar }           from '../components/ui/Avatar'
import { PageSpinner }      from '../components/ui/Spinner'
import { EmptyState }       from '../components/ui/EmptyState'
import type { Competitor }  from '../types'
import type { CompetitorFilters, CreateCompetitorPayload } from '../services/competitor.service'
import { extractApiError }  from '../services/api'

const schema = z.object({
  name:          z.string().min(1, 'Name is required'),
  websiteUrl:    z.string().url('Must be a valid URL'),
  description:   z.string().optional(),
  industry:      z.string().optional(),
  headquarters:  z.string().optional(),
  priorityScore: z.coerce.number().min(1).max(10).optional(),
  notes:         z.string().optional(),
})
type FormData = z.infer<typeof schema>

export function CompetitorsPage() {
  const [filters,    setFilters]    = useState<CompetitorFilters>({ page: 0, size: 18 })
  const [search,     setSearch]     = useState('')
  const [modalOpen,  setModalOpen]  = useState(false)
  const [editing,    setEditing]    = useState<Competitor | null>(null)
  const [apiErr,     setApiErr]     = useState('')
  const [deleteTarget, setDeleteTarget] = useState<Competitor | null>(null)

  const { data, isLoading, isError, refetch } = useCompetitors(filters)
  const { data: industries = [] }             = useIndustries()

  const createMut  = useCreateCompetitor()
  const updateMut  = useUpdateCompetitor()
  const archiveMut = useArchiveCompetitor()
  const deleteMut  = useDeleteCompetitor()

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } =
    useForm<FormData>({ resolver: zodResolver(schema) })

  const openCreate = () => {
    setEditing(null)
    reset({})
    setApiErr('')
    setModalOpen(true)
  }

  const openEdit = (c: Competitor) => {
    setEditing(c)
    reset({
      name:          c.name,
      websiteUrl:    c.websiteUrl,
      description:   c.description   ?? '',
      industry:      c.industry       ?? '',
      headquarters:  c.headquarters   ?? '',
      priorityScore: c.priorityScore,
      notes:         c.notes          ?? '',
    })
    setApiErr('')
    setModalOpen(true)
  }

  const onSubmit = async (data: FormData) => {
    setApiErr('')
    try {
      if (editing) {
        await updateMut.mutateAsync({ id: editing.id, data })
      } else {
        await createMut.mutateAsync(data as CreateCompetitorPayload)
      }
      setModalOpen(false)
    } catch (err) {
      setApiErr(extractApiError(err))
    }
  }

  const handleSearch = useCallback(() => {
    setFilters(f => ({ ...f, search, page: 0 }))
  }, [search])

  const clearFilters = () => {
    setSearch('')
    setFilters({ page: 0, size: 18 })
  }

  if (isLoading) return <PageSpinner />

  if (isError) return (
    <div className="flex flex-col items-center justify-center h-64 gap-3">
      <p className="text-red-500">Failed to load competitors</p>
      <Button variant="secondary" icon={<RefreshCw size={14} />} onClick={() => refetch()}>
        Retry
      </Button>
    </div>
  )

  const competitors      = data?.content ?? []
  const hasActiveFilters = !!(filters.search || filters.status || filters.industry)

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-screen-xl mx-auto space-y-5"
    >
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Competitors</h2>
          <p className="text-sm text-gray-500">{data?.totalElements ?? 0} tracked companies</p>
        </div>
        <Button icon={<Plus size={15} />} size="sm" onClick={openCreate}>
          Add Competitor
        </Button>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-2 items-center">
        <div className="relative flex-1 max-w-xs">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSearch()}
            placeholder="Search competitors…"
            aria-label="Search competitors"
            className="input pl-9 text-sm w-full"
          />
        </div>

        <select
          aria-label="Filter by status"
          className="input text-sm w-36"
          onChange={e => setFilters(f => ({ ...f, status: e.target.value || undefined, page: 0 }))}
        >
          <option value="">All Status</option>
          {['ACTIVE', 'INACTIVE', 'ARCHIVED', 'PENDING_REVIEW'].map(s => (
            <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>
          ))}
        </select>

        <select
          aria-label="Filter by industry"
          className="input text-sm w-40"
          onChange={e => setFilters(f => ({ ...f, industry: e.target.value || undefined, page: 0 }))}
        >
          <option value="">All Industries</option>
          {industries.map(i => <option key={i} value={i}>{i}</option>)}
        </select>

        {hasActiveFilters && (
          <button
            onClick={clearFilters}
            aria-label="Clear filters"
            className="p-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
          >
            <X size={14} />
          </button>
        )}
      </div>

      {/* Cards grid */}
      {competitors.length === 0 ? (
        <EmptyState
          icon={<Building2 size={24} />}
          title="No competitors found"
          description="Add your first competitor or adjust your filters."
          action={
            <Button icon={<Plus size={14} />} size="sm" onClick={openCreate}>
              Add Competitor
            </Button>
          }
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {competitors.map(c => (
            <Card key={c.id} className="hover:shadow-md transition-shadow">
              <div className="flex items-start gap-3">
                <Avatar name={c.name} size="md" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <h3 className="text-sm font-semibold text-gray-900 dark:text-white truncate">
                      {c.name}
                    </h3>
                    {statusBadge(c.status)}
                  </div>
                  <p className="text-xs text-gray-500 truncate mt-0.5">
                    {c.industry ?? '—'} · {c.headquarters ?? '—'}
                  </p>
                </div>
                <div className="flex items-center gap-0.5 text-amber-500 flex-shrink-0">
                  <Star size={12} fill="currentColor" />
                  <span className="text-xs font-medium">{c.priorityScore}</span>
                </div>
              </div>

              {c.description && (
                <p className="mt-3 text-xs text-gray-500 dark:text-gray-400 line-clamp-2">
                  {c.description}
                </p>
              )}

              <div className="mt-4 flex items-center justify-between">
                <div className="flex gap-4">
                  <div className="text-center">
                    <p className="text-sm font-bold text-gray-900 dark:text-white">{c.activeSourceCount}</p>
                    <p className="text-xs text-gray-400">Sources</p>
                  </div>
                  <div className="text-center">
                    <p className="text-sm font-bold text-gray-900 dark:text-white">{c.recentEventCount}</p>
                    <p className="text-xs text-gray-400">Events</p>
                  </div>
                </div>

                <div className="flex items-center gap-1">
                  <a
                    href={c.websiteUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label={`Visit ${c.name} website`}
                    className="p-1.5 text-gray-400 hover:text-brand-600 transition-colors"
                  >
                    <Globe size={14} />
                  </a>
                  <button
                    onClick={() => openEdit(c)}
                    aria-label={`Edit ${c.name}`}
                    className="p-1.5 text-gray-400 hover:text-blue-500 transition-colors"
                  >
                    <Pencil size={14} />
                  </button>
                  <button
                    onClick={() => archiveMut.mutate(c.id)}
                    aria-label={`Archive ${c.name}`}
                    className="p-1.5 text-gray-400 hover:text-amber-500 transition-colors"
                  >
                    <Archive size={14} />
                  </button>
                  <button
                    onClick={() => setDeleteTarget(c)}
                    aria-label={`Delete ${c.name}`}
                    className="p-1.5 text-gray-400 hover:text-red-500 transition-colors"
                  >
                    <Trash2 size={14} />
                  </button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Pagination */}
      {(data?.totalPages ?? 0) > 1 && (
        <div className="flex justify-center items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            disabled={data?.first ?? true}
            onClick={() => setFilters(f => ({ ...f, page: Math.max(0, (f.page ?? 1) - 1) }))}
          >
            Previous
          </Button>
          <span className="px-3 py-1.5 text-sm text-gray-500">
            Page {(filters.page ?? 0) + 1} of {data?.totalPages}
          </span>
          <Button
            variant="secondary"
            size="sm"
            disabled={data?.last ?? true}
            onClick={() => setFilters(f => ({ ...f, page: (f.page ?? 0) + 1 }))}
          >
            Next
          </Button>
        </div>
      )}

      {/* Create / Edit Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Edit Competitor' : 'Add Competitor'}
        size="md"
      >
        {apiErr && (
          <p className="mb-4 text-sm text-red-500 bg-red-50 dark:bg-red-900/20 rounded-xl p-3">
            {apiErr}
          </p>
        )}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="Company Name" {...register('name')} error={errors.name?.message} autoComplete="organization" />
            <Input label="Website URL"  {...register('websiteUrl')} error={errors.websiteUrl?.message} autoComplete="url" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input label="Industry"     {...register('industry')} />
            <Input label="Headquarters" {...register('headquarters')} />
          </div>
          <Input label="Priority Score (1–10)" type="number" min={1} max={10} {...register('priorityScore')} error={errors.priorityScore?.message} />
          <Textarea label="Description" rows={3} {...register('description')} />
          <Textarea label="Notes"       rows={2} {...register('notes')} />
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="secondary" type="button" onClick={() => setModalOpen(false)}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>{editing ? 'Save Changes' : 'Add Competitor'}</Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirm Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Competitor"
        message={`Are you sure you want to delete "${deleteTarget?.name}"? All associated sources, events and alerts will also be removed.`}
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

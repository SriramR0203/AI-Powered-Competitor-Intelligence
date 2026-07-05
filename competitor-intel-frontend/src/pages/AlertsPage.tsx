import { useState } from 'react'
import { motion } from 'framer-motion'
import { Bell, Plus, Check, CheckCheck, Trash2, AlertTriangle } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  useAlertRules, useAlertNotifications, useCreateAlertRule,
  useDeleteAlertRule, useAcknowledgeNotification, useAcknowledgeAll,
  useEnableRule, useDisableRule,
} from '../hooks/useAlerts'
import { Table, Thead, Th, Tbody, Tr, Td } from '../components/ui/Table'
import { severityBadge, statusBadge } from '../components/ui/Badge'
import { Card }           from '../components/ui/Card'
import { Button }         from '../components/ui/Button'
import { Input }          from '../components/ui/Input'
import { Select }         from '../components/ui/Select'
import { Modal }          from '../components/ui/Modal'
import { ConfirmDialog }  from '../components/ui/ConfirmDialog'
import { PageSpinner }    from '../components/ui/Spinner'
import { EmptyState }     from '../components/ui/EmptyState'
import { formatRelative } from '../utils/formatters'
import type { CreateAlertRulePayload } from '../services/alert.service'
import type { AlertRule } from '../types'
import { extractApiError } from '../services/api'
import { cn } from '../utils/cn'

const schema = z.object({
  name:            z.string().min(1, 'Required'),
  description:     z.string().optional(),
  categoryFilter:  z.string().optional(),
  sentimentFilter: z.string().optional(),
  keywordFilter:   z.string().optional(),
  severity:        z.string().default('MEDIUM'),
  notifyEmail:     z.boolean().default(true),
  notifyInApp:     z.boolean().default(true),
  cooldownMinutes: z.coerce.number().min(1).default(60),
})
type FormData = z.infer<typeof schema>
type Tab = 'notifications' | 'rules'

const CATEGORIES = ['PRODUCT_LAUNCH','PRICING_CHANGE','PARTNERSHIP','ACQUISITION','FUNDING','LEGAL','HIRING','GENERAL_NEWS']
const SEVERITIES = ['LOW','MEDIUM','HIGH','CRITICAL']

export function AlertsPage() {
  const [tab,          setTab]          = useState<Tab>('notifications')
  const [modal,        setModal]        = useState(false)
  const [page,         setPage]         = useState(0)
  const [apiErr,       setApiErr]       = useState('')
  const [deleteTarget, setDeleteTarget] = useState<AlertRule | null>(null)

  const { data: rules,     isLoading: lr } = useAlertRules()
  const { data: notifPage, isLoading: ln } = useAlertNotifications(page)
  const createRule  = useCreateAlertRule()
  const deleteRule  = useDeleteAlertRule()
  const enableRule  = useEnableRule()
  const disableRule = useDisableRule()
  const ack         = useAcknowledgeNotification()
  const ackAll      = useAcknowledgeAll()

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } =
    useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: FormData) => {
    setApiErr('')
    try {
      await createRule.mutateAsync(data as CreateAlertRulePayload)
      setModal(false)
      reset({})
    } catch (err) {
      setApiErr(extractApiError(err))
    }
  }

  const notifications = notifPage?.content ?? []
  const pendingCount  = notifications.filter(n => n.status === 'PENDING' || n.status === 'SENT').length

  if (lr || ln) return <PageSpinner />

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="max-w-screen-xl mx-auto space-y-5">

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Alerts</h2>
          <p className="text-sm text-gray-500">{pendingCount} unread notifications</p>
        </div>
        <div className="flex gap-2">
          {tab === 'rules' && (
            <Button icon={<Plus size={15} />} size="sm" onClick={() => { setApiErr(''); reset({}); setModal(true) }}>
              New Rule
            </Button>
          )}
          {tab === 'notifications' && pendingCount > 0 && (
            <Button variant="secondary" size="sm" icon={<CheckCheck size={14} />}
              loading={ackAll.isPending} onClick={() => ackAll.mutate()}>
              Mark All Read
            </Button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 bg-gray-100 dark:bg-gray-800 p-1 rounded-xl w-fit">
        {(['notifications', 'rules'] as Tab[]).map(t => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={cn(
              'px-4 py-2 text-sm font-medium rounded-lg capitalize transition-all',
              tab === t
                ? 'bg-white dark:bg-gray-700 text-gray-900 dark:text-white shadow-sm'
                : 'text-gray-500 hover:text-gray-700 dark:hover:text-gray-300',
            )}
          >
            {t}
            {t === 'notifications' && pendingCount > 0 && (
              <span className="ml-1.5 px-1.5 py-0.5 bg-red-500 text-white text-xs rounded-full">
                {pendingCount}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* ── Notifications tab ─────────────────────────────────── */}
      {tab === 'notifications' && (
        <>
          <Card padding={false}>
            {notifications.length === 0 ? (
              <EmptyState icon={<Bell size={24} />} title="No notifications"
                description="Alert rules will fire notifications here." />
            ) : (
              <Table>
                <Thead>
                  <Tr>
                    <Th>Title</Th><Th>Competitor</Th><Th>Rule</Th>
                    <Th>Severity</Th><Th>Status</Th><Th>When</Th><Th>Action</Th>
                  </Tr>
                </Thead>
                <Tbody>
                  {notifications.map(n => (
                    <Tr key={n.id} className={n.status === 'PENDING' ? 'bg-amber-50/40 dark:bg-amber-950/10' : undefined}>
                      <Td>
                        <p className="font-medium text-sm text-gray-900 dark:text-white">{n.title}</p>
                        <p className="text-xs text-gray-400 line-clamp-1">{n.message}</p>
                      </Td>
                      <Td><span className="text-brand-600 text-xs font-medium">{n.competitorName}</span></Td>
                      <Td><span className="text-xs text-gray-500">{n.alertRuleName}</span></Td>
                      <Td>{severityBadge(n.severity)}</Td>
                      <Td>{statusBadge(n.status)}</Td>
                      <Td><span className="text-xs text-gray-400">{formatRelative(n.createdAt)}</span></Td>
                      <Td>
                        {(n.status === 'PENDING' || n.status === 'SENT') && (
                          <Button variant="ghost" size="sm" icon={<Check size={13} />}
                            aria-label="Acknowledge notification"
                            loading={ack.isPending && ack.variables === n.id}
                            onClick={() => ack.mutate(n.id)} />
                        )}
                      </Td>
                    </Tr>
                  ))}
                </Tbody>
              </Table>
            )}
          </Card>

          {(notifPage?.totalPages ?? 0) > 1 && (
            <div className="flex justify-center items-center gap-2">
              <Button variant="secondary" size="sm" disabled={notifPage?.first} onClick={() => setPage(p => p - 1)}>Previous</Button>
              <span className="px-3 py-1.5 text-sm text-gray-500">Page {page + 1} of {notifPage?.totalPages}</span>
              <Button variant="secondary" size="sm" disabled={notifPage?.last}  onClick={() => setPage(p => p + 1)}>Next</Button>
            </div>
          )}
        </>
      )}

      {/* ── Rules tab ─────────────────────────────────────────── */}
      {tab === 'rules' && (
        <Card padding={false}>
          {!rules || rules.length === 0 ? (
            <EmptyState icon={<AlertTriangle size={24} />} title="No alert rules"
              action={<Button icon={<Plus size={14} />} size="sm" onClick={() => setModal(true)}>Create Rule</Button>} />
          ) : (
            <Table>
              <Thead>
                <Tr>
                  <Th>Name</Th><Th>Competitor</Th><Th>Category</Th>
                  <Th>Severity</Th><Th>Status</Th><Th>Cooldown</Th><Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {rules.map(r => (
                  <Tr key={r.id}>
                    <Td>
                      <p className="font-medium text-sm text-gray-900 dark:text-white">{r.name}</p>
                      {r.description && <p className="text-xs text-gray-400">{r.description}</p>}
                    </Td>
                    <Td><span className="text-xs">{r.competitorName ?? 'All'}</span></Td>
                    <Td><span className="text-xs text-gray-500">{r.categoryFilter ?? 'Any'}</span></Td>
                    <Td>{severityBadge(r.severity)}</Td>
                    <Td>{statusBadge(r.active ? 'ACTIVE' : 'INACTIVE')}</Td>
                    <Td><span className="text-xs text-gray-400">{r.cooldownMinutes}m</span></Td>
                    <Td>
                      <div className="flex gap-1">
                        <Button
                          variant="ghost" size="sm"
                          className={r.active ? 'text-amber-500 hover:text-amber-600' : 'text-emerald-500 hover:text-emerald-600'}
                          onClick={() => r.active ? disableRule.mutate(r.id) : enableRule.mutate(r.id)}
                        >
                          {r.active ? 'Disable' : 'Enable'}
                        </Button>
                        <Button
                          variant="ghost" size="sm"
                          icon={<Trash2 size={13} />}
                          className="text-red-400 hover:text-red-500"
                          aria-label={`Delete rule ${r.name}`}
                          loading={deleteRule.isPending && deleteRule.variables === r.id}
                          onClick={() => setDeleteTarget(r)}
                        />
                      </div>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          )}
        </Card>
      )}

      {/* ── Create Rule Modal ─────────────────────────────────── */}
      <Modal open={modal} onClose={() => setModal(false)} title="Create Alert Rule" size="md">
        {apiErr && (
          <p className="mb-4 text-sm text-red-500 bg-red-50 dark:bg-red-900/20 rounded-xl p-3">{apiErr}</p>
        )}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Rule Name" {...register('name')} error={errors.name?.message} />
          <Input label="Description (optional)" {...register('description')} />
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Category Filter"
              {...register('categoryFilter')}
              options={[
                { value: '', label: 'Any Category' },
                ...CATEGORIES.map(c => ({ value: c, label: c.replace(/_/g, ' ') })),
              ]}
            />
            <Select
              label="Severity"
              {...register('severity')}
              options={SEVERITIES.map(s => ({ value: s, label: s }))}
            />
          </div>
          <Input label="Keyword Filter (optional)" {...register('keywordFilter')} />
          <Input label="Cooldown (minutes)" type="number" min={1} {...register('cooldownMinutes')} />
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="secondary" type="button" onClick={() => setModal(false)}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>Create Rule</Button>
          </div>
        </form>
      </Modal>

      {/* ── Delete Confirm Dialog ─────────────────────────────── */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Alert Rule"
        message={`Are you sure you want to delete the rule "${deleteTarget?.name}"? All associated notifications will also be removed.`}
        confirmLabel="Delete"
        loading={deleteRule.isPending}
        onConfirm={() => {
          if (!deleteTarget) return
          deleteRule.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
        }}
        onCancel={() => setDeleteTarget(null)}
      />
    </motion.div>
  )
}

import { useState } from 'react'
import { motion } from 'framer-motion'
import { FileText, Download, Trash2, RefreshCw } from 'lucide-react'
import { useReports, useExportCsv, useExportPdf, useDeleteReport } from '../hooks/useReports'
import { Table, Thead, Th, Tbody, Tr, Td } from '../components/ui/Table'
import { Card }         from '../components/ui/Card'
import { Button }       from '../components/ui/Button'
import { Badge }        from '../components/ui/Badge'
import { PageSpinner }  from '../components/ui/Spinner'
import { EmptyState }   from '../components/ui/EmptyState'
import { ConfirmDialog } from '../components/ui/ConfirmDialog'
import { formatDate, formatBytes, formatNumber } from '../utils/formatters'
import type { Report } from '../types'

export function ReportsPage() {
  const [page,          setPage]          = useState(0)
  const [deleteTarget,  setDeleteTarget]  = useState<Report | null>(null)

  const { data, isLoading, isError, refetch } = useReports(page)
  const exportCsv = useExportCsv()
  const exportPdf = useExportPdf()
  const deleteRep = useDeleteReport()

  const handleDelete = () => {
    if (!deleteTarget) return
    deleteRep.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
  }

  if (isLoading) return <PageSpinner />
  if (isError) return (
    <div className="flex flex-col items-center justify-center h-64 gap-3">
      <p className="text-red-500">Failed to load reports</p>
      <Button variant="secondary" icon={<RefreshCw size={14} />} onClick={() => refetch()}>Retry</Button>
    </div>
  )

  const reports = data?.content ?? []

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="max-w-screen-xl mx-auto space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-gray-900 dark:text-white">Reports</h2>
          <p className="text-sm text-gray-500">{data?.totalElements ?? 0} generated reports</p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="secondary" size="sm"
            icon={<Download size={14} />}
            loading={exportCsv.isPending}
            onClick={() => exportCsv.mutate({ daysBack: 30 })}
          >
            Export CSV
          </Button>
          <Button
            size="sm"
            icon={<Download size={14} />}
            loading={exportPdf.isPending}
            onClick={() => exportPdf.mutate({ daysBack: 30 })}
          >
            Export PDF
          </Button>
        </div>
      </div>

      <Card padding={false}>
        {reports.length === 0 ? (
          <EmptyState
            icon={<FileText size={24} />}
            title="No reports yet"
            description="Export events to CSV or PDF to generate reports."
          />
        ) : (
          <Table>
            <Thead>
              <Tr>
                <Th>Name</Th><Th>Format</Th><Th>Competitor</Th>
                <Th>Rows</Th><Th>Size</Th><Th>Generated</Th>
                <Th>Downloads</Th><Th>Actions</Th>
              </Tr>
            </Thead>
            <Tbody>
              {reports.map(r => (
                <Tr key={r.id}>
                  <Td>
                    <div className="flex items-center gap-2">
                      <FileText size={14} className="text-brand-500 flex-shrink-0" />
                      <div>
                        <p className="font-medium text-sm text-gray-900 dark:text-white">{r.name}</p>
                        {r.description && <p className="text-xs text-gray-400">{r.description}</p>}
                      </div>
                    </div>
                  </Td>
                  <Td><Badge variant={r.format === 'PDF' ? 'danger' : 'info'}>{r.format}</Badge></Td>
                  <Td><span className="text-xs">{r.competitorName ?? 'All'}</span></Td>
                  <Td><span className="text-xs">{formatNumber(r.rowCount)}</span></Td>
                  <Td><span className="text-xs text-gray-400">{formatBytes(r.fileSizeBytes)}</span></Td>
                  <Td><span className="text-xs text-gray-400">{formatDate(r.generatedAt)}</span></Td>
                  <Td><span className="text-xs text-gray-400">{r.downloadCount}</span></Td>
                  <Td>
                    <Button
                      variant="ghost" size="sm"
                      icon={<Trash2 size={13} />}
                      className="text-red-400 hover:text-red-500"
                      aria-label={`Delete report ${r.name}`}
                      onClick={() => setDeleteTarget(r)}
                    />
                  </Td>
                </Tr>
              ))}
            </Tbody>
          </Table>
        )}
      </Card>

      {(data?.totalPages ?? 0) > 1 && (
        <div className="flex justify-center items-center gap-2">
          <Button variant="secondary" size="sm" disabled={data?.first} onClick={() => setPage(p => p - 1)}>Previous</Button>
          <span className="px-3 py-1.5 text-sm text-gray-500">Page {page + 1} of {data?.totalPages}</span>
          <Button variant="secondary" size="sm" disabled={data?.last}  onClick={() => setPage(p => p + 1)}>Next</Button>
        </div>
      )}

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Report"
        message={`Are you sure you want to delete "${deleteTarget?.name}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteRep.isPending}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </motion.div>
  )
}

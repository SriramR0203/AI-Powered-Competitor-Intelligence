import { apiClient } from './api'
import type { Report, PageResponse } from '../types'

const reportService = {
  getAll: (page = 0, size = 20) =>
    apiClient.get<PageResponse<Report>>('/reports', { params: { page, size } }).then(r => r.data),

  exportCsv: async (competitorId?: number, daysBack = 30) => {
    const response = await apiClient.get('/reports/export/csv', {
      params:       { competitorId: competitorId || undefined, daysBack },
      responseType: 'blob',
    })
    downloadBlob(response.data as Blob, `events-${new Date().toISOString().slice(0, 10)}.csv`)
  },

  exportPdf: async (competitorId?: number, daysBack = 30) => {
    const response = await apiClient.get('/reports/export/pdf', {
      params:       { competitorId: competitorId || undefined, daysBack },
      responseType: 'blob',
    })
    downloadBlob(response.data as Blob, `events-${new Date().toISOString().slice(0, 10)}.pdf`)
  },

  delete: (id: number) => apiClient.delete(`/reports/${id}`),
}

function downloadBlob(blob: Blob, filename: string) {
  const url  = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href     = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

export default reportService

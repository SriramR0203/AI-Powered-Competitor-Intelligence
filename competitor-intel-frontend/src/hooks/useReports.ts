import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import reportService from '../services/report.service'

export const reportKeys = {
  all: (page: number) => ['reports', page] as const,
}

export function useReports(page = 0) {
  return useQuery({
    queryKey: reportKeys.all(page),
    queryFn:  () => reportService.getAll(page),
    placeholderData: (prev) => prev,
  })
}

export function useExportCsv() {
  return useMutation({
    mutationFn: ({ competitorId, daysBack }: { competitorId?: number; daysBack?: number }) =>
      reportService.exportCsv(competitorId, daysBack),
  })
}

export function useExportPdf() {
  return useMutation({
    mutationFn: ({ competitorId, daysBack }: { competitorId?: number; daysBack?: number }) =>
      reportService.exportPdf(competitorId, daysBack),
  })
}

export function useDeleteReport() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => reportService.delete(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['reports'] }),
  })
}

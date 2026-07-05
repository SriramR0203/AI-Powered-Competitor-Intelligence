import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import sourceService, {
  type SourceFilters,
  type CreateSourcePayload,
  type UpdateSourcePayload,
} from '../services/source.service'

export const sourceKeys = {
  all:        (f?: SourceFilters) => ['sources', f ?? {}] as const,
  competitor: (cid: number)       => ['sources', 'competitor', cid] as const,
  detail:     (id: number)        => ['sources', id] as const,
}

export function useSources(filters: SourceFilters = {}) {
  return useQuery({
    queryKey: sourceKeys.all(filters),
    queryFn:  () => sourceService.getAll(filters),
    placeholderData: (prev) => prev,
  })
}

export function useSourcesByCompetitor(competitorId: number) {
  return useQuery({
    queryKey: sourceKeys.competitor(competitorId),
    queryFn:  () => sourceService.getByCompetitor(competitorId),
    enabled:  !!competitorId,
  })
}

export function useCreateSource() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateSourcePayload) => sourceService.create(payload),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['sources'] }),
  })
}

export function useUpdateSource() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateSourcePayload }) =>
      sourceService.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['sources'] }),
  })
}

export function useDeleteSource() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => sourceService.delete(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['sources'] }),
  })
}

export function useTriggerScrape() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => sourceService.scrape(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['sources'] }),
  })
}

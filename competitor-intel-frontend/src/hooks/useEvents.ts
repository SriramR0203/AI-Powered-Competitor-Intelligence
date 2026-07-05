import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import intelligenceService, { type EventFilters } from '../services/intelligence.service'

export const eventKeys = {
  all:    (f?: EventFilters) => ['events', f ?? {}] as const,
  detail: (id: number)       => ['events', id] as const,
}

export function useEvents(filters: EventFilters = {}) {
  return useQuery({
    queryKey: eventKeys.all(filters),
    queryFn:  () => intelligenceService.getAll(filters),
    placeholderData: (prev) => prev,
  })
}

export function useEvent(id: number) {
  return useQuery({
    queryKey: eventKeys.detail(id),
    queryFn:  () => intelligenceService.getById(id),
    enabled:  !!id,
  })
}

export function useEnrichEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => intelligenceService.enrich(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['events'] }),
  })
}

export function useReprocessEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => intelligenceService.reprocess(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['events'] }),
  })
}

export function useFlagEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) =>
      intelligenceService.flag(id, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['events'] }),
  })
}

export function useProcessRaw() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: intelligenceService.processRaw,
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['events'] }),
  })
}

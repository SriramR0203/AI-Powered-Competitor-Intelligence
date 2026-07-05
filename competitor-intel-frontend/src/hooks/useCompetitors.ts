import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import competitorService, {
  type CompetitorFilters,
  type CreateCompetitorPayload,
  type UpdateCompetitorPayload,
} from '../services/competitor.service'

export const competitorKeys = {
  all:        (f?: CompetitorFilters) => ['competitors', f ?? {}] as const,
  active:     ()                      => ['competitors', 'active'] as const,
  industries: ()                      => ['competitors', 'industries'] as const,
  detail:     (id: number)            => ['competitors', id] as const,
}

export function useCompetitors(filters: CompetitorFilters = {}) {
  return useQuery({
    queryKey: competitorKeys.all(filters),
    queryFn:  () => competitorService.getAll(filters),
    placeholderData: (prev) => prev,
  })
}

export function useActiveCompetitors() {
  return useQuery({ queryKey: competitorKeys.active(), queryFn: competitorService.getActive })
}

export function useIndustries() {
  return useQuery({ queryKey: competitorKeys.industries(), queryFn: competitorService.getIndustries })
}

export function useCompetitor(id: number) {
  return useQuery({
    queryKey: competitorKeys.detail(id),
    queryFn:  () => competitorService.getById(id),
    enabled:  !!id,
  })
}

export function useCreateCompetitor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateCompetitorPayload) => competitorService.create(payload),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['competitors'] }),
  })
}

export function useUpdateCompetitor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCompetitorPayload }) =>
      competitorService.update(id, data),
    onSuccess: (_d, { id }) => {
      qc.invalidateQueries({ queryKey: ['competitors'] })
      qc.invalidateQueries({ queryKey: competitorKeys.detail(id) })
    },
  })
}

export function useArchiveCompetitor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => competitorService.archive(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['competitors'] }),
  })
}

export function useActivateCompetitor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => competitorService.activate(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['competitors'] }),
  })
}

export function useDeleteCompetitor() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => competitorService.delete(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['competitors'] }),
  })
}

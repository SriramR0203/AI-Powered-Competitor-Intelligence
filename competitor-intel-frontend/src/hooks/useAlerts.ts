import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import alertService, { type CreateAlertRulePayload } from '../services/alert.service'

export const alertKeys = {
  rules:         ()             => ['alertRules'] as const,
  rule:          (id: number)   => ['alertRules', id] as const,
  notifications: (p: number)   => ['alertNotifications', p] as const,
  unread:        ()             => ['alertUnread'] as const,
}

export function useAlertRules() {
  return useQuery({ queryKey: alertKeys.rules(), queryFn: alertService.getRules })
}

export function useAlertNotifications(page = 0) {
  return useQuery({
    queryKey: alertKeys.notifications(page),
    queryFn:  () => alertService.getNotifications(page),
    placeholderData: (prev) => prev,
  })
}

export function useUnreadCount() {
  return useQuery({
    queryKey: alertKeys.unread(),
    queryFn:  alertService.getUnreadCount,
    refetchInterval: 30_000,
  })
}

export function useCreateAlertRule() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateAlertRulePayload) => alertService.createRule(payload),
    onSuccess:  () => qc.invalidateQueries({ queryKey: alertKeys.rules() }),
  })
}

export function useDeleteAlertRule() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => alertService.deleteRule(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: alertKeys.rules() }),
  })
}

export function useEnableRule() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => alertService.enableRule(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: alertKeys.rules() }),
  })
}

export function useDisableRule() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => alertService.disableRule(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: alertKeys.rules() }),
  })
}

export function useAcknowledgeNotification() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => alertService.acknowledge(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['alertNotifications'] })
      qc.invalidateQueries({ queryKey: alertKeys.unread() })
    },
  })
}

export function useAcknowledgeAll() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: alertService.acknowledgeAll,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['alertNotifications'] })
      qc.invalidateQueries({ queryKey: alertKeys.unread() })
    },
  })
}

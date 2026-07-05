import { apiClient } from './api'
import type { AlertRule, AlertNotification, PageResponse } from '../types'

export interface CreateAlertRulePayload {
  name:              string
  description?:      string
  competitorId?:     number
  categoryFilter?:   string
  sentimentFilter?:  string
  keywordFilter?:    string
  minRelevanceScore?: number
  minImportanceScore?: number
  severity?:         string
  notifyEmail?:      boolean
  notifyInApp?:      boolean
  cooldownMinutes?:  number
}

const alertService = {
  // Rules
  getRules: () =>
    apiClient.get<AlertRule[]>('/alerts/rules').then(r => r.data),

  getRuleById: (id: number) =>
    apiClient.get<AlertRule>(`/alerts/rules/${id}`).then(r => r.data),

  createRule: (payload: CreateAlertRulePayload) =>
    apiClient.post<AlertRule>('/alerts/rules', payload).then(r => r.data),

  enableRule:  (id: number) => apiClient.patch(`/alerts/rules/${id}/enable`),
  disableRule: (id: number) => apiClient.patch(`/alerts/rules/${id}/disable`),
  deleteRule:  (id: number) => apiClient.delete(`/alerts/rules/${id}`),

  // Notifications
  getNotifications: (page = 0, size = 20) =>
    apiClient.get<PageResponse<AlertNotification>>('/alerts/notifications', {
      params: { page, size },
    }).then(r => r.data),

  getUnreadCount: () =>
    apiClient.get<number>('/alerts/notifications/unread-count').then(r => r.data),

  acknowledge: (id: number) =>
    apiClient.patch(`/alerts/notifications/${id}/acknowledge`),

  acknowledgeAll: () =>
    apiClient.patch('/alerts/notifications/acknowledge-all'),
}

export default alertService

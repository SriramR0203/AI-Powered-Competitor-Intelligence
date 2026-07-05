import { apiClient } from './api'
import type { IntelligenceEvent, PageResponse } from '../types'

export interface EventFilters {
  competitorId?:    number
  category?:        string
  sentiment?:       string
  processingStatus?: string
  from?:            string
  to?:              string
  search?:          string
  page?:            number
  size?:            number
  sortBy?:          string
  sortDir?:         'asc' | 'desc'
}

const intelligenceService = {
  getAll: (filters: EventFilters = {}) =>
    apiClient.get<PageResponse<IntelligenceEvent>>('/events', {
      params: {
        competitorId:    filters.competitorId    || undefined,
        category:        filters.category && filters.category !== 'ALL' ? filters.category : undefined,
        sentiment:       filters.sentiment && filters.sentiment !== 'ALL' ? filters.sentiment : undefined,
        processingStatus: filters.processingStatus && filters.processingStatus !== 'ALL' ? filters.processingStatus : undefined,
        from:            filters.from    || undefined,
        to:              filters.to      || undefined,
        search:          filters.search  || undefined,
        page:            filters.page   ?? 0,
        size:            filters.size   ?? 20,
        sortBy:          filters.sortBy  ?? 'publishedAt',
        sortDir:         filters.sortDir ?? 'desc',
      },
    }).then(r => r.data),

  getById: (id: number) =>
    apiClient.get<IntelligenceEvent>(`/events/${id}`).then(r => r.data),

  enrich:   (id: number) => apiClient.post(`/events/${id}/enrich`),
  reprocess: (id: number) => apiClient.post(`/events/${id}/reprocess`),

  flag: (id: number, reason: string) =>
    apiClient.patch(`/events/${id}/flag`, null, { params: { reason } }),

  unflag: (id: number) => apiClient.patch(`/events/${id}/unflag`),

  processRaw: () => apiClient.post('/events/process-raw'),
}

export default intelligenceService

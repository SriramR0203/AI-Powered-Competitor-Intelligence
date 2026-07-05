import { apiClient } from './api'
import type { IntelligenceSource, PageResponse } from '../types'

export interface SourceFilters { competitorId?: number; page?: number; size?: number }

export interface CreateSourcePayload {
  competitorId:       number
  name:               string
  url:                string
  sourceType:         string
  scrapeIntervalHours?: number
  cssSelector?:       string
  xpathSelector?:     string
  requiresJavascript?: boolean
  httpHeaders?:       string
  notes?:             string
}
export type UpdateSourcePayload = Partial<Omit<CreateSourcePayload, 'competitorId'>>

const sourceService = {
  getAll: (filters: SourceFilters = {}) =>
    apiClient.get<PageResponse<IntelligenceSource>>('/sources', {
      params: {
        competitorId: filters.competitorId || undefined,
        page: filters.page ?? 0,
        size: filters.size ?? 50,
      },
    }).then(r => r.data),

  getByCompetitor: (competitorId: number) =>
    apiClient.get<IntelligenceSource[]>(`/sources/competitor/${competitorId}`).then(r => r.data),

  getById: (id: number) =>
    apiClient.get<IntelligenceSource>(`/sources/${id}`).then(r => r.data),

  create: (payload: CreateSourcePayload) =>
    apiClient.post<IntelligenceSource>('/sources', payload).then(r => r.data),

  update: (id: number, payload: UpdateSourcePayload) =>
    apiClient.put<IntelligenceSource>(`/sources/${id}`, payload).then(r => r.data),

  enable:  (id: number) => apiClient.patch(`/sources/${id}/enable`),
  disable: (id: number) => apiClient.patch(`/sources/${id}/disable`),
  delete:  (id: number) => apiClient.delete(`/sources/${id}`),
  scrape:  (id: number) => apiClient.post(`/sources/${id}/scrape`),
}

export default sourceService

import { apiClient } from './api'
import type { Competitor, CompetitorSummary, PageResponse } from '../types'

export interface CompetitorFilters {
  search?:   string
  status?:   string
  industry?: string
  page?:     number
  size?:     number
  sortBy?:   string
  sortDir?:  'asc' | 'desc'
}

export interface CreateCompetitorPayload {
  name:                string
  websiteUrl:          string
  description?:        string
  industry?:           string
  headquarters?:       string
  employeeCountRange?: string
  foundedYear?:        number
  linkedinUrl?:        string
  twitterHandle?:      string
  logoUrl?:            string
  status?:             string
  priorityScore?:      number
  notes?:              string
}
export type UpdateCompetitorPayload = Partial<CreateCompetitorPayload>

const competitorService = {
  getAll: (filters: CompetitorFilters = {}) =>
    apiClient.get<PageResponse<Competitor>>('/competitors', {
      params: {
        search:   filters.search   || undefined,
        status:   filters.status !== 'ALL' ? filters.status : undefined,
        industry: filters.industry || undefined,
        page:     filters.page    ?? 0,
        size:     filters.size    ?? 20,
        sortBy:   filters.sortBy  ?? 'priorityScore',
        sortDir:  filters.sortDir ?? 'desc',
      },
    }).then(r => r.data),

  getActive: () =>
    apiClient.get<CompetitorSummary[]>('/competitors/active').then(r => r.data),

  getIndustries: () =>
    apiClient.get<string[]>('/competitors/industries').then(r => r.data),

  getById: (id: number) =>
    apiClient.get<Competitor>(`/competitors/${id}`).then(r => r.data),

  create: (payload: CreateCompetitorPayload) =>
    apiClient.post<Competitor>('/competitors', payload).then(r => r.data),

  update: (id: number, payload: UpdateCompetitorPayload) =>
    apiClient.put<Competitor>(`/competitors/${id}`, payload).then(r => r.data),

  archive: (id: number) =>
    apiClient.patch(`/competitors/${id}/archive`),

  activate: (id: number) =>
    apiClient.patch(`/competitors/${id}/activate`),

  delete: (id: number) =>
    apiClient.delete(`/competitors/${id}`),
}

export default competitorService

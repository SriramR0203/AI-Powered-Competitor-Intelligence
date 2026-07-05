import { apiClient } from './api'
import type { DashboardStats } from '../types'

const dashboardService = {
  getStats: (daysBack = 30) =>
    apiClient.get<DashboardStats>('/dashboard/stats', { params: { daysBack } }).then(r => r.data),
}

export default dashboardService

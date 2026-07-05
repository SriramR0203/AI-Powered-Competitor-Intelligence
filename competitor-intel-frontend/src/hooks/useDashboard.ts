import { useQuery } from '@tanstack/react-query'
import dashboardService from '../services/dashboard.service'

export function useDashboard(daysBack = 30) {
  return useQuery({
    queryKey: ['dashboardStats', daysBack],
    queryFn:  () => dashboardService.getStats(daysBack),
    staleTime: 1000 * 60 * 2,
  })
}

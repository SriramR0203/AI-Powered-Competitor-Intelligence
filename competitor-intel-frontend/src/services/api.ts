/**
 * Central Axios instance — single source of truth for all HTTP calls.
 * Handles: JWT attachment, 401 redirect, timeout, error normalisation.
 * Import `apiClient` in every service module. Never create another instance.
 */
import axios, {
  type AxiosError,
  type InternalAxiosRequestConfig,
} from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL}/api/v1`
  : '/api/v1'

const TIMEOUT = Number(import.meta.env.VITE_API_TIMEOUT) || 15_000

export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
  headers: { 'Content-Type': 'application/json' },
})

// ── Request: attach JWT ───────────────────────────────────────────────────
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('ci_token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (err) => Promise.reject(err),
)

// ── Response: 401 → clear session & redirect ─────────────────────────────
apiClient.interceptors.response.use(
  (res) => res,
  (err: AxiosError) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('ci_token')
      localStorage.removeItem('ci_user')
      if (window.location.pathname !== '/login') window.location.href = '/login'
    }
    return Promise.reject(err)
  },
)

/** Normalise any Axios error into a human-readable string. */
export function extractApiError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as Record<string, unknown> | undefined
    if (data?.detail)  return String(data.detail)
    if (data?.message) return String(data.message)
    if (data?.error)   return String(data.error)
    if (error.code === 'ECONNABORTED') return 'Request timed out — please try again.'
    if (!error.response)               return 'Network error — check your connection.'
    return `Server error (${error.response.status})`
  }
  return 'An unexpected error occurred.'
}

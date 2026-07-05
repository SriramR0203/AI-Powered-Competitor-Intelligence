import { apiClient } from './api'
import type { AuthResponse, User } from '../types'

export interface LoginPayload   { usernameOrEmail: string; password: string }
export interface RefreshPayload { refreshToken: string }

const authService = {
  login: (payload: LoginPayload) =>
    apiClient.post<AuthResponse>('/auth/login', payload).then(r => r.data),

  refresh: (payload: RefreshPayload) =>
    apiClient.post<AuthResponse>('/auth/refresh', payload).then(r => r.data),

  me: () =>
    apiClient.get<User>('/users/me').then(r => r.data),
}

export default authService

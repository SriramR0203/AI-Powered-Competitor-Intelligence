import { apiClient } from './api'
import type { User, PageResponse } from '../types'

export interface UpdateProfilePayload {
  email?:     string
  firstName?: string
  lastName?:  string
}

export interface ChangePasswordPayload {
  currentPassword: string
  newPassword:     string
}

const userService = {
  getMe: () =>
    apiClient.get<User>('/users/me').then(r => r.data),

  getAll: (search?: string, page = 0, size = 20) =>
    apiClient.get<PageResponse<User>>('/users', {
      params: { search: search || undefined, page, size },
    }).then(r => r.data),

  getById: (id: number) =>
    apiClient.get<User>(`/users/${id}`).then(r => r.data),

  update: (id: number, payload: UpdateProfilePayload) =>
    apiClient.put<User>(`/users/${id}`, payload).then(r => r.data),

  changePassword: (payload: ChangePasswordPayload) =>
    apiClient.patch('/users/me/password', payload),

  activate:   (id: number) => apiClient.patch(`/users/${id}/activate`),
  deactivate: (id: number) => apiClient.patch(`/users/${id}/deactivate`),
  delete:     (id: number) => apiClient.delete(`/users/${id}`),
}

export default userService

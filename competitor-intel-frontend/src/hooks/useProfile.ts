import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import userService, {
  type UpdateProfilePayload,
  type ChangePasswordPayload,
} from '../services/user.service'
import { useAuth } from '../context/AuthContext'

export function useProfile() {
  return useQuery({ queryKey: ['profile'], queryFn: userService.getMe })
}

export function useUpdateProfile() {
  const qc = useQueryClient()
  const { user } = useAuth()
  return useMutation({
    mutationFn: (payload: UpdateProfilePayload) =>
      userService.update(user?.id ?? 0, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['profile'] }),
  })
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (payload: ChangePasswordPayload) => userService.changePassword(payload),
  })
}

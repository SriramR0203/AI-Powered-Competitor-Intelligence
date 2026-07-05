import { motion } from 'framer-motion'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { User, Mail, Calendar, Shield, CheckCircle, AlertCircle } from 'lucide-react'
import { useState } from 'react'
import { useProfile, useUpdateProfile, useChangePassword } from '../hooks/useProfile'
import { useAuth } from '../context/AuthContext'
import { Card, CardHeader } from '../components/ui/Card'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Avatar } from '../components/ui/Avatar'
import { Badge } from '../components/ui/Badge'
import { PageSpinner } from '../components/ui/Spinner'
import { formatDate } from '../utils/formatters'
import { extractApiError } from '../services/api'

const profileSchema = z.object({
  firstName: z.string().min(1, 'Required'),
  lastName:  z.string().min(1, 'Required'),
  email:     z.string().email('Valid email required'),
})
const passwordSchema = z.object({
  currentPassword: z.string().min(1, 'Required'),
  newPassword:     z.string().min(8, 'At least 8 characters')
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/, 'Must include upper, lower, digit and symbol'),
})
type ProfileData  = z.infer<typeof profileSchema>
type PasswordData = z.infer<typeof passwordSchema>

export function ProfilePage() {
  const { user: authUser } = useAuth()
  const { data: profile, isLoading } = useProfile()
  const updateProfile = useUpdateProfile()
  const changePassword = useChangePassword()
  const [profileMsg, setProfileMsg] = useState<{ type: 'ok'|'err'; text: string } | null>(null)
  const [pwMsg,      setPwMsg]      = useState<{ type: 'ok'|'err'; text: string } | null>(null)

  const u = profile ?? authUser

  const {
    register: regP, handleSubmit: handleP, formState: { errors: errP, isSubmitting: subP },
  } = useForm<ProfileData>({
    resolver: zodResolver(profileSchema),
    values: { firstName: u?.firstName ?? '', lastName: u?.lastName ?? '', email: u?.email ?? '' },
  })

  const {
    register: regPw, handleSubmit: handlePw, reset: resetPw, formState: { errors: errPw, isSubmitting: subPw },
  } = useForm<PasswordData>({ resolver: zodResolver(passwordSchema) })

  const onProfile = async (data: ProfileData) => {
    try {
      await updateProfile.mutateAsync(data)
      setProfileMsg({ type: 'ok', text: 'Profile updated successfully.' })
    } catch (err) { setProfileMsg({ type: 'err', text: extractApiError(err) }) }
  }

  const onPassword = async (data: PasswordData) => {
    try {
      await changePassword.mutateAsync(data)
      setPwMsg({ type: 'ok', text: 'Password changed successfully.' })
      resetPw()
    } catch (err) { setPwMsg({ type: 'err', text: extractApiError(err) }) }
  }

  if (isLoading) return <PageSpinner />

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="max-w-2xl mx-auto space-y-5">
      <h2 className="text-lg font-bold text-gray-900 dark:text-white">Profile</h2>

      <Card>
        <div className="flex items-center gap-5">
          <Avatar name={u?.fullName ?? 'User'} size="lg" />
          <div className="flex-1 min-w-0">
            <h3 className="text-base font-bold text-gray-900 dark:text-white">{u?.fullName}</h3>
            <p className="text-sm text-gray-500">{u?.email}</p>
            <div className="flex flex-wrap gap-1.5 mt-2">
              {(u?.roles ?? []).map(r => (
                <Badge key={r} variant="info">{r.replace('ROLE_', '')}</Badge>
              ))}
            </div>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4 mt-5 text-sm">
          {[
            { icon: User,     label: 'Username',    value: u?.username },
            { icon: Mail,     label: 'Email',        value: u?.email },
            { icon: Calendar, label: 'Member Since', value: formatDate(u?.createdAt) },
            { icon: Shield,   label: 'Verified',     value: u?.emailVerified ? 'Yes' : 'No' },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-xl bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                <Icon size={14} className="text-gray-500" />
              </div>
              <div>
                <p className="text-xs text-gray-400">{label}</p>
                <p className="font-medium text-gray-900 dark:text-white">{value ?? '—'}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>

      <Card>
        <CardHeader title="Edit Profile" />
        {profileMsg && (
          <div className={`mb-4 p-3 rounded-xl flex items-center gap-2 text-sm ${profileMsg.type === 'ok' ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-900/20' : 'bg-red-50 text-red-600 dark:bg-red-900/20'}`}>
            {profileMsg.type === 'ok' ? <CheckCircle size={14} /> : <AlertCircle size={14} />}
            {profileMsg.text}
          </div>
        )}
        <form onSubmit={handleP(onProfile)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" {...regP('firstName')} error={errP.firstName?.message} />
            <Input label="Last Name"  {...regP('lastName')}  error={errP.lastName?.message} />
          </div>
          <Input label="Email" type="email" {...regP('email')} error={errP.email?.message} />
          <div className="flex justify-end">
            <Button type="submit" loading={subP}>Save Changes</Button>
          </div>
        </form>
      </Card>

      <Card>
        <CardHeader title="Change Password" />
        {pwMsg && (
          <div className={`mb-4 p-3 rounded-xl flex items-center gap-2 text-sm ${pwMsg.type === 'ok' ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-900/20' : 'bg-red-50 text-red-600 dark:bg-red-900/20'}`}>
            {pwMsg.type === 'ok' ? <CheckCircle size={14} /> : <AlertCircle size={14} />}
            {pwMsg.text}
          </div>
        )}
        <form onSubmit={handlePw(onPassword)} className="space-y-4">
          <Input label="Current Password" type="password" {...regPw('currentPassword')} error={errPw.currentPassword?.message} />
          <Input label="New Password"     type="password" {...regPw('newPassword')}     error={errPw.newPassword?.message} />
          <div className="flex justify-end">
            <Button type="submit" loading={subPw}>Change Password</Button>
          </div>
        </form>
      </Card>
    </motion.div>
  )
}

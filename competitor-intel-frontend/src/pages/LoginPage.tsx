import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { TrendingUp, Eye, EyeOff, AlertCircle } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import authService from '../services/auth.service'
import { extractApiError } from '../services/api'

const schema = z.object({
  usernameOrEmail: z.string().min(1, 'Required'),
  password:        z.string().min(1, 'Required'),
})
type FormData = z.infer<typeof schema>

export function LoginPage() {
  const { login } = useAuth()
  const navigate   = useNavigate()
  const location   = useLocation()
  const from       = (location.state as { from?: string })?.from ?? '/dashboard'
  const [showPw, setShowPw] = useState(false)
  const [apiError, setApiError] = useState('')

  const {
    register, handleSubmit, formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: FormData) => {
    setApiError('')
    try {
      const res = await authService.login(data)
      login(res.accessToken, res.user)
      navigate(from, { replace: true })
    } catch (err) {
      setApiError(extractApiError(err))
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-950 via-blue-950 to-gray-900 p-4 relative overflow-hidden">
      <div className="absolute top-[-10%] right-[-5%] w-96 h-96 bg-brand-600/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-[-10%] left-[-5%] w-80 h-80 bg-purple-600/20 rounded-full blur-3xl pointer-events-none" />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="w-full max-w-sm"
      >
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-brand-600 flex items-center justify-center shadow-xl shadow-brand-600/30 mb-4">
            <TrendingUp size={28} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white">Competitor Intel</h1>
          <p className="text-sm text-gray-400 mt-1">AI-Powered Intelligence Platform</p>
        </div>

        <div className="bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl p-7 shadow-2xl">
          <h2 className="text-base font-semibold text-white mb-5">Sign in to your account</h2>

          {apiError && (
            <div className="mb-4 p-3 bg-red-500/10 border border-red-500/30 rounded-xl text-sm text-red-400 flex items-start gap-2">
              <AlertCircle size={15} className="mt-0.5 flex-shrink-0" />
              {apiError}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-1.5">Username or Email</label>
              <input
                {...register('usernameOrEmail')}
                autoComplete="username"
                className="w-full px-4 py-2.5 bg-white/10 border border-white/20 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-brand-500 text-sm"
                placeholder="admin"
              />
              {errors.usernameOrEmail && <p className="mt-1 text-xs text-red-400">{errors.usernameOrEmail.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-1.5">Password</label>
              <div className="relative">
                <input
                  {...register('password')}
                  type={showPw ? 'text' : 'password'}
                  autoComplete="current-password"
                  className="w-full px-4 py-2.5 pr-10 bg-white/10 border border-white/20 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-brand-500 text-sm"
                  placeholder="••••••••"
                />
                <button type="button" onClick={() => setShowPw(v => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white transition-colors">
                  {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.password && <p className="mt-1 text-xs text-red-400">{errors.password.message}</p>}
            </div>

            <button type="submit" disabled={isSubmitting}
              className="w-full py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-sm font-semibold transition-all flex items-center justify-center gap-2 mt-2 disabled:opacity-60">
              {isSubmitting && <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />}
              {isSubmitting ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <p className="mt-5 text-xs text-center text-gray-500">
            Contact your administrator for access credentials.
          </p>
        </div>
      </motion.div>
    </div>
  )
}

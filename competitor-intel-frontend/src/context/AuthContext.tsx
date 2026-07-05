import React, { createContext, useContext, useState, useCallback } from 'react'
import type { User } from '../types'

interface AuthContextValue {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  login: (token: string, user: User) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken]  = useState<string | null>(() => localStorage.getItem('ci_token'))
  const [user,  setUser]   = useState<User | null>(() => {
    const stored = localStorage.getItem('ci_user')
    return stored ? (JSON.parse(stored) as User) : null
  })

  const login = useCallback((t: string, u: User) => {
    localStorage.setItem('ci_token', t)
    localStorage.setItem('ci_user',  JSON.stringify(u))
    setToken(t)
    setUser(u)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('ci_token')
    localStorage.removeItem('ci_user')
    setToken(null)
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

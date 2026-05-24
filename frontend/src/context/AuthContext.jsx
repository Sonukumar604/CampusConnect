import { createContext, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import { authService } from '../services/authService'
import { userService } from '../services/userService'

export const AuthContext = createContext(null)

const storageKey = 'cc_user'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [authReady, setAuthReady] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const stored = localStorage.getItem(storageKey)
    if (stored) {
      try {
        setUser(JSON.parse(stored))
      } catch (error) {
        localStorage.removeItem(storageKey)
      }
    }

    const init = async () => {
      try {
        const refreshed = await authService.refresh()
        const nextUser = {
          id: refreshed.userId,
          name: refreshed.name,
          email: refreshed.email,
          role: refreshed.role
        }
        setUser(nextUser)
        localStorage.setItem(storageKey, JSON.stringify(nextUser))
      } catch (error) {
        setUser((current) => current || null)
      } finally {
        setAuthReady(true)
      }
    }

    init()
  }, [])

  const login = async (payload) => {
    setLoading(true)
    try {
      const data = await authService.login(payload)
      const nextUser = {
        id: data.userId,
        name: data.name,
        email: data.email,
        role: data.role
      }
      setUser(nextUser)
      localStorage.setItem(storageKey, JSON.stringify(nextUser))
      toast.success('Welcome back')
      return data
    } catch (error) {
      toast.error('Login failed. Check your credentials.')
      throw error
    } finally {
      setLoading(false)
    }
  }

  const register = async (payload) => {
    setLoading(true)
    try {
      const data = await authService.register(payload)
      toast.success('Account created. Please verify your email.')
      return data
    } catch (error) {
      toast.error('Registration failed. Try again.')
      throw error
    } finally {
      setLoading(false)
    }
  }

  const logout = async () => {
    setLoading(true)
    try {
      await authService.logout()
    } catch (error) {
      // ignore
    } finally {
      setUser(null)
      localStorage.removeItem(storageKey)
      setLoading(false)
    }
  }

  const refreshProfile = async () => {
    try {
      const profile = await userService.getCurrentUser()
      const nextUser = {
        id: profile.id,
        name: profile.name,
        email: profile.email,
        role: profile.role
      }
      setUser(nextUser)
      localStorage.setItem(storageKey, JSON.stringify(nextUser))
      return nextUser
    } catch (error) {
      return null
    }
  }

  const value = useMemo(
    () => ({
      user,
      loading,
      authReady,
      isAuthenticated: Boolean(user),
      login,
      register,
      logout,
      refreshProfile,
      setUser
    }),
    [user, loading, authReady]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

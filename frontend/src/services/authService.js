import { authClient } from './apiClient'

export const authService = {
  async login(payload) {
    const { data } = await authClient.post('/api/auth/login', payload)
    return data
  },
  async register(payload) {
    const { data } = await authClient.post('/api/auth/signup', payload)
    return data
  },
  async logout() {
    const { data } = await authClient.post('/api/auth/logout')
    return data
  },
  async refresh() {
    const { data } = await authClient.post('/api/auth/refresh')
    return data
  },
  async verifyEmail(token) {
    const { data } = await authClient.get('/api/auth/verify-email', {
      params: { token }
    })
    return data
  },
  async forgotPassword(email) {
    const { data } = await authClient.post('/api/auth/forgot-password', null, {
      params: { email }
    })
    return data
  },
  async resetPassword(token, newPassword) {
    const { data } = await authClient.post('/api/auth/reset-password', null, {
      params: { token, newPassword }
    })
    return data
  }
}

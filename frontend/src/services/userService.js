import { apiClient } from './apiClient'

const unwrap = (response) => response?.data?.data ?? response?.data

export const userService = {
  async getCurrentUser() {
    const response = await apiClient.get('/api/users/me')
    return unwrap(response)
  },
  async updateProfile(userId, payload) {
    const response = await apiClient.put(`/api/users/${userId}`, payload)
    return unwrap(response)
  },
  async getAllUsers() {
    const response = await apiClient.get('/api/users')
    return unwrap(response)
  }
}

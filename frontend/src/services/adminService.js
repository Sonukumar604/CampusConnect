import { apiClient } from './apiClient'

export const adminService = {
  async getAnalytics() {
    const { data } = await apiClient.get('/api/admin/analytics')
    return data
  },
  async listUsers() {
    const { data } = await apiClient.get('/api/admin/users')
    return data
  },
  async listUsersPaged(params) {
    const { data } = await apiClient.get('/api/admin/users/paged', { params })
    return data
  },
  async updateUserRole(userId, role) {
    const { data } = await apiClient.put(`/api/admin/users/${userId}/role`, null, {
      params: { role }
    })
    return data
  },
  async blockUser(userId) {
    const { data } = await apiClient.put(`/api/admin/users/${userId}/block`)
    return data
  },
  async unblockUser(userId) {
    const { data } = await apiClient.put(`/api/admin/users/${userId}/unblock`)
    return data
  }
}

import { apiClient } from './apiClient'

export const notificationService = {
  async list() {
    const { data } = await apiClient.get('/api/notifications')
    return data
  },
  async markRead(id) {
    const { data } = await apiClient.post(`/api/notifications/${id}/read`)
    return data
  }
}

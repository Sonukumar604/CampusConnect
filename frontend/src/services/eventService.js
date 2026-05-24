import { apiClient } from './apiClient'

export const eventService = {
  async listPublished(params) {
    const { data } = await apiClient.get('/api/user/events/published', { params })
    return data
  },
  async listUpcoming(params) {
    const { data } = await apiClient.get('/api/user/events/upcoming', { params })
    return data
  },
  async filter(params) {
    const { data } = await apiClient.get('/api/user/events/filter', { params })
    return data
  },
  async getById(id) {
    const { data } = await apiClient.get(`/api/user/events/${id}`)
    return data
  },
  async register(eventId, userId, payload) {
    const { data } = await apiClient.post(
      `/api/events/registrations/${eventId}/user/${userId}`,
      payload
    )
    return data
  },
  async getUserRegistrations(userId) {
    const { data } = await apiClient.get(`/api/events/registrations/user/${userId}`)
    return data
  }
}

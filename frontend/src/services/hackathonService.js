import { apiClient } from './apiClient'

export const hackathonService = {
  async listAll() {
    const { data } = await apiClient.get('/api/hackathons')
    return data
  },
  async filter(params) {
    const { data } = await apiClient.get('/api/hackathons/filter', { params })
    return data
  },
  async getPaged(params) {
    const { data } = await apiClient.get('/api/hackathons/paged', { params })
    return data
  },
  async filterPaged(params) {
    const { data } = await apiClient.get('/api/hackathons/filter/paged', { params })
    return data
  },
  async getById(id) {
    const { data } = await apiClient.get(`/api/hackathons/${id}`)
    return data
  },
  async register(hackathonId, userId, payload) {
    const { data } = await apiClient.post(
      `/api/hackathons/registrations/${hackathonId}/user/${userId}`,
      payload
    )
    return data
  },
  async getUserRegistrations(userId) {
    const { data } = await apiClient.get(`/api/hackathons/registrations/user/${userId}`)
    return data
  }
}

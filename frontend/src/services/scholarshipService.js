import { apiClient } from './apiClient'

export const scholarshipService = {
  async listPublished(params) {
    const { data } = await apiClient.get('/api/scholarships/published', { params })
    return data
  },
  async filter(params) {
    const { data } = await apiClient.get('/api/scholarships/filter', { params })
    return data
  },
  async getById(id) {
    const { data } = await apiClient.get(`/api/scholarships/${id}`)
    return data
  },
  async apply(userId, payload) {
    const { data } = await apiClient.post(
      `/api/user/scholarship-applications/${userId}/apply`,
      payload
    )
    return data
  },
  async listMyApplications(userId) {
    const { data } = await apiClient.get(`/api/user/scholarship-applications/${userId}`)
    return data
  }
}

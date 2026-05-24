import { apiClient } from './apiClient'

export const internshipService = {
  async listPaged(params) {
    const { data } = await apiClient.get('/api/internships', { params })
    return data
  },
  async filterByLocation(params) {
    const { data } = await apiClient.get('/api/internships/location', { params })
    return data
  },
  async filterByType(params) {
    const { data } = await apiClient.get('/api/internships/type', { params })
    return data
  },
  async search(params) {
    const { data } = await apiClient.get('/api/internships/search', { params })
    return data
  }
}

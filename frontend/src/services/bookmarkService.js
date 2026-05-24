import { apiClient } from './apiClient'

export const bookmarkService = {
  async create(payload) {
    const { data } = await apiClient.post('/api/bookmarks', payload)
    return data
  },
  async list() {
    const { data } = await apiClient.get('/api/bookmarks')
    return data
  },
  async remove(id) {
    const { data } = await apiClient.delete(`/api/bookmarks/${id}`)
    return data
  }
}

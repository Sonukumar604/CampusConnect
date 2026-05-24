import { apiClient } from './apiClient'

export const courseService = {
  async listPaged(params) {
    const { data } = await apiClient.get('/api/user/courses', { params })
    return data
  },
  async getFree() {
    const { data } = await apiClient.get('/api/user/courses/free')
    return data
  },
  async filterByDomain(domain) {
    const { data } = await apiClient.get(`/api/user/courses/filter/domain/${domain}`)
    return data
  },
  async filterByTechnology(tech) {
    const { data } = await apiClient.get(`/api/user/courses/filter/technology/${tech}`)
    return data
  },
  async filterByInstructor(instructor) {
    const { data } = await apiClient.get(`/api/user/courses/filter/instructor/${instructor}`)
    return data
  },
  async filterByType(type) {
    const { data } = await apiClient.get(`/api/user/courses/filter/type/${type}`)
    return data
  },
  async getById(id) {
    const { data } = await apiClient.get(`/api/user/courses/${id}`)
    return data
  },
  async enroll(courseId, userId, payload) {
    const { data } = await apiClient.post(
      `/api/user/courses/${courseId}/enroll/${userId}`,
      payload
    )
    return data
  },
  async getEnrollments(userId) {
    const { data } = await apiClient.get(`/api/user/courses/enrollments/${userId}`)
    return data
  }
}

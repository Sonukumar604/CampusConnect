import { useEffect, useState } from 'react'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { courseService } from '../services/courseService'
import { useAuth } from '../hooks/useAuth'
import toast from 'react-hot-toast'

export default function CoursesPage() {
  const { user } = useAuth()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ domain: '', technology: '', instructor: '' })

  const loadCourses = async () => {
    setLoading(true)
    try {
      let data
      if (filters.domain) {
        data = await courseService.filterByDomain(filters.domain)
      } else if (filters.technology) {
        data = await courseService.filterByTechnology(filters.technology)
      } else if (filters.instructor) {
        data = await courseService.filterByInstructor(filters.instructor)
      } else {
        data = await courseService.listPaged({ page: 0, size: 8 })
        data = data.content || []
      }
      setItems(data)
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCourses()
  }, [])

  const enroll = async (courseId) => {
    if (!user?.id) return
    try {
      await courseService.enroll(courseId, user.id, {
        extra: 'enroll',
        note: 'Enrolled via CampusConnect'
      })
      toast.success('Enrolled successfully')
    } catch (error) {
      toast.error('Enrollment failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Courses" subtitle="Learn faster with curated learning paths." />

      <div className="glass-card rounded-3xl p-6">
        <div className="grid gap-4 md:grid-cols-3">
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Domain"
            value={filters.domain}
            onChange={(event) => setFilters({ ...filters, domain: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Technology"
            value={filters.technology}
            onChange={(event) => setFilters({ ...filters, technology: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Instructor"
            value={filters.instructor}
            onChange={(event) => setFilters({ ...filters, instructor: event.target.value })}
          />
        </div>
        <button
          className="mt-4 rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
          onClick={loadCourses}
        >
          Apply filters
        </button>
      </div>

      {loading ? (
        <LoadingState label="Loading courses" />
      ) : items.length === 0 ? (
        <EmptyState title="No courses found" description="Try a different search." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
              <p className="mt-2 text-sm text-sand-200/70">
                {item.instructor} · {item.platform}
              </p>
              <div className="mt-3 text-xs text-sand-200/70">
                {item.domain} · {item.technology} · {item.level}
              </div>
              <div className="mt-4 flex items-center gap-4">
                <button
                  className="rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
                  onClick={() => enroll(item.id)}
                >
                  Enroll
                </button>
                {item.url && (
                  <a
                    href={item.url}
                    target="_blank"
                    rel="noreferrer"
                    className="text-sm text-tide-200"
                  >
                    View course
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

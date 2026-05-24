import { useEffect, useState } from 'react'
import Topbar from '../components/Topbar'
import StatCard from '../components/StatCard'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { useAuth } from '../hooks/useAuth'
import { adminService } from '../services/adminService'
import { hackathonService } from '../services/hackathonService'
import { internshipService } from '../services/internshipService'
import { eventService } from '../services/eventService'
import { scholarshipService } from '../services/scholarshipService'
import { notificationService } from '../services/notificationService'
import toast from 'react-hot-toast'

export default function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState([])
  const [feed, setFeed] = useState([])
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        if (user?.role === 'ADMIN') {
          const data = await adminService.getAnalytics()
          setStats([
            { label: 'Total users', value: data.totalUsers },
            { label: 'Hackathons', value: data.totalHackathons },
            { label: 'Internships', value: data.totalInternships },
            { label: 'Courses', value: data.totalCourses }
          ])
          setFeed([])
        } else {
          const [hackathons, internships, events, scholarships] = await Promise.all([
            hackathonService.getPaged({ page: 0, size: 3 }),
            internshipService.listPaged({ page: 0, size: 3 }),
            eventService.listUpcoming({ limit: 3 }),
            scholarshipService.listPublished({ page: 0, size: 3 })
          ])

          setStats([
            { label: 'Hackathons live', value: hackathons?.content?.length ?? 0 },
            { label: 'Internships', value: internships?.content?.length ?? 0 },
            { label: 'Upcoming events', value: events?.length ?? 0 },
            { label: 'Scholarships', value: scholarships?.content?.length ?? 0 }
          ])

          setFeed([
            ...(hackathons?.content || []).map((item) => ({
              id: `h-${item.id}`,
              title: item.title,
              tag: 'Hackathon',
              detail: item.organization
            })),
            ...(events || []).map((item) => ({
              id: `e-${item.id}`,
              title: item.title,
              tag: 'Event',
              detail: item.hostOrganization
            })),
            ...(scholarships?.content || []).map((item) => ({
              id: `s-${item.id}`,
              title: item.title,
              tag: 'Scholarship',
              detail: item.provider
            }))
          ])
        }

        const notificationList = await notificationService.list()
        setNotifications(notificationList?.slice(0, 4) || [])
      } catch {
        setStats([])
        setFeed([])
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [user])

  const markNotificationRead = async (id) => {
    try {
      await notificationService.markRead(id)
      setNotifications((current) =>
        current.map((note) => (note.id === id ? { ...note, read: true } : note))
      )
      toast.success('Notification marked as read')
    } catch {
      toast.error('Could not update notification')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <Topbar
        title={user?.role === 'ADMIN' ? 'Admin overview' : 'Student dashboard'}
        subtitle="Track everything that matters this week."
      />

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {loading ? (
          <LoadingState label="Preparing insights" />
        ) : (
          stats.map((stat) => <StatCard key={stat.label} {...stat} />)
        )}
      </section>

      <section className="grid gap-6 lg:grid-cols-[1.4fr_0.6fr]">
        <div className="glass-card rounded-3xl p-6">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold text-sand-50">Opportunity feed</h3>
            <p className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Live</p>
          </div>
          <div className="mt-6 space-y-4">
            {loading && <LoadingState label="Loading feed" />}
            {!loading && feed.length === 0 && (
              <EmptyState title="No updates yet" description="Check back soon for new releases." />
            )}
            {feed.map((item) => (
              <div key={item.id} className="flex items-center justify-between rounded-2xl border border-white/10 bg-white/5 p-4">
                <div>
                  <p className="text-sm text-sand-50">{item.title}</p>
                  <p className="text-xs text-sand-200/70">{item.detail}</p>
                </div>
                <span className="rounded-full bg-tide-300/20 px-3 py-1 text-xs text-tide-200">
                  {item.tag}
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="glass-card rounded-3xl p-6">
          <h3 className="text-lg font-semibold text-sand-50">Notifications</h3>
          <div className="mt-6 space-y-3">
            {loading && <LoadingState label="Loading notifications" />}
            {!loading && notifications.length === 0 && (
              <EmptyState title="No alerts" description="You are all caught up." />
            )}
            {notifications.map((note) => (
              <div
                key={note.id}
                className={`rounded-2xl border p-4 ${
                  note.read
                    ? 'border-white/10 bg-white/5'
                    : 'border-tide-300/30 bg-tide-300/10'
                }`}
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm text-sand-50">{note.title || 'Notification'}</p>
                    <p className="text-xs text-sand-200/70">
                      {note.message || 'Update received.'}
                    </p>
                  </div>
                  {!note.read && (
                    <button
                      className="shrink-0 rounded-full border border-tide-300/50 px-3 py-1 text-xs font-semibold text-tide-200"
                      onClick={() => markNotificationRead(note.id)}
                    >
                      Mark read
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}

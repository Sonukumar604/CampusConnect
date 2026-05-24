import { useEffect, useState } from 'react'
import Topbar from '../../components/Topbar'
import StatCard from '../../components/StatCard'
import LoadingState from '../../components/LoadingState'
import { adminService } from '../../services/adminService'
import toast from 'react-hot-toast'

export default function AdminDashboardPage() {
  const [stats, setStats] = useState([])
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => {
    setLoading(true)
    try {
      const [analytics, userList] = await Promise.all([
        adminService.getAnalytics(),
        adminService.listUsers()
      ])
      setStats([
        { label: 'Total users', value: analytics.totalUsers },
        { label: 'Hackathons', value: analytics.totalHackathons },
        { label: 'Internships', value: analytics.totalInternships },
        { label: 'Courses', value: analytics.totalCourses }
      ])
      setUsers(userList || [])
    } catch (error) {
      setStats([])
      setUsers([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const updateRole = async (userId, role) => {
    try {
      await adminService.updateUserRole(userId, role)
      toast.success('Role updated')
      load()
    } catch (error) {
      toast.error('Update failed')
    }
  }

  const blockUser = async (userId) => {
    try {
      await adminService.blockUser(userId)
      toast.success('User blocked')
      load()
    } catch (error) {
      toast.error('Action failed')
    }
  }

  const unblockUser = async (userId) => {
    try {
      await adminService.unblockUser(userId)
      toast.success('User unblocked')
      load()
    } catch (error) {
      toast.error('Action failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <Topbar title="Admin control" subtitle="Monitor platform performance and users." />

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {loading ? (
          <LoadingState label="Loading analytics" />
        ) : (
          stats.map((stat) => <StatCard key={stat.label} {...stat} />)
        )}
      </section>

      <section className="glass-card rounded-3xl p-6">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-sand-50">User management</h3>
          <p className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Live</p>
        </div>
        <div className="mt-6 space-y-4">
          {loading && <LoadingState label="Loading users" />}
          {!loading && users.length === 0 && (
            <p className="text-sm text-sand-200/70">No users available.</p>
          )}
          {users.map((item) => (
            <div key={item.id} className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-white/10 bg-white/5 p-4">
              <div>
                <p className="text-sm text-sand-50">{item.name}</p>
                <p className="text-xs text-sand-200/70">{item.email}</p>
              </div>
              <div className="flex flex-wrap items-center gap-3">
                <select
                  className="rounded-full border border-white/20 bg-ink-900/60 px-3 py-2 text-xs text-sand-100"
                  value={item.role}
                  onChange={(event) => updateRole(item.id, event.target.value)}
                >
                  <option value="STUDENT">STUDENT</option>
                  <option value="ORGANIZER">ORGANIZER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
                <button
                  className="rounded-full border border-white/20 px-4 py-2 text-xs text-sand-100"
                  onClick={() => blockUser(item.id)}
                >
                  Block
                </button>
                <button
                  className="rounded-full border border-white/20 px-4 py-2 text-xs text-sand-100"
                  onClick={() => unblockUser(item.id)}
                >
                  Unblock
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

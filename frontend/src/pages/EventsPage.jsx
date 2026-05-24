import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { eventService } from '../services/eventService'

export default function EventsPage() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ type: '', mode: '', location: '' })

  const load = async () => {
    setLoading(true)
    try {
      let data
      if (filters.type || filters.mode || filters.location) {
        data = await eventService.filter({
          page: 0,
          size: 6,
          type: filters.type || undefined,
          mode: filters.mode || undefined,
          location: filters.location || undefined
        })
        setItems(data.content || [])
      } else {
        data = await eventService.listPublished({ page: 0, size: 6 })
        setItems(data.content || [])
      }
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Events" subtitle="Register for high-impact sessions." />

      <div className="glass-card rounded-3xl p-6">
        <div className="grid gap-4 md:grid-cols-3">
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Type"
            value={filters.type}
            onChange={(event) => setFilters({ ...filters, type: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Mode"
            value={filters.mode}
            onChange={(event) => setFilters({ ...filters, mode: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Location"
            value={filters.location}
            onChange={(event) => setFilters({ ...filters, location: event.target.value })}
          />
        </div>
        <button
          className="mt-4 rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
          onClick={load}
        >
          Apply filters
        </button>
      </div>

      {loading ? (
        <LoadingState label="Loading events" />
      ) : items.length === 0 ? (
        <EmptyState title="No events available" description="Check back soon." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
                <span className="rounded-full bg-white/10 px-3 py-1 text-xs text-sand-100">
                  {item.eventType}
                </span>
              </div>
              <p className="mt-2 text-sm text-sand-200/80">{item.hostOrganization}</p>
              <div className="mt-4 text-xs text-sand-200/70">
                {item.mode} · {item.location || 'Online'}
              </div>
              <Link
                to={`/events/${item.id}/register`}
                className="mt-6 inline-flex rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
              >
                Register
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

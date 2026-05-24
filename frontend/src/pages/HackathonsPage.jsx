import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { hackathonService } from '../services/hackathonService'

export default function HackathonsPage() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({
    technology: '',
    organization: '',
    startDate: ''
  })

  const loadHackathons = async () => {
    setLoading(true)
    try {
      const params = {
        ...filters,
        startDate: filters.startDate || undefined
      }
      const data = await hackathonService.filter(params)
      setItems(data)
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadHackathons()
  }, [])

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Hackathons" subtitle="Discover live competitions and register fast." />

      <div className="glass-card rounded-3xl p-6">
        <div className="grid gap-4 md:grid-cols-3">
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Technology"
            value={filters.technology}
            onChange={(event) => setFilters({ ...filters, technology: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Organization"
            value={filters.organization}
            onChange={(event) => setFilters({ ...filters, organization: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="date"
            value={filters.startDate}
            onChange={(event) => setFilters({ ...filters, startDate: event.target.value })}
          />
        </div>
        <button
          className="mt-4 rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
          onClick={loadHackathons}
        >
          Apply filters
        </button>
      </div>

      {loading ? (
        <LoadingState label="Loading hackathons" />
      ) : items.length === 0 ? (
        <EmptyState title="No hackathons found" description="Try a different filter set." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
                <span className="rounded-full bg-white/10 px-3 py-1 text-xs text-sand-100">
                  {item.mode}
                </span>
              </div>
              <p className="mt-2 text-sm text-sand-200/80">{item.organization}</p>
              <div className="mt-4 flex items-center justify-between text-xs text-sand-200/70">
                <span>{item.technology}</span>
                <span>
                  {item.startDate} to {item.endDate}
                </span>
              </div>
              <Link
                to={`/hackathons/${item.id}/register`}
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

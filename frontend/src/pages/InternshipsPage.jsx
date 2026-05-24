import { useEffect, useState } from 'react'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { internshipService } from '../services/internshipService'
import { bookmarkService } from '../services/bookmarkService'
import toast from 'react-hot-toast'

export default function InternshipsPage() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ keyword: '', location: '', type: '' })

  const loadInternships = async () => {
    setLoading(true)
    try {
      let data
      if (filters.keyword) {
        data = await internshipService.search({ keyword: filters.keyword, page: 0, size: 10 })
      } else if (filters.location) {
        data = await internshipService.filterByLocation({ location: filters.location, page: 0, size: 10 })
      } else if (filters.type) {
        data = await internshipService.filterByType({ type: filters.type, page: 0, size: 10 })
      } else {
        data = await internshipService.listPaged({ page: 0, size: 10 })
      }
      setItems(data.content || [])
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadInternships()
  }, [])

  const saveBookmark = async (id) => {
    try {
      await bookmarkService.create({ entityId: id, type: 'INTERNSHIP' })
      toast.success('Saved to your applications')
    } catch (error) {
      toast.error('Unable to save application')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Internships" subtitle="Find your next role and track applications." />

      <div className="glass-card rounded-3xl p-6">
        <div className="grid gap-4 md:grid-cols-3">
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Search keyword"
            value={filters.keyword}
            onChange={(event) => setFilters({ ...filters, keyword: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Location"
            value={filters.location}
            onChange={(event) => setFilters({ ...filters, location: event.target.value })}
          />
          <select
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            value={filters.type}
            onChange={(event) => setFilters({ ...filters, type: event.target.value })}
          >
            <option value="">Type</option>
            <option value="REMOTE">Remote</option>
            <option value="HYBRID">Hybrid</option>
            <option value="ONSITE">On-site</option>
          </select>
        </div>
        <button
          className="mt-4 rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
          onClick={loadInternships}
        >
          Apply filters
        </button>
      </div>

      {loading ? (
        <LoadingState label="Loading internships" />
      ) : items.length === 0 ? (
        <EmptyState title="No internships found" description="Try another search." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-sand-50">{item.role}</h3>
                <span className="rounded-full bg-white/10 px-3 py-1 text-xs text-sand-100">
                  {item.type}
                </span>
              </div>
              <p className="mt-2 text-sm text-sand-200/80">{item.companyName}</p>
              <div className="mt-3 text-xs text-sand-200/70">
                {item.location} · {item.stipend} · {item.duration}
              </div>
              <div className="mt-5 flex flex-wrap gap-3">
                <a
                  href={item.applyLink}
                  target="_blank"
                  rel="noreferrer"
                  className="rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950"
                >
                  Apply now
                </a>
                <button
                  className="rounded-full border border-white/20 px-5 py-2.5 text-sm text-sand-100"
                  onClick={() => saveBookmark(item.id)}
                >
                  Save application
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

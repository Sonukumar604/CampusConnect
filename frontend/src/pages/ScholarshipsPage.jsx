import { useEffect, useState } from 'react'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import Button from '../components/Button'
import { scholarshipService } from '../services/scholarshipService'
import { useAuth } from '../hooks/useAuth'
import toast from 'react-hot-toast'

export default function ScholarshipsPage() {
  const { user } = useAuth()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ category: '', provider: '' })
  const [selected, setSelected] = useState(null)
  const [application, setApplication] = useState({ statementOfPurpose: '', gpa: '' })

  const load = async () => {
    setLoading(true)
    try {
      let data
      if (filters.category || filters.provider) {
        data = await scholarshipService.filter({
          page: 0,
          size: 6,
          category: filters.category || undefined,
          provider: filters.provider || undefined
        })
      } else {
        data = await scholarshipService.listPublished({ page: 0, size: 6 })
      }
      setItems(data.content || [])
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const apply = async () => {
    if (!user?.id || !selected) return
    try {
      await scholarshipService.apply(user.id, {
        scholarshipId: selected.id,
        statementOfPurpose: application.statementOfPurpose,
        gpa: Number(application.gpa)
      })
      toast.success('Application submitted')
      setSelected(null)
      setApplication({ statementOfPurpose: '', gpa: '' })
    } catch (error) {
      toast.error('Application failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Scholarships" subtitle="Apply for funding and grants." />

      <div className="glass-card rounded-3xl p-6">
        <div className="grid gap-4 md:grid-cols-2">
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Category"
            value={filters.category}
            onChange={(event) => setFilters({ ...filters, category: event.target.value })}
          />
          <input
            className="w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Provider"
            value={filters.provider}
            onChange={(event) => setFilters({ ...filters, provider: event.target.value })}
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
        <LoadingState label="Loading scholarships" />
      ) : items.length === 0 ? (
        <EmptyState title="No scholarships found" description="Check back soon." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
              <p className="mt-2 text-sm text-sand-200/70">{item.provider}</p>
              <p className="mt-3 text-xs text-sand-200/70">
                Amount: {item.amount} · Deadline: {item.deadline}
              </p>
              <Button
                variant="primary"
                className="mt-5"
                onClick={() => setSelected(item)}
              >
                Apply now
              </Button>
            </div>
          ))}
        </div>
      )}

      {selected && (
        <div className="glass-card rounded-3xl p-6">
          <h3 className="text-lg font-semibold text-sand-50">
            Apply for {selected.title}
          </h3>
          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <textarea
              className="col-span-2 rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
              rows="4"
              placeholder="Statement of purpose"
              value={application.statementOfPurpose}
              onChange={(event) =>
                setApplication({ ...application, statementOfPurpose: event.target.value })
              }
            />
            <input
              className="rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
              placeholder="GPA"
              value={application.gpa}
              onChange={(event) => setApplication({ ...application, gpa: event.target.value })}
            />
          </div>
          <div className="mt-4 flex items-center gap-3">
            <Button variant="primary" onClick={apply}>
              Submit application
            </Button>
            <Button variant="ghost" onClick={() => setSelected(null)}>
              Cancel
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}

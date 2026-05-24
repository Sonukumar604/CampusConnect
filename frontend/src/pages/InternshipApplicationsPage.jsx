import { useEffect, useState } from 'react'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import { bookmarkService } from '../services/bookmarkService'
import toast from 'react-hot-toast'

export default function InternshipApplicationsPage() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => {
    setLoading(true)
    try {
      const data = await bookmarkService.list()
      setItems(data.filter((item) => item.type === 'INTERNSHIP'))
    } catch (error) {
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const remove = async (id) => {
    try {
      await bookmarkService.remove(id)
      toast.success('Removed from applications')
      load()
    } catch (error) {
      toast.error('Unable to remove')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Internship applications" subtitle="Track the roles you saved." />

      {loading ? (
        <LoadingState label="Loading applications" />
      ) : items.length === 0 ? (
        <EmptyState title="No saved applications" description="Save internships to track them here." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="glass-card rounded-3xl p-6">
              <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
              <p className="mt-2 text-sm text-sand-200/70">Saved internship</p>
              <button
                className="mt-4 rounded-full border border-white/20 px-4 py-2 text-xs text-sand-100"
                onClick={() => remove(item.id)}
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

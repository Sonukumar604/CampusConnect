import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import PageHeader from '../components/PageHeader'
import Button from '../components/Button'
import LoadingState from '../components/LoadingState'
import { eventService } from '../services/eventService'
import { useAuth } from '../hooks/useAuth'
import toast from 'react-hot-toast'

export default function EventRegistrationPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const [event, setEvent] = useState(null)
  const [loading, setLoading] = useState(true)
  const { register, handleSubmit } = useForm()

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const data = await eventService.getById(id)
        setEvent(data)
      } catch (error) {
        setEvent(null)
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [id])

  const onSubmit = async (payload) => {
    if (!user?.id) return
    try {
      await eventService.register(id, user.id, payload)
      toast.success('Registration confirmed')
    } catch (error) {
      toast.error('Registration failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Event registration" subtitle="Save your seat and get updates." />

      {loading ? (
        <LoadingState label="Loading event" />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
          <div className="glass-card rounded-3xl p-6">
            <h3 className="text-lg font-semibold text-sand-50">{event?.title}</h3>
            <p className="mt-2 text-sm text-sand-200/80">{event?.description}</p>
            <div className="mt-4 grid gap-3 text-xs text-sand-200/70">
              <span>Type: {event?.eventType}</span>
              <span>Mode: {event?.mode}</span>
              <span>Location: {event?.location || 'Online'}</span>
              <span>Speaker: {event?.speakerName || 'TBA'}</span>
            </div>
            <Link to="/events" className="mt-6 inline-flex text-sm text-tide-200">
              Back to events
            </Link>
          </div>
          <div className="glass-card rounded-3xl p-6">
            <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
              <div>
                <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
                  Notes (optional)
                </label>
                <textarea
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                  rows="4"
                  {...register('notes')}
                />
              </div>
              <Button type="submit" variant="primary" className="w-full">
                Confirm registration
              </Button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

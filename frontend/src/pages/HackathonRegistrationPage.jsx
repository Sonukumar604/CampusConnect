import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import PageHeader from '../components/PageHeader'
import Button from '../components/Button'
import LoadingState from '../components/LoadingState'
import { hackathonService } from '../services/hackathonService'
import { useAuth } from '../hooks/useAuth'
import toast from 'react-hot-toast'

export default function HackathonRegistrationPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const [hackathon, setHackathon] = useState(null)
  const [loading, setLoading] = useState(true)
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm()

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const data = await hackathonService.getById(id)
        setHackathon(data)
      } catch (error) {
        setHackathon(null)
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [id])

  const onSubmit = async (payload) => {
    if (!user?.id) return
    try {
      await hackathonService.register(id, user.id, payload)
      toast.success('Registration submitted')
    } catch (error) {
      toast.error('Registration failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Hackathon registration" subtitle="Secure your spot in the lineup." />

      {loading ? (
        <LoadingState label="Loading hackathon" />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
          <div className="glass-card rounded-3xl p-6">
            <h3 className="text-lg font-semibold text-sand-50">{hackathon?.title}</h3>
            <p className="mt-2 text-sm text-sand-200/80">{hackathon?.organization}</p>
            <div className="mt-4 grid gap-3 text-xs text-sand-200/70">
              <span>Mode: {hackathon?.mode}</span>
              <span>Technology: {hackathon?.technology}</span>
              <span>
                Dates: {hackathon?.startDate} to {hackathon?.endDate}
              </span>
            </div>
            <Link to="/hackathons" className="mt-6 inline-flex text-sm text-tide-200">
              Back to hackathons
            </Link>
          </div>
          <div className="glass-card rounded-3xl p-6">
            <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
              <div>
                <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
                  Team name
                </label>
                <input
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                  {...register('teamName', { required: 'Team name is required' })}
                />
                {errors.teamName && (
                  <p className="mt-1 text-xs text-coral-300">{errors.teamName.message}</p>
                )}
              </div>
              <div>
                <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
                  Project idea
                </label>
                <textarea
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                  rows="4"
                  {...register('projectIdea', { required: 'Project idea is required' })}
                />
                {errors.projectIdea && (
                  <p className="mt-1 text-xs text-coral-300">{errors.projectIdea.message}</p>
                )}
              </div>
              <Button type="submit" variant="primary" className="w-full">
                Submit registration
              </Button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

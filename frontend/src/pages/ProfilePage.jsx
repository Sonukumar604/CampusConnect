import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import PageHeader from '../components/PageHeader'
import Button from '../components/Button'
import LoadingState from '../components/LoadingState'
import { useAuth } from '../hooks/useAuth'
import { userService } from '../services/userService'
import toast from 'react-hot-toast'

export default function ProfilePage() {
  const { user, refreshProfile } = useAuth()
  const [loading, setLoading] = useState(true)
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm()

  useEffect(() => {
    const load = async () => {
      if (!user?.id) return
      setLoading(true)
      try {
        const profile = await userService.getCurrentUser()
        reset({
          name: profile.name,
          email: profile.email,
          role: profile.role,
          password: ''
        })
      } catch (error) {
        // ignore
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [user, reset])

  const onSubmit = async (payload) => {
    if (!user?.id) return
    try {
      await userService.updateProfile(user.id, payload)
      await refreshProfile()
      toast.success('Profile updated')
    } catch (error) {
      toast.error('Profile update failed')
    }
  }

  return (
    <div className="flex flex-col gap-10">
      <PageHeader title="Profile" subtitle="Keep your details up to date." />

      <div className="glass-card rounded-3xl p-6">
        {loading ? (
          <LoadingState label="Loading profile" />
        ) : (
          <form className="grid gap-6 md:grid-cols-2" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Name</label>
              <input
                className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                {...register('name', { required: 'Name is required' })}
              />
              {errors.name && <p className="mt-1 text-xs text-coral-300">{errors.name.message}</p>}
            </div>
            <div>
              <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Email</label>
              <input
                className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                type="email"
                {...register('email', { required: 'Email is required' })}
              />
              {errors.email && <p className="mt-1 text-xs text-coral-300">{errors.email.message}</p>}
            </div>
            <div>
              <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
                Password (optional)
              </label>
              <input
                className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                type="password"
                {...register('password')}
              />
            </div>
            <div>
              <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Role</label>
              <input
                className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
                {...register('role')}
                disabled
              />
            </div>
            <div className="md:col-span-2">
              <Button type="submit" variant="primary">
                Save changes
              </Button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

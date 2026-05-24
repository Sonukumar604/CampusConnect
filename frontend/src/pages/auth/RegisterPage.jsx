import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import Button from '../../components/Button'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'

export default function RegisterPage() {
  const { register: signUp, loading } = useAuth()
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm()

  const onSubmit = async (data) => {
    await signUp(data)
    navigate('/login')
  }

  return (
    <div className="w-full max-w-md space-y-6">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Create account</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Join CampusConnect</h2>
        <p className="mt-2 text-sm text-sand-200/80">
          Build your profile and unlock campus opportunities.
        </p>
      </div>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Name</label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            placeholder="Your full name"
            {...register('name', { required: 'Name is required' })}
          />
          {errors.name && <p className="mt-1 text-xs text-coral-300">{errors.name.message}</p>}
        </div>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Email</label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="email"
            placeholder="you@campusconnect.com"
            {...register('email', { required: 'Email is required' })}
          />
          {errors.email && <p className="mt-1 text-xs text-coral-300">{errors.email.message}</p>}
        </div>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">Password</label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="password"
            placeholder="Create a password"
            {...register('password', { required: 'Password is required', minLength: 8 })}
          />
          {errors.password && (
            <p className="mt-1 text-xs text-coral-300">Password must be at least 8 characters</p>
          )}
        </div>
        <div className="flex items-center justify-between text-xs text-sand-200/70">
          <Link to="/login" className="hover:text-sand-50">
            Already have an account?
          </Link>
        </div>
        <Button type="submit" variant="primary" className="w-full" disabled={loading}>
          {loading ? 'Creating account...' : 'Create account'}
        </Button>
      </form>
      <div className="flex items-center gap-3 text-xs uppercase tracking-[0.25em] text-sand-200/50">
        <span className="h-px flex-1 bg-white/10" />
        Or
        <span className="h-px flex-1 bg-white/10" />
      </div>
      <a
        href={authService.getOAuthUrl('google')}
        className="flex w-full items-center justify-center rounded-full border border-white/15 px-5 py-2.5 text-sm font-semibold text-sand-100 transition hover:border-tide-300/70 hover:text-sand-50"
      >
        Continue with Google
      </a>
    </div>
  )
}

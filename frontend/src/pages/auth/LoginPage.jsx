import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import Button from '../../components/Button'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'

export default function LoginPage() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm()

  const onSubmit = async (data) => {
    await login(data)
    navigate('/dashboard')
  }

  return (
    <div className="w-full max-w-md space-y-6">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Sign in</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Welcome back</h2>
        <p className="mt-2 text-sm text-sand-200/80">
          Use your CampusConnect credentials to continue.
        </p>
      </div>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
            Email
          </label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="email"
            placeholder="you@campusconnect.com"
            {...register('email', { required: 'Email is required' })}
          />
          {errors.email && <p className="mt-1 text-xs text-coral-300">{errors.email.message}</p>}
        </div>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
            Password
          </label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="password"
            placeholder="Enter your password"
            {...register('password', { required: 'Password is required' })}
          />
          {errors.password && (
            <p className="mt-1 text-xs text-coral-300">{errors.password.message}</p>
          )}
        </div>
        <div className="flex items-center justify-between text-xs text-sand-200/70">
          <Link to="/forgot-password" className="hover:text-sand-50">
            Forgot password?
          </Link>
          <Link to="/register" className="hover:text-sand-50">
            Create account
          </Link>
        </div>
        <Button type="submit" variant="primary" className="w-full" disabled={loading}>
          {loading ? 'Signing in...' : 'Sign in'}
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

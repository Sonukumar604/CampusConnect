import { useForm } from 'react-hook-form'
import { useSearchParams, Link } from 'react-router-dom'
import Button from '../../components/Button'
import { authService } from '../../services/authService'
import toast from 'react-hot-toast'

export default function ResetPasswordPage() {
  const [params] = useSearchParams()
  const token = params.get('token')
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm()

  const onSubmit = async ({ password }) => {
    if (!token) {
      toast.error('Reset token missing')
      return
    }
    try {
      await authService.resetPassword(token, password)
      toast.success('Password updated. Please sign in.')
    } catch (error) {
      toast.error('Reset failed. Try again.')
    }
  }

  return (
    <div className="w-full max-w-md space-y-6">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Reset access</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Create a new password</h2>
        <p className="mt-2 text-sm text-sand-200/80">
          Choose a secure password to continue.
        </p>
      </div>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-xs uppercase tracking-[0.2em] text-sand-200/70">New password</label>
          <input
            className="mt-2 w-full rounded-2xl border border-white/10 bg-ink-900/60 px-4 py-3 text-sm text-sand-50"
            type="password"
            placeholder="At least 6 characters"
            {...register('password', { required: 'Password is required', minLength: 6 })}
          />
          {errors.password && (
            <p className="mt-1 text-xs text-coral-300">Password must be at least 6 characters</p>
          )}
        </div>
        <Button type="submit" variant="primary" className="w-full">
          Update password
        </Button>
      </form>
      <Link to="/login" className="text-xs text-sand-200/70 hover:text-sand-50">
        Back to sign in
      </Link>
    </div>
  )
}

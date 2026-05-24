import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import Button from '../../components/Button'
import { authService } from '../../services/authService'
import toast from 'react-hot-toast'

export default function ForgotPasswordPage() {
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm()

  const onSubmit = async ({ email }) => {
    try {
      await authService.forgotPassword(email)
      toast.success('Reset link sent to your email')
    } catch (error) {
      toast.error('Unable to send reset link')
    }
  }

  return (
    <div className="w-full max-w-md space-y-6">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Reset access</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Forgot password</h2>
        <p className="mt-2 text-sm text-sand-200/80">
          Enter your email and we will send you a reset link.
        </p>
      </div>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
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
        <Button type="submit" variant="primary" className="w-full">
          Send reset link
        </Button>
      </form>
      <Link to="/login" className="text-xs text-sand-200/70 hover:text-sand-50">
        Back to sign in
      </Link>
    </div>
  )
}

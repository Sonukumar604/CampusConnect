import { useEffect, useState } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import Button from '../../components/Button'
import { authService } from '../../services/authService'

export default function VerifyEmailPage() {
  const [params] = useSearchParams()
  const [status, setStatus] = useState('verifying')

  useEffect(() => {
    const token = params.get('token')
    if (!token) {
      setStatus('missing')
      return
    }

    const verify = async () => {
      try {
        await authService.verifyEmail(token)
        setStatus('success')
      } catch (error) {
        setStatus('error')
      }
    }

    verify()
  }, [params])

  const message = {
    verifying: 'Verifying your email...',
    success: 'Email verified. You can now sign in.',
    error: 'Verification failed. Please request a new link.',
    missing: 'Verification token is missing.'
  }[status]

  return (
    <div className="w-full max-w-md space-y-6 text-center">
      <h2 className="font-serif text-3xl text-sand-50">Email verification</h2>
      <p className="text-sm text-sand-200/80">{message}</p>
      <Link to="/login">
        <Button variant="primary">Return to sign in</Button>
      </Link>
    </div>
  )
}

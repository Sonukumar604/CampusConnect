import { Link } from 'react-router-dom'
import Button from '../../components/Button'

export default function ForbiddenPage() {
  return (
    <div className="surface-grid flex min-h-screen items-center justify-center px-6">
      <div className="glass-card w-full max-w-md rounded-3xl p-8 text-center">
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">403</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Access denied</h2>
        <p className="mt-3 text-sm text-sand-200/80">
          You do not have permission to access this area.
        </p>
        <Link to="/dashboard" className="mt-6 inline-flex">
          <Button variant="primary">Return to dashboard</Button>
        </Link>
      </div>
    </div>
  )
}

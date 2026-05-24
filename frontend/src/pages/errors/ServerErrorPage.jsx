import { Link } from 'react-router-dom'
import Button from '../../components/Button'

export default function ServerErrorPage() {
  return (
    <div className="surface-grid flex min-h-screen items-center justify-center px-6">
      <div className="glass-card w-full max-w-md rounded-3xl p-8 text-center">
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">500</p>
        <h2 className="mt-2 font-serif text-3xl text-sand-50">Something went wrong</h2>
        <p className="mt-3 text-sm text-sand-200/80">
          We are working on it. Please try again later.
        </p>
        <Link to="/" className="mt-6 inline-flex">
          <Button variant="primary">Return home</Button>
        </Link>
      </div>
    </div>
  )
}

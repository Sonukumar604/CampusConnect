import { useAuth } from '../hooks/useAuth'
import Button from './Button'

export default function Topbar({ title, subtitle }) {
  const { user, logout } = useAuth()

  return (
    <div className="flex flex-col gap-4 border-b border-white/10 pb-6 md:flex-row md:items-center md:justify-between">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">
          Welcome back
        </p>
        <h2 className="font-serif text-2xl text-sand-50 md:text-3xl">{title}</h2>
        {subtitle && <p className="mt-2 text-sm text-sand-200/80">{subtitle}</p>}
      </div>
      <div className="flex items-center gap-4">
        <div className="text-right text-sm">
          <p className="text-sand-50">{user?.name}</p>
          <p className="text-xs uppercase tracking-[0.2em] text-sand-200/70">
            {user?.role}
          </p>
        </div>
        <Button variant="secondary" onClick={logout}>
          Sign out
        </Button>
      </div>
    </div>
  )
}

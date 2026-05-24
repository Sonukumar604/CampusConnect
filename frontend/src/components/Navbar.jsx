import { Link, NavLink } from 'react-router-dom'
import Logo from './Logo'
import Button from './Button'
import { useAuth } from '../hooks/useAuth'

const links = [
  { label: 'Hackathons', to: '/hackathons' },
  { label: 'Internships', to: '/internships' },
  { label: 'Courses', to: '/courses' },
  { label: 'Events', to: '/events' },
  { label: 'Scholarships', to: '/scholarships' }
]

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth()

  return (
    <header className="sticky top-0 z-20 border-b border-white/10 bg-ink-950/80 backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-4">
        <Link to="/">
          <Logo />
        </Link>
        <nav className="hidden items-center gap-6 text-sm text-sand-100 md:flex">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `transition ${isActive ? 'text-tide-200' : 'text-sand-100/80 hover:text-sand-50'}`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className="text-sm text-sand-100/80 hover:text-sand-50">
                {user?.name || 'Dashboard'}
              </Link>
              <Button variant="secondary" onClick={logout}>
                Sign out
              </Button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm text-sand-100/80 hover:text-sand-50">
                Sign in
              </Link>
              <Link
                to="/register"
                className="hidden rounded-full bg-tide-300 px-5 py-2.5 text-sm font-semibold text-ink-950 transition hover:bg-tide-200 sm:inline-flex"
              >
                Get started
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}

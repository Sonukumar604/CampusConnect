import { NavLink } from 'react-router-dom'
import Logo from './Logo'
import { useAuth } from '../hooks/useAuth'

const baseLinks = [
  { label: 'Dashboard', to: '/dashboard' },
  { label: 'Profile', to: '/profile' },
  { label: 'Hackathons', to: '/hackathons' },
  { label: 'Internships', to: '/internships' },
  { label: 'Courses', to: '/courses' },
  { label: 'My Activity', to: '/my-activity' },
  { label: 'Events', to: '/events' },
  { label: 'Scholarships', to: '/scholarships' }
]

export default function Sidebar() {
  const { user } = useAuth()
  const links = [...baseLinks]

  if (user?.role === 'ADMIN') {
    links.push({ label: 'Admin Dashboard', to: '/admin' })
  }

  return (
    <aside className="flex h-full w-full flex-col gap-8 border-r border-white/10 bg-ink-900/60 px-6 py-8">
      <Logo />
      <nav className="flex flex-1 flex-col gap-3 text-sm">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `rounded-2xl px-4 py-3 transition ${
                isActive
                  ? 'bg-tide-300/20 text-tide-200'
                  : 'text-sand-100/70 hover:text-sand-50'
              }`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
      <div className="rounded-3xl border border-white/10 bg-white/5 p-4 text-xs text-sand-200/70">
        <p className="text-sand-100">Need help?</p>
        <p className="mt-2">
          Explore your upcoming events and opportunities in one place.
        </p>
      </div>
    </aside>
  )
}

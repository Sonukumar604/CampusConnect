import { Outlet } from 'react-router-dom'
import Logo from '../components/Logo'

export default function AuthLayout() {
  return (
    <div className="surface-grid min-h-screen">
      <div className="mx-auto flex min-h-screen w-full max-w-6xl flex-col gap-10 px-6 py-12 lg:grid lg:grid-cols-[1.1fr_0.9fr]">
        <section className="flex flex-col justify-center gap-6">
          <Logo />
          <h1 className="font-serif text-4xl text-sand-50 md:text-5xl">
            Build momentum for every semester.
          </h1>
          <p className="max-w-md text-sm text-sand-200/80">
            Sign in to track your applications, discover new cohorts, and keep your
            opportunities organized.
          </p>
          <div className="section-divider h-px w-36" />
          <div className="grid gap-4 text-xs uppercase tracking-[0.3em] text-sand-200/70">
            <span>Hackathons</span>
            <span>Internships</span>
            <span>Events</span>
            <span>Scholarships</span>
          </div>
        </section>
        <section className="glass-card flex items-center justify-center rounded-3xl p-8 shadow-card">
          <Outlet />
        </section>
      </div>
    </div>
  )
}

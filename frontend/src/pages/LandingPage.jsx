import { Link } from 'react-router-dom'
import Button from '../components/Button'
import Badge from '../components/Badge'

export default function LandingPage() {
  return (
    <div className="flex flex-col gap-16">
      <section className="grid gap-10 rounded-[32px] border border-white/10 bg-hero-texture p-10 md:grid-cols-[1.1fr_0.9fr]">
        <div className="flex flex-col gap-6">
          <Badge>CampusConnect</Badge>
          <h1 className="font-serif text-4xl text-sand-50 md:text-5xl">
            Where ambitious students plan their next win.
          </h1>
          <p className="text-sm text-sand-200/80">
            Track hackathons, internships, courses, events, and scholarships in one
            command center. Stay organized, apply faster, and keep momentum through
            every semester.
          </p>
          <div className="flex flex-wrap items-center gap-4">
            <Link to="/register">
              <Button variant="primary">Create account</Button>
            </Link>
            <Link to="/login">
              <Button variant="secondary">Sign in</Button>
            </Link>
          </div>
          <div className="mt-6 grid grid-cols-2 gap-4 text-xs uppercase tracking-[0.3em] text-sand-200/60 md:grid-cols-4">
            <span>Hackathons</span>
            <span>Internships</span>
            <span>Events</span>
            <span>Scholarships</span>
          </div>
        </div>
        <div className="glass-card rounded-3xl p-6">
          <div className="flex flex-col gap-4">
            <div className="rounded-2xl border border-white/10 bg-white/5 p-5">
              <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Today</p>
              <p className="mt-2 text-xl text-sand-50">3 new internships live</p>
              <p className="mt-2 text-sm text-sand-200/80">
                Save roles, export deadlines, and track outreach from one workspace.
              </p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-5">
              <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">This week</p>
              <p className="mt-2 text-xl text-sand-50">Hackathon registration opens</p>
              <p className="mt-2 text-sm text-sand-200/80">
                Get notified the minute a new event goes public.
              </p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-5">
              <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Next sprint</p>
              <p className="mt-2 text-xl text-sand-50">Scholarship shortlists ready</p>
              <p className="mt-2 text-sm text-sand-200/80">
                Keep your profile primed with verified achievements.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="grid gap-6 md:grid-cols-3">
        {[
          {
            title: 'Unified opportunity feed',
            description:
              'Collect every hackathon, internship, and scholarship update in one personalized stream.'
          },
          {
            title: 'Action-ready dashboards',
            description:
              'Track your applications, registrations, and course enrollments with clear progress.'
          },
          {
            title: 'Admin-ready controls',
            description:
              'Organizers can publish events, manage users, and monitor campus analytics.'
          }
        ].map((item) => (
          <div key={item.title} className="glass-card rounded-3xl p-6 shadow-card">
            <h3 className="text-lg font-semibold text-sand-50">{item.title}</h3>
            <p className="mt-3 text-sm text-sand-200/80">{item.description}</p>
          </div>
        ))}
      </section>

      <section className="rounded-[28px] border border-white/10 bg-white/5 p-8">
        <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">Start now</p>
            <h2 className="font-serif text-3xl text-sand-50">
              Your next opportunity is already waiting.
            </h2>
          </div>
          <Link to="/register">
            <Button variant="primary">Get started</Button>
          </Link>
        </div>
      </section>
    </div>
  )
}

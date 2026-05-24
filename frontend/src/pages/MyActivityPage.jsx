import { useEffect, useMemo, useState } from 'react'
import PageHeader from '../components/PageHeader'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import Badge from '../components/Badge'
import { useAuth } from '../hooks/useAuth'
import { courseService } from '../services/courseService'
import { eventService } from '../services/eventService'
import { hackathonService } from '../services/hackathonService'
import { scholarshipService } from '../services/scholarshipService'

const sections = [
  { key: 'courses', label: 'Courses' },
  { key: 'hackathons', label: 'Hackathons' },
  { key: 'events', label: 'Events' },
  { key: 'scholarships', label: 'Scholarships' }
]

const uniqueIds = (items, field) => [
  ...new Set(items.map((item) => item[field]).filter(Boolean))
]

const resolveDetails = async (items, field, loader) => {
  const ids = uniqueIds(items, field)
  const results = await Promise.allSettled(ids.map((id) => loader(id)))

  return results.reduce((map, result, index) => {
    if (result.status === 'fulfilled') {
      map[ids[index]] = result.value
    }
    return map
  }, {})
}

function ActivityCard({ title, eyebrow, meta, status, children }) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 p-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.25em] text-tide-200">{eyebrow}</p>
          <h3 className="mt-2 text-base font-semibold text-sand-50">{title}</h3>
          {meta && <p className="mt-2 text-sm text-sand-200/70">{meta}</p>}
        </div>
        {status && <Badge>{status}</Badge>}
      </div>
      {children && <div className="mt-4 text-sm text-sand-200/75">{children}</div>}
    </div>
  )
}

function ActivitySection({ title, loading, items, emptyTitle, children }) {
  return (
    <section className="glass-card rounded-3xl p-6">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-sand-50">{title}</h2>
        <span className="text-xs uppercase tracking-[0.25em] text-sand-200/60">
          {items.length}
        </span>
      </div>
      <div className="mt-5 space-y-4">
        {loading && <LoadingState label={`Loading ${title.toLowerCase()}`} />}
        {!loading && items.length === 0 && (
          <EmptyState title={emptyTitle} description="New activity will appear here." />
        )}
        {!loading && children}
      </div>
    </section>
  )
}

export default function MyActivityPage() {
  const { user } = useAuth()
  const [active, setActive] = useState('courses')
  const [loading, setLoading] = useState(true)
  const [activity, setActivity] = useState({
    courses: [],
    hackathons: [],
    events: [],
    scholarships: []
  })
  const [details, setDetails] = useState({
    courses: {},
    hackathons: {},
    events: {},
    scholarships: {}
  })

  useEffect(() => {
    const load = async () => {
      if (!user?.id) {
        setLoading(false)
        return
      }

      setLoading(true)
      try {
        const [courses, hackathons, events, scholarships] = await Promise.all([
          courseService.getEnrollments(user.id),
          hackathonService.getUserRegistrations(user.id),
          eventService.getUserRegistrations(user.id),
          scholarshipService.listMyApplications(user.id)
        ])

        const nextActivity = {
          courses: courses || [],
          hackathons: hackathons || [],
          events: events || [],
          scholarships: scholarships || []
        }

        setActivity(nextActivity)

        const [courseDetails, hackathonDetails, eventDetails, scholarshipDetails] =
          await Promise.all([
            resolveDetails(nextActivity.courses, 'courseId', courseService.getById),
            resolveDetails(nextActivity.hackathons, 'hackathonId', hackathonService.getById),
            resolveDetails(nextActivity.events, 'eventId', eventService.getById),
            resolveDetails(
              nextActivity.scholarships,
              'scholarshipId',
              scholarshipService.getById
            )
          ])

        setDetails({
          courses: courseDetails,
          hackathons: hackathonDetails,
          events: eventDetails,
          scholarships: scholarshipDetails
        })
      } catch {
        setActivity({
          courses: [],
          hackathons: [],
          events: [],
          scholarships: []
        })
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [user])

  const counts = useMemo(
    () =>
      sections.reduce((map, section) => {
        map[section.key] = activity[section.key].length
        return map
      }, {}),
    [activity]
  )

  return (
    <div className="flex flex-col gap-10">
      <PageHeader
        title="My activity"
        subtitle="Review your enrollments, registrations, and scholarship applications."
      />

      <div className="flex flex-wrap gap-3">
        {sections.map((section) => (
          <button
            key={section.key}
            className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
              active === section.key
                ? 'bg-tide-300 text-ink-950'
                : 'border border-white/10 text-sand-100 hover:border-tide-300/60'
            }`}
            onClick={() => setActive(section.key)}
          >
            {section.label} ({counts[section.key]})
          </button>
        ))}
      </div>

      {active === 'courses' && (
        <ActivitySection
          title="Course enrollments"
          loading={loading}
          items={activity.courses}
          emptyTitle="No course enrollments"
        >
          {activity.courses.map((item) => {
            const course = details.courses[item.courseId]
            return (
              <ActivityCard
                key={item.id}
                eyebrow={`Course #${item.courseId}`}
                title={course?.title || 'Course enrollment'}
                meta={course ? `${course.instructor || 'Instructor'} - ${course.platform || 'Platform'}` : `Enrolled on ${item.enrolledAt || 'recorded date'}`}
                status={item.status}
              />
            )
          })}
        </ActivitySection>
      )}

      {active === 'hackathons' && (
        <ActivitySection
          title="Hackathon registrations"
          loading={loading}
          items={activity.hackathons}
          emptyTitle="No hackathon registrations"
        >
          {activity.hackathons.map((item) => {
            const hackathon = details.hackathons[item.hackathonId]
            return (
              <ActivityCard
                key={item.id}
                eyebrow={`Hackathon #${item.hackathonId}`}
                title={hackathon?.title || item.teamName || 'Hackathon registration'}
                meta={hackathon?.organization || `Team: ${item.teamName || 'Individual'}`}
                status={item.approved ? 'Approved' : 'Pending'}
              >
                {item.projectIdea && <span>Project idea: {item.projectIdea}</span>}
              </ActivityCard>
            )
          })}
        </ActivitySection>
      )}

      {active === 'events' && (
        <ActivitySection
          title="Event registrations"
          loading={loading}
          items={activity.events}
          emptyTitle="No event registrations"
        >
          {activity.events.map((item) => {
            const event = details.events[item.eventId]
            return (
              <ActivityCard
                key={item.id}
                eyebrow={`Event #${item.eventId}`}
                title={event?.title || 'Event registration'}
                meta={event ? `${event.mode || 'Mode'} - ${event.location || 'Online'}` : item.registrationTime}
                status={item.status}
              >
                {item.notes && <span>{item.notes}</span>}
              </ActivityCard>
            )
          })}
        </ActivitySection>
      )}

      {active === 'scholarships' && (
        <ActivitySection
          title="Scholarship applications"
          loading={loading}
          items={activity.scholarships}
          emptyTitle="No scholarship applications"
        >
          {activity.scholarships.map((item) => {
            const scholarship = details.scholarships[item.scholarshipId]
            return (
              <ActivityCard
                key={item.id}
                eyebrow={`Scholarship #${item.scholarshipId}`}
                title={scholarship?.title || 'Scholarship application'}
                meta={scholarship?.provider}
                status={item.status}
              >
                {item.sop && <span>{item.sop}</span>}
              </ActivityCard>
            )
          })}
        </ActivitySection>
      )}
    </div>
  )
}

export default function Footer() {
  return (
    <footer className="border-t border-white/10 py-10">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-4 px-6 text-sm text-sand-200 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-sand-100">CampusConnect</p>
          <p className="text-xs text-sand-200/70">
            Student platform for hackathons, internships, courses, and scholarships.
          </p>
        </div>
        <p className="text-xs uppercase tracking-[0.3em] text-sand-200/60">
          Built for ambitious learners
        </p>
      </div>
    </footer>
  )
}

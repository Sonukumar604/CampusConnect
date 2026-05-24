export default function Logo({ compact = false }) {
  return (
    <div className="flex items-center gap-3">
      <div className="h-10 w-10 rounded-2xl bg-tide-300/20 p-2">
        <div className="h-full w-full rounded-xl bg-tide-300" />
      </div>
      {!compact && (
        <div>
          <p className="text-lg font-semibold text-sand-50">CampusConnect</p>
          <p className="text-xs uppercase tracking-[0.3em] text-sand-200/70">
            Student Network
          </p>
        </div>
      )}
    </div>
  )
}

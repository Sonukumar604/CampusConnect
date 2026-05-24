export default function StatCard({ label, value, trend }) {
  return (
    <div className="glass-card rounded-3xl p-5 shadow-card">
      <p className="text-xs uppercase tracking-[0.35em] text-sand-200/70">{label}</p>
      <div className="mt-4 flex items-end justify-between">
        <p className="text-3xl font-semibold text-sand-50">{value}</p>
        {trend && <span className="text-xs text-tide-200">{trend}</span>}
      </div>
    </div>
  )
}

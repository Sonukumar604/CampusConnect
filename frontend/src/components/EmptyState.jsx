export default function EmptyState({ title, description }) {
  return (
    <div className="glass-card rounded-3xl p-6 text-center text-sand-200">
      <p className="text-lg font-semibold text-sand-50">{title}</p>
      {description && <p className="mt-2 text-sm text-sand-200/80">{description}</p>}
    </div>
  )
}

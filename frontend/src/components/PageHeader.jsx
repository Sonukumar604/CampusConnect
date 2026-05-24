export default function PageHeader({ title, subtitle, action }) {
  return (
    <div className="flex flex-col gap-4 border-b border-white/10 pb-6 md:flex-row md:items-end md:justify-between">
      <div>
        <p className="text-xs uppercase tracking-[0.35em] text-tide-200">
          CampusConnect
        </p>
        <h1 className="font-serif text-3xl text-sand-50 md:text-4xl">{title}</h1>
        {subtitle && <p className="mt-2 text-sm text-sand-200/80">{subtitle}</p>}
      </div>
      {action && <div>{action}</div>}
    </div>
  )
}

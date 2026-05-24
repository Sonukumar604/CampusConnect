export default function LoadingState({ label = 'Loading...' }) {
  return (
    <div className="flex items-center gap-3 text-sm text-sand-200">
      <span className="h-3 w-3 animate-pulse rounded-full bg-tide-200" />
      {label}
    </div>
  )
}

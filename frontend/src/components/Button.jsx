export default function Button({
  variant = 'primary',
  className = '',
  type = 'button',
  ...props
}) {
  const base =
    'inline-flex items-center justify-center rounded-full px-5 py-2.5 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-tide-300/60'
  const variants = {
    primary: 'bg-tide-300 text-ink-950 hover:bg-tide-200',
    secondary: 'border border-sand-200/40 text-sand-100 hover:border-sand-200',
    ghost: 'text-sand-100 hover:bg-white/5'
  }

  return (
    <button type={type} className={`${base} ${variants[variant]} ${className}`} {...props} />
  )
}

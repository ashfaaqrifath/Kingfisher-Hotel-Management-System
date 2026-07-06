export default function StatCard({ label, value, accent = 'navy', hint }) {
  const accents = {
    navy: 'border-l-navy-900',
    teal: 'border-l-teal-600',
    amber: 'border-l-amber',
    rust: 'border-l-rust',
  }
  return (
    <div className={`card border-l-4 ${accents[accent]}`}>
      <p className="text-xs uppercase tracking-wide text-navy-700 font-medium mb-2">{label}</p>
      <p className="stat-number">{value}</p>
      {hint && <p className="text-xs text-navy-700 mt-1">{hint}</p>}
    </div>
  )
}

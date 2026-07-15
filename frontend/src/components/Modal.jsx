export default function Modal({ open, title, onClose, children, width = 'max-w-lg' }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-navy-950/40 px-4">
      <div className={`bg-white rounded-[15px] w-full ${width} border border-sand-300`}>
        <div className="flex items-center justify-between px-5 py-4 border-b border-sand-300">
          <h3 className="font-display font-semibold text-lg">{title}</h3>
          <button onClick={onClose} className="text-navy-700 hover:text-navy-950 text-xl leading-none" aria-label="Close">
            ×
          </button>
        </div>
        <div className="p-5">{children}</div>
      </div>
    </div>
  )
}

import Sidebar from './Sidebar'

export default function Layout({ title, subtitle, actions, children }) {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 px-8 py-7 max-w-[1400px]">
        <div className="flex items-start justify-between mb-6">
          <div>
            <h1 className="text-2xl font-semibold text-navy-950">{title}</h1>
            {subtitle && <p className="text-sm text-navy-700 mt-1">{subtitle}</p>}
          </div>
          {actions && <div className="flex gap-2">{actions}</div>}
        </div>
        {children}
      </main>
    </div>
  )
}

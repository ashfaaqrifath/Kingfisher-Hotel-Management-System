import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/', label: 'Dashboard', icon: '◧' },
  { to: '/bookings', label: 'Bookings', icon: '▤' },
  { to: '/rooms', label: 'Rooms', icon: '▦' },
  { to: '/guests', label: 'Guests', icon: '◍' },
  { to: '/employees', label: 'Employees', icon: '◎', adminOnly: true },
  { to: '/inventory', label: 'Inventory', icon: '◫' },
  { to: '/activity-log', label: 'Activity Log', icon: '≡', adminOnly: true },
]

export default function Sidebar() {
  const { profile, isAdmin, logout } = useAuth()

  return (
    <aside className="w-60 shrink-0 bg-navy-950 text-sand-100 flex flex-col h-screen sticky top-0">
      <div className="px-5 py-6 border-b border-white/10">
        <p className="font-display font-semibold text-lg leading-tight tracking-tight">Kingfisher</p>
        <p className="text-xs text-sand-300 tracking-wide uppercase">Beach Resort · HMS</p>
      </div>

      <nav className="flex-1 py-4 px-2 space-y-1">
        {links
          .filter((l) => !l.adminOnly || isAdmin)
          .map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded text-sm transition-colors ${
                  isActive
                    ? 'bg-teal-600 text-white font-medium'
                    : 'text-sand-200 hover:bg-white/5'
                }`
              }
            >
              <span className="text-base w-4 text-center">{link.icon}</span>
              {link.label}
            </NavLink>
          ))}
      </nav>

      <div className="px-4 py-4 border-t border-white/10 text-sm">
        <p className="font-medium truncate">{profile?.full_name || 'Loading…'}</p>
        <p className="text-xs text-sand-300 uppercase tracking-wide mb-3">{profile?.role}</p>
        <button onClick={logout} className="btn btn-secondary w-full !bg-transparent !border-white/20 !text-sand-100 hover:!bg-white/10">
          Log out
        </button>
      </div>
    </aside>
  )
}

import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ children, adminOnly = false, ownerOnly = false }) {
  const { session, isAdmin, isOwner, loading } = useAuth()

  if (loading) {
    return <div className="h-screen flex items-center justify-center text-navy-700">Loading…</div>
  }
  if (!session) return <Navigate to="/login" replace />
  if (ownerOnly && !isOwner) return <Navigate to="/" replace />
  if (adminOnly && !isAdmin) return <Navigate to="/" replace />

  return children
}

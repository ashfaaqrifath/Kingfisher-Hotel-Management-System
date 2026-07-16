import { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { session, login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (session) return <Navigate to="/" replace />

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    const { error } = await login(email, password)
    setBusy(false)
    if (error) setError(error.message)
  }

  return (
    <div className="min-h-screen flex">
      {/* Left brand panel — flat navy, no gradient */}
      <div className="hidden md:flex flex-col justify-between w-1/2 bg-navy-950 text-sand-100 px-14 py-12">
        <div className="flex items-center gap-4">
          <img src="images/kingfisher-logo.png" alt="Kingfisher logo" className="w-44 h-auto object-contain" loading="lazy" />
          
        </div>
        <div>
          <p className="font-display text-3xl leading-snug max-w-sm">
            One dashboard for bookings, rooms, guests, and stock.
          </p>
          <p className="text-sand-300 text-sm mt-4 max-w-sm">
            Kingfisher Hotel Management System · v1.0
          </p>
        </div>
        <p className="text-xs text-sand-300">© Developed by Group CS03</p>
      </div>

      {/* Right form panel */}
      <div className="w-full md:w-1/2 flex items-center justify-center px-6">
        <form onSubmit={handleSubmit} className="w-full max-w-sm">
          <h1 className="text-2xl font-semibold text-navy-950 mb-1">Sign in</h1>
          <p className="text-sm text-navy-700 mb-6">Use your staff or admin account to continue.</p>

          {error && (
            <div className="mb-4 text-sm text-rust bg-rust/10 border border-rust/30 rounded px-3 py-2">
              {error}
            </div>
          )}

          <label className="label" htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            required
            className="input mb-4"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@kingfisherresort.lk"
          />

          <label className="label" htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            required
            className="input mb-6"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />

          <button type="submit" disabled={busy} className="btn btn-primary w-full">
            {busy ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  )
}

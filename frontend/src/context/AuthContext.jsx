import { createContext, useContext, useEffect, useRef, useState } from 'react'
import { supabase } from '../lib/supabaseClient'

const AuthContext = createContext(null)
const INACTIVITY_TIMEOUT_MS = 30 * 60 * 1000
const ACTIVITY_EVENTS = ['mousemove', 'keydown', 'click', 'scroll', 'touchstart']

export function AuthProvider({ children }) {
  const [session, setSession] = useState(null)
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const timeoutRef = useRef(null)
  const activeUserIdRef = useRef(null)

  async function loadProfile(userId) {
    activeUserIdRef.current = userId
    const { data } = await supabase
      .from('profiles')
      .select('id, full_name, role')
      .eq('id', userId)
      .single()
    if (activeUserIdRef.current === userId) setProfile(data || null)
  }

  function clearInactivityTimer() {
    if (timeoutRef.current) {
      window.clearTimeout(timeoutRef.current)
      timeoutRef.current = null
    }
  }

  function resetInactivityTimer() {
    clearInactivityTimer()
    timeoutRef.current = window.setTimeout(() => {
      void handleSessionTimeout()
    }, INACTIVITY_TIMEOUT_MS)
  }

  async function handleSessionTimeout() {
    clearInactivityTimer()
    if (!session?.user) return
    try {
      await supabase.auth.signOut()
    } catch (error) {
      console.error('Failed to sign out after inactivity timeout', error)
    }
    setSession(null)
    setProfile(null)
    activeUserIdRef.current = null
  }

  useEffect(() => {
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session)
      if (session?.user) loadProfile(session.user.id)
      setLoading(false)
    })

    const { data: listener } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session)
      if (session?.user) loadProfile(session.user.id)
      else {
        setProfile(null)
        activeUserIdRef.current = null
      }
    })

    return () => {
      clearInactivityTimer()
      listener.subscription.unsubscribe()
    }
  }, [])

  useEffect(() => {
    if (!session?.user) return

    const handleActivity = () => resetInactivityTimer()
    ACTIVITY_EVENTS.forEach((eventName) => window.addEventListener(eventName, handleActivity, true))
    resetInactivityTimer()

    return () => {
      clearInactivityTimer()
      ACTIVITY_EVENTS.forEach((eventName) => window.removeEventListener(eventName, handleActivity, true))
    }
  }, [session?.user])

  async function login(email, password) {
    const { error } = await supabase.auth.signInWithPassword({ email, password })
    return { error }
  }

  async function logout() {
    clearInactivityTimer()
    try {
      await supabase.auth.signOut()
    } catch (error) {
      console.error('Failed to sign out', error)
    }
    setSession(null)
    setProfile(null)
    activeUserIdRef.current = null
  }

  const value = {
    session,
    user: session?.user || null,
    profile,
    isAdmin: profile?.role === 'admin',
    loading,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}

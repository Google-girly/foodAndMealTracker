import { useEffect, useState } from 'react'
import { supabase } from '../supaBaseClient'
import { Navigate } from 'react-router-dom'

export default function ProtectedRoute({ children }) {

  const [session, setSession] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {

    const checkSession = async () => {
      const { data } = await supabase.auth.getSession()
      setSession(data.session)
      setLoading(false)
    }

    checkSession()

    const { data: listener } = supabase.auth.onAuthStateChange(
      (_event, session) => {
        setSession(session)
      }
    )

    return () => {
      listener.subscription.unsubscribe()
    }

  }, [])

  if (loading) return null

  return session ? children : <Navigate to="/login" replace />
}
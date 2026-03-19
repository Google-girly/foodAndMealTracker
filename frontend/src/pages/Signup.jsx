import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { supabase } from '../supaBaseClient'
import { ensureBackendUserFromSupabaseUser } from '../utils/backendUser'

export default function SignUp() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const checkSession = async () => {
      const { data } = await supabase.auth.getSession()
      const currentUser = data.session?.user

      if (currentUser) {
        try {
          await ensureBackendUserFromSupabaseUser(currentUser)
          navigate('/dashboard', { replace: true })
        } catch (error) {
          console.error('checkSession error', error)
        }
      }
    }

    checkSession()
  }, [navigate])

  const handleGoogleLogin = async () => {
    setLoading(true)
    try {
      await supabase.auth.signInWithOAuth({
        provider: 'google',
        options: {
          redirectTo: `${window.location.origin}/dashboard`,
        },
      })
    } catch (error) {
      console.error('handleGoogleLogin error', error)
      alert('Error signing in with Google')
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: '520px', margin: '80px auto', padding: '24px', textAlign: 'center' }}>
      <h1>Welcome</h1>
      <p>Sign in with your Google account to get started</p>

      <button onClick={handleGoogleLogin} disabled={loading} style={{ marginTop: '20px' }}>
        {loading ? 'Signing in...' : 'Continue with Google'}
      </button>
    </div>
  )
}

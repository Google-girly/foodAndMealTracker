import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { supabase } from '../supaBaseClient'
import { ensureBackendUserFromSupabaseUser } from '../utils/backendUser'

export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const checkSession = async () => {
      const { data } = await supabase.auth.getSession()
      if (data.session?.user) {
        try {
          await ensureBackendUserFromSupabaseUser(data.session.user)
          navigate('/dashboard', { replace: true })
        } catch (err) {
          console.error('checkSession error', err)
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
      setError('Error signing in with Google')
      setLoading(false)
    }
  }

  const handleEmailLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      const { data, error: authError } = await supabase.auth.signInWithPassword({
        email: email.trim(),
        password: password.trim(),
      })

      if (authError) throw authError

      if (data.user) {
        await ensureBackendUserFromSupabaseUser(data.user)
        navigate('/dashboard', { replace: true })
      }
    } catch (err) {
      console.error('handleEmailLogin error', err)
      setError(err.message || 'Failed to sign in')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: '520px', margin: '80px auto', padding: '24px', textAlign: 'center' }}>
      <h1>Sign In</h1>

      <button 
        type="button"
        onClick={handleGoogleLogin}
        disabled={loading}
        style={{ 
          width: '100%', 
          padding: '12px', 
          marginTop: '20px',
          backgroundColor: '#fff',
          border: '1px solid #ddd',
          borderRadius: '4px',
          cursor: loading ? 'not-allowed' : 'pointer',
          fontSize: '1em',
          fontWeight: '500',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '8px'
        }}
      >
        Continue with Google
      </button>

      <div style={{ margin: '16px 0', position: 'relative' }}>
        <div style={{ borderBottom: '1px solid #ddd', marginBottom: '16px' }}></div>
        <p style={{ margin: '0', fontSize: '0.9em', color: '#666' }}>or</p>
        <div style={{ borderTop: '1px solid #ddd', marginTop: '16px' }}></div>
      </div>

      <form onSubmit={handleEmailLogin} style={{ marginTop: '0' }}>
        <div style={{ marginBottom: '10px' }}>
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
              required
            />
          </label>
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Your password"
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
              required
            />
          </label>
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
      </form>

      {error && <p style={{ marginTop: '12px', color: '#d32f2f' }}>{error}</p>}

      <p style={{ marginTop: '20px', fontSize: '0.9em' }}>
        Don't have an account? <Link to="/">Sign up</Link>
      </p>
    </div>
  )
}
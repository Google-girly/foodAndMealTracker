import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { supabase } from '../supaBaseClient'
import { ensureBackendUser, ensureBackendUserFromSupabaseUser } from '../utils/backendUser'

export default function SignUp() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [message, setMessage] = useState('')
  const [showEmailSignup, setShowEmailSignup] = useState(false)

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

  const handleEmailSignup = async (e) => {
    e.preventDefault()

    if (!email.trim()) {
      alert('Email is required')
      return
    }

    if (!password.trim()) {
      alert('Password is required')
      return
    }

    if (password.length < 6) {
      alert('Password must be at least 6 characters')
      return
    }

    setLoading(true)
    setMessage('')

    try {
      const { data, error } = await supabase.auth.signUp({
        email: email.trim(),
        password: password.trim(),
      })

      if (error) throw error

      const user = data.user
      if (user) {
        await ensureBackendUser({
          email: email.trim(),
          fullName: fullName.trim(),
        })

        setMessage('Account created! Check your email to verify.')
        setEmail('')
        setPassword('')
        setFullName('')
      }
    } catch (error) {
      console.error('handleEmailSignup error', error)
      alert(error.message || 'Could not create account')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: '520px', margin: '80px auto', padding: '24px', textAlign: 'center' }}>
      <h1>Welcome</h1>

      {!showEmailSignup ? (
        <>
          <p>Sign in to get started</p>

          <button onClick={handleGoogleLogin} disabled={loading} style={{ marginTop: '20px' }}>
            {loading ? 'Signing in...' : 'Continue with Google'}
          </button>

          <div style={{ marginTop: '20px' }}>
            <p style={{ margin: '0 0 10px 0', fontSize: '0.9em', color: '#666' }}>or</p>
            <button onClick={() => setShowEmailSignup(true)}>Sign up with email</button>
          </div>
        </>
      ) : (
        <>
          <p>Create an account with email</p>

          <form onSubmit={handleEmailSignup} style={{ marginTop: '20px' }}>
            <div style={{ marginBottom: '10px' }}>
              <label>
                Full name (optional)
                <input
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="Your name"
                  style={{ display: 'block', width: '100%', marginTop: '4px' }}
                />
              </label>
            </div>

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
                  placeholder="At least 6 characters"
                  style={{ display: 'block', width: '100%', marginTop: '4px' }}
                  required
                />
              </label>
            </div>

            <button type="submit" disabled={loading}>
              {loading ? 'Creating...' : 'Create account'}
            </button>
          </form>

          <div style={{ marginTop: '16px' }}>
            <button onClick={() => setShowEmailSignup(false)}>Back</button>
          </div>
        </>
      )}

      {message && <p style={{ marginTop: '12px', color: '#666' }}>{message}</p>}
    </div>
  )
}

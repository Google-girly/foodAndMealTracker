import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { supabase } from '../supaBaseClient'
import { ensureBackendUser, ensureBackendUserFromSupabaseUser } from '../utils/backendUser'

export default function SignUp() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [isPasswordSignup, setIsPasswordSignup] = useState(false)

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

  const handleSignupWithPassword = async (e) => {
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

        setMessage('Account created! Check your email to verify, then log in.')
        setEmail('')
        setPassword('')
        setFullName('')
      }
    } catch (error) {
      console.error('handleSignupWithPassword error', error)
      alert(error.message || 'Could not create account')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateBackendUser = async (e) => {
    e.preventDefault()

    if (!email.trim()) {
      alert('Email is required')
      return
    }

    setLoading(true)
    setMessage('')

    try {
      await ensureBackendUser({
        email: email.trim(),
        fullName: fullName.trim(),
      })

      setMessage('Account profile saved. Now sign in with Google using the same email.')
    } catch (error) {
      console.error('handleCreateBackendUser error', error)
      alert(error.message || 'Could not create backend user')
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleLogin = async () => {
    await supabase.auth.signInWithOAuth({
      provider: 'google',
      options: {
        redirectTo: `${window.location.origin}/dashboard`,
      },
    })
  }

  return (
    <div style={{ maxWidth: '520px', margin: '80px auto', padding: '24px' }}>
      <h1>Welcome</h1>
      <p>Create your account</p>

      {!isPasswordSignup ? (
        <>
          <form onSubmit={handleCreateBackendUser}>
            <div style={{ marginBottom: '10px' }}>
              <label>
                Full name
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

            <button type="submit" disabled={loading}>
              {loading ? 'Saving...' : 'Create profile'}
            </button>
          </form>

          <div style={{ marginTop: '16px' }}>
            <button onClick={handleGoogleLogin}>Continue with Google</button>
          </div>

          <div style={{ marginTop: '16px', textAlign: 'center' }}>
            <p style={{ marginBottom: '8px' }}>or</p>
            <button onClick={() => setIsPasswordSignup(true)}>Sign up with email & password</button>
          </div>
        </>
      ) : (
        <>
          <form onSubmit={handleSignupWithPassword}>
            <div style={{ marginBottom: '10px' }}>
              <label>
                Full name
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

          <div style={{ marginTop: '16px', textAlign: 'center' }}>
            <button onClick={() => setIsPasswordSignup(false)}>Back</button>
          </div>
        </>
      )}

      {message && <p style={{ marginTop: '12px', textAlign: 'center', color: '#666' }}>{message}</p>}
    </div>
  )
}

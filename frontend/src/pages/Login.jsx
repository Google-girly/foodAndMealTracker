import { supabase } from '../supaBaseClient'

export default function Login() {

  const handleGoogleLogin = async () => {
    await supabase.auth.signInWithOAuth({
      provider: 'google',
      options: {
            redirectTo: `${window.location.origin}/dashboard`      
          }
    })
  }

  return (
    <div style={{ textAlign: 'center', marginTop: '100px' }}>
      <h1>Login</h1>
      <button onClick={handleGoogleLogin}>
        Continue with Google
      </button>
    </div>
  )
}
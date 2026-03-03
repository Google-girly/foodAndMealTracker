import { useEffect, useState } from 'react'
import { supabase } from '../supaBaseClient'

export default function Dashboard() {

  const [user, setUser] = useState(null)

  useEffect(() => {
    const fetchUser = async () => {
      const { data } = await supabase.auth.getUser()
      setUser(data.user)
    }

    fetchUser()
  }, [])

  const handleLogout = async () => {
    await supabase.auth.signOut()
    window.location.href = "/login"
  }

  return (
    <div style={{ textAlign: 'center', marginTop: '100px' }}>
      <h1>Dashboard</h1>
      {user && (
        <>
          <p>Welcome: {user.email}</p>
          <button onClick={handleLogout}>Logout</button>
        </>
      )}
    </div>
  )
}
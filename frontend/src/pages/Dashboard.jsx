import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { supabase } from '../supaBaseClient'
import { ensureBackendUserFromSupabaseUser } from '../utils/backendUser'

export default function Dashboard() {

  const [user, setUser] = useState(null)
  const [backendUser, setBackendUser] = useState(null)

  useEffect(() => {
    const fetchUser = async () => {
      const { data } = await supabase.auth.getUser()
      setUser(data.user)

      if (data.user) {
        try {
          const syncedUser = await ensureBackendUserFromSupabaseUser(data.user)
          setBackendUser(syncedUser)
        } catch (error) {
          console.error('Failed to sync backend user', error)
        }
      }
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
          <div style={{ marginTop: '20px' }}>
            <Link to="/meals" style={{ marginRight: '10px' }}>
              <button>Create Meal</button>
            </Link>
            <Link to="/view-meals" style={{ marginRight: '10px' }}>
              <button>View Meals</button>
            </Link>

            {backendUser?.admin && (
              <Link to="/admin" style={{ marginRight: '10px' }}>
                <button>Admin Page</button>
              </Link>
            )}

            <button onClick={handleLogout}>Logout</button>
          </div>
        </>
      )}
    </div>
  )
}
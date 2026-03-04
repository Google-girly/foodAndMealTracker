import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
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
          <div style={{ marginTop: '20px' }}>
            <Link to="/meals" style={{ marginRight: '10px' }}>
              <button>Create Meal</button>
            </Link>
            <Link to="/view-meals" style={{ marginRight: '10px' }}>
              <button>View Meals</button>
            </Link>
            <button onClick={handleLogout}>Logout</button>
          </div>
        </>
      )}
    </div>
  )
}
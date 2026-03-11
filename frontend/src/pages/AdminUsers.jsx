import { Link } from 'react-router-dom'

export default function AdminUsers() {
  return (
    <div style={{ textAlign: 'center', marginTop: '100px' }}>
      <h1>Admin: Users</h1>
      <p>Placeholder UI — will connect to backend once admin user management endpoints exist.</p>

      <div style={{ marginTop: '20px' }}>
        <Link to="/admin">
          <button>Back to Admin Panel</button>
        </Link>
      </div>
    </div>
  )
}
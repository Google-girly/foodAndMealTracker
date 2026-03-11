import { Link } from 'react-router-dom'

export default function AdminPanel() {
  return (
    <div style={{ textAlign: 'center', marginTop: '100px' }}>
      <h1>Admin Panel</h1>
      <p>Admin tools (UI only for now — backend wiring coming later).</p>

      <div style={{ marginTop: '20px' }}>
        <Link to="/admin/users" style={{ marginRight: '10px' }}>
          <button>Edit Users</button>
        </Link>

        <Link to="/admin/foods" style={{ marginRight: '10px' }}>
          <button>Edit Foods</button>
        </Link>

        <Link to="/admin/meals" style={{ marginRight: '10px' }}>
          <button>Edit Meals</button>
        </Link>

        <Link to="/dashboard" style={{ marginLeft: '10px' }}>
          <button>Back</button>
        </Link>
      </div>
    </div>
  )
}
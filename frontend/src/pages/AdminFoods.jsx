import { Link } from 'react-router-dom'

export default function AdminFoods() {
  return (
    <div style={{ textAlign: 'center', marginTop: '100px' }}>
      <h1>Admin: Foods</h1>
      <p>Placeholder UI — will connect later.</p>

      <div style={{ marginTop: '20px' }}>
        <Link to="/admin">
          <button>Back to Admin Panel</button>
        </Link>
      </div>
    </div>
  )
}
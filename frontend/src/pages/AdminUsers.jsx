import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAuthenticatedBackendUser, getUserRequestHeaders } from '../utils/backendUser'

const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

const emptyForm = {
  email: '',
  fullName: '',
  admin: false,
}

export default function AdminUsers() {
  const [currentAdmin, setCurrentAdmin] = useState(null)
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [authorized, setAuthorized] = useState(false)

  const loadUsers = async (adminUser) => {
    const response = await fetch(`${API_BASE}/admin/users`, {
      headers: getUserRequestHeaders(adminUser.id, false),
    })

    if (!response.ok) {
      throw new Error(await response.text() || 'Failed to load users')
    }

    const data = await response.json()
    setUsers(data)
  }

  useEffect(() => {
    const initialize = async () => {
      setLoading(true)
      setError('')

      try {
        const adminUser = await getAuthenticatedBackendUser()
        setCurrentAdmin(adminUser)

        const pingResponse = await fetch(`${API_BASE}/admin/ping`, {
          headers: getUserRequestHeaders(adminUser.id, false),
        })

        if (!pingResponse.ok) {
          setAuthorized(false)
          setError('You do not have admin access.')
          return
        }

        setAuthorized(true)
        await loadUsers(adminUser)
      } catch (err) {
        console.error('initialize admin users error', err)
        setError(err.message || 'Failed to load admin user tools')
      } finally {
        setLoading(false)
      }
    }

    initialize()
  }, [])

  const resetForm = () => {
    setForm(emptyForm)
    setEditingId(null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!currentAdmin) return

    setSaving(true)
    setError('')
    setMessage('')

    try {
      const endpoint = editingId
        ? `${API_BASE}/admin/users/${editingId}`
        : `${API_BASE}/admin/users`

      const response = await fetch(endpoint, {
        method: editingId ? 'PUT' : 'POST',
        headers: getUserRequestHeaders(currentAdmin.id),
        body: JSON.stringify({
          email: form.email.trim(),
          fullName: form.fullName.trim(),
          admin: form.admin,
        }),
      })

      if (!response.ok) {
        throw new Error(await response.text() || 'Failed to save user')
      }

      await loadUsers(currentAdmin)
      resetForm()
      setMessage(editingId ? 'User updated.' : 'User created.')
    } catch (err) {
      console.error('handleSubmit admin users error', err)
      setError(err.message || 'Failed to save user')
    } finally {
      setSaving(false)
    }
  }

  const handleEdit = (user) => {
    setForm({
      email: user.email || '',
      fullName: user.fullName || '',
      admin: Boolean(user.admin),
    })
    setEditingId(user.id)
    setMessage('')
    setError('')
  }

  const handleDelete = async (user) => {
    if (!currentAdmin) return

    const confirmed = window.confirm(`Delete ${user.email}?`)
    if (!confirmed) return

    setError('')
    setMessage('')

    try {
      const response = await fetch(`${API_BASE}/admin/users/${user.id}`, {
        method: 'DELETE',
        headers: getUserRequestHeaders(currentAdmin.id, false),
      })

      if (!response.ok) {
        throw new Error(await response.text() || 'Failed to delete user')
      }

      await loadUsers(currentAdmin)
      if (editingId === user.id) {
        resetForm()
      }
      setMessage('User deleted.')
    } catch (err) {
      console.error('handleDelete admin users error', err)
      setError(err.message || 'Failed to delete user')
    }
  }

  if (loading) {
    return <div style={{ textAlign: 'center', marginTop: '100px' }}>Loading admin users...</div>
  }

  if (!authorized) {
    return (
      <div style={{ textAlign: 'center', marginTop: '100px' }}>
        <h1>Admin: Users</h1>
        <p>{error || 'You do not have admin access.'}</p>
        <Link to="/dashboard">
          <button>Back to Dashboard</button>
        </Link>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: '1100px', margin: '40px auto', padding: '20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1>Admin: Users</h1>
          <p>Create, update, and delete backend user records.</p>
        </div>
        <div>
          <Link to="/admin" style={{ marginRight: '10px' }}>
            <button>Back to Admin Panel</button>
          </Link>
          <Link to="/dashboard">
            <button>Dashboard</button>
          </Link>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(320px, 360px) 1fr', gap: '24px', alignItems: 'start' }}>
        <form onSubmit={handleSubmit} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '16px' }}>
          <h2 style={{ marginTop: 0 }}>{editingId ? 'Edit User' : 'Create User'}</h2>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Email
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
              required
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Full name
            <input
              value={form.fullName}
              onChange={(e) => setForm((prev) => ({ ...prev, fullName: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
            <input
              type="checkbox"
              checked={form.admin}
              onChange={(e) => setForm((prev) => ({ ...prev, admin: e.target.checked }))}
            />
            Admin access
          </label>

          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <button type="submit" disabled={saving}>
              {saving ? 'Saving...' : editingId ? 'Update User' : 'Create User'}
            </button>
            {editingId && (
              <button type="button" onClick={resetForm}>
                Cancel
              </button>
            )}
          </div>

          {message && <p style={{ color: '#1b5e20', marginTop: '12px' }}>{message}</p>}
          {error && <p style={{ color: '#b00020', marginTop: '12px' }}>{error}</p>}
        </form>

        <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '16px' }}>
          <h2 style={{ marginTop: 0 }}>Existing Users</h2>

          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>ID</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Email</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Name</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Admin</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{user.id}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{user.email}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{user.fullName || '—'}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{user.admin ? 'Yes' : 'No'}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>
                      <button onClick={() => handleEdit(user)} style={{ marginRight: '8px' }}>
                        Edit
                      </button>
                      <button onClick={() => handleDelete(user)} disabled={currentAdmin?.id === user.id}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
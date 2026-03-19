import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAuthenticatedBackendUser, getUserRequestHeaders } from '../utils/backendUser'

const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

const emptyForm = {
  name: '',
  calories: '',
  protein: '',
  carbs: '',
  fat: '',
  isPublic: true,
}

export default function AdminFoods() {
  const [currentAdmin, setCurrentAdmin] = useState(null)
  const [foods, setFoods] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [authorized, setAuthorized] = useState(false)

  const loadFoods = async (adminUser) => {
    const response = await fetch(`${API_BASE}/admin/foods`, {
      headers: getUserRequestHeaders(adminUser.id, false),
    })

    if (!response.ok) {
      throw new Error(await response.text() || 'Failed to load foods')
    }

    const data = await response.json()
    setFoods(data)
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
        await loadFoods(adminUser)
      } catch (err) {
        console.error('initialize admin foods error', err)
        setError(err.message || 'Failed to load admin food tools')
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

  const buildPayload = () => ({
    name: form.name.trim(),
    calories: form.calories === '' ? null : Number(form.calories),
    protein: form.protein === '' ? null : Number(form.protein),
    carbs: form.carbs === '' ? null : Number(form.carbs),
    fat: form.fat === '' ? null : Number(form.fat),
    isPublic: form.isPublic,
    createdById: currentAdmin?.id ?? null,
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!currentAdmin) return

    setSaving(true)
    setError('')
    setMessage('')

    try {
      const endpoint = editingId
        ? `${API_BASE}/admin/foods/${editingId}`
        : `${API_BASE}/admin/foods`

      const response = await fetch(endpoint, {
        method: editingId ? 'PUT' : 'POST',
        headers: getUserRequestHeaders(currentAdmin.id),
        body: JSON.stringify(buildPayload()),
      })

      if (!response.ok) {
        throw new Error(await response.text() || 'Failed to save food')
      }

      await loadFoods(currentAdmin)
      resetForm()
      setMessage(editingId ? 'Food updated.' : 'Food created.')
    } catch (err) {
      console.error('handleSubmit admin foods error', err)
      setError(err.message || 'Failed to save food')
    } finally {
      setSaving(false)
    }
  }

  const handleEdit = (food) => {
    setForm({
      name: food.name || '',
      calories: food.calories ?? '',
      protein: food.protein ?? '',
      carbs: food.carbs ?? '',
      fat: food.fat ?? '',
      isPublic: Boolean(food.isPublic),
    })
    setEditingId(food.id)
    setMessage('')
    setError('')
  }

  const handleDelete = async (food) => {
    if (!currentAdmin) return

    const confirmed = window.confirm(`Delete ${food.name}?`)
    if (!confirmed) return

    setError('')
    setMessage('')

    try {
      const response = await fetch(`${API_BASE}/admin/foods/${food.id}`, {
        method: 'DELETE',
        headers: getUserRequestHeaders(currentAdmin.id, false),
      })

      if (!response.ok) {
        throw new Error(await response.text() || 'Failed to delete food')
      }

      await loadFoods(currentAdmin)
      if (editingId === food.id) {
        resetForm()
      }
      setMessage('Food deleted.')
    } catch (err) {
      console.error('handleDelete admin foods error', err)
      setError(err.message || 'Failed to delete food')
    }
  }

  if (loading) {
    return <div style={{ textAlign: 'center', marginTop: '100px' }}>Loading admin foods...</div>
  }

  if (!authorized) {
    return (
      <div style={{ textAlign: 'center', marginTop: '100px' }}>
        <h1>Admin: Foods</h1>
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
          <h1>Admin: Foods</h1>
          <p>Create, update, and delete foods from one place.</p>
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
          <h2 style={{ marginTop: 0 }}>{editingId ? 'Edit Food' : 'Create Food'}</h2>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Name
            <input
              value={form.name}
              onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))}
              required
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Calories
            <input
              type="number"
              min="0"
              value={form.calories}
              onChange={(e) => setForm((prev) => ({ ...prev, calories: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Protein
            <input
              type="number"
              min="0"
              step="0.1"
              value={form.protein}
              onChange={(e) => setForm((prev) => ({ ...prev, protein: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Carbs
            <input
              type="number"
              min="0"
              step="0.1"
              value={form.carbs}
              onChange={(e) => setForm((prev) => ({ ...prev, carbs: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Fat
            <input
              type="number"
              min="0"
              step="0.1"
              value={form.fat}
              onChange={(e) => setForm((prev) => ({ ...prev, fat: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
            <input
              type="checkbox"
              checked={form.isPublic}
              onChange={(e) => setForm((prev) => ({ ...prev, isPublic: e.target.checked }))}
            />
            Public food
          </label>

          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <button type="submit" disabled={saving}>
              {saving ? 'Saving...' : editingId ? 'Update Food' : 'Create Food'}
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
          <h2 style={{ marginTop: 0 }}>Existing Foods</h2>

          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>ID</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Name</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Calories</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Macros</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Public</th>
                  <th style={{ textAlign: 'left', padding: '8px', borderBottom: '1px solid #ddd' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {foods.map((food) => (
                  <tr key={food.id}>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{food.id}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{food.name}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{food.calories ?? '—'}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>
                      P: {food.protein ?? '—'} / C: {food.carbs ?? '—'} / F: {food.fat ?? '—'}
                    </td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>{food.isPublic ? 'Yes' : 'No'}</td>
                    <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>
                      <button onClick={() => handleEdit(food)} style={{ marginRight: '8px' }}>
                        Edit
                      </button>
                      <button onClick={() => handleDelete(food)}>
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
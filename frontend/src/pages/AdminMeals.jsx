import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAuthenticatedBackendUser, getUserRequestHeaders } from '../utils/backendUser'

const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

const emptyForm = {
  usersId: '',
  name: '',
  mealType: 'snack',
  mealDate: '',
  description: '',
}

const formatMealDateForInput = (value) => {
  if (!value) return ''
  if (typeof value === 'string') {
    return value.includes('T') ? value.split('T')[0] : value
  }
  return ''
}

export default function AdminMeals() {
  const [currentAdmin, setCurrentAdmin] = useState(null)
  const [users, setUsers] = useState([])
  const [meals, setMeals] = useState([])
  const [mealFoods, setMealFoods] = useState([])
  const [selectedMealId, setSelectedMealId] = useState('')
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [authorized, setAuthorized] = useState(false)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const usersById = useMemo(() => {
    const map = new Map()
    users.forEach((user) => {
      map.set(String(user.id), user)
    })
    return map
  }, [users])

  const mealFoodCountByMealId = useMemo(() => {
    const counts = new Map()
    mealFoods.forEach((item) => {
      const key = String(item.mealId)
      counts.set(key, (counts.get(key) || 0) + 1)
    })
    return counts
  }, [mealFoods])

  const loadUsers = async (adminUser) => {
    let response = await fetch(`${API_BASE}/admin/users`, {
      headers: getUserRequestHeaders(adminUser.id, false),
    })

    if (response.status === 404) {
      response = await fetch(`${API_BASE}/users`)
    }

    if (!response.ok) {
      throw new Error(await response.text() || 'Failed to load users')
    }

    const data = await response.json()
    setUsers(data)
  }

  const loadMeals = async (adminUser) => {
    let response = await fetch(`${API_BASE}/admin/meals`, {
      headers: getUserRequestHeaders(adminUser.id, false),
    })

    if (response.status === 404) {
      response = await fetch(`${API_BASE}/meals`, {
        headers: getUserRequestHeaders(adminUser.id, false),
      })
    }

    if (!response.ok) {
      throw new Error(await response.text() || 'Failed to load meals')
    }

    const data = await response.json()
    setMeals(data)
  }

  const loadMealFoods = async () => {
    const response = await fetch(`${API_BASE}/meal-foods`)
    if (!response.ok) {
      setMealFoods([])
      return
    }

    const data = await response.json()
    setMealFoods(data)
  }

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

      await Promise.all([
        loadUsers(adminUser),
        loadMeals(adminUser),
        loadMealFoods(),
      ])
    } catch (err) {
      console.error('initialize admin meals error', err)
      setError(err.message || 'Failed to load admin meal tools')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    initialize()
  }, [])

  const resetForm = () => {
    setForm(emptyForm)
    setEditingId(null)
    setSelectedMealId('')
  }

  const handleSelectMeal = (e) => {
    const nextId = e.target.value
    setSelectedMealId(nextId)
    setMessage('')
    setError('')

    if (!nextId) {
      resetForm()
      return
    }

    const meal = meals.find((m) => String(m.id) === nextId)
    if (!meal) return

    setEditingId(meal.id)
    setForm({
      usersId: String(meal.usersId ?? ''),
      name: meal.name || '',
      mealType: meal.mealType || 'snack',
      mealDate: formatMealDateForInput(meal.mealDate),
      description: meal.description || '',
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!currentAdmin) return

    setSaving(true)
    setError('')
    setMessage('')

    try {
      const payload = {
        usersId: Number(form.usersId),
        name: form.name.trim(),
        mealType: form.mealType,
        mealDate: form.mealDate || null,
        description: form.description.trim() || null,
      }

      const endpoint = editingId
        ? `${API_BASE}/admin/meals/${editingId}`
        : `${API_BASE}/admin/meals`

      const response = await fetch(endpoint, {
        method: editingId ? 'PUT' : 'POST',
        headers: getUserRequestHeaders(currentAdmin.id),
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Admin meal routes are not deployed on the backend yet. Redeploy the backend service and try again.')
        }
        throw new Error(await response.text() || 'Failed to save meal')
      }

      await Promise.all([loadMeals(currentAdmin), loadMealFoods()])
      resetForm()
      setMessage(editingId ? 'Meal updated.' : 'Meal created.')
    } catch (err) {
      console.error('handleSubmit admin meals error', err)
      setError(err.message || 'Failed to save meal')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!currentAdmin || !selectedMealId) return

    const meal = meals.find((m) => String(m.id) === selectedMealId)
    if (!meal) return

    const confirmed = window.confirm(`Delete meal \"${meal.name || 'Untitled meal'}\"?`)
    if (!confirmed) return

    setError('')
    setMessage('')

    try {
      const response = await fetch(`${API_BASE}/admin/meals/${meal.id}`, {
        method: 'DELETE',
        headers: getUserRequestHeaders(currentAdmin.id, false),
      })

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Admin meal routes are not deployed on the backend yet. Redeploy the backend service and try again.')
        }
        throw new Error(await response.text() || 'Failed to delete meal')
      }

      await Promise.all([loadMeals(currentAdmin), loadMealFoods()])
      resetForm()
      setMessage('Meal deleted.')
    } catch (err) {
      console.error('handleDelete admin meals error', err)
      setError(err.message || 'Failed to delete meal')
    }
  }

  const selectedMeal = meals.find((meal) => String(meal.id) === selectedMealId)
  const selectedOwner = selectedMeal ? usersById.get(String(selectedMeal.usersId)) : null

  if (loading) {
    return <div style={{ textAlign: 'center', marginTop: '100px' }}>Loading admin meals...</div>
  }

  if (!authorized) {
    return (
      <div style={{ textAlign: 'center', marginTop: '100px' }}>
        <h1>Admin: Meals</h1>
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
          <h1>Admin: Meals</h1>
          <p>Edit meal details, owner user, and metadata.</p>
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

      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(340px, 400px) 1fr', gap: '24px', alignItems: 'start' }}>
        <form onSubmit={handleSubmit} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '16px' }}>
          <h2 style={{ marginTop: 0 }}>{editingId ? 'Edit Meal' : 'Create Meal'}</h2>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Meal owner (user)
            <select
              value={form.usersId}
              onChange={(e) => setForm((prev) => ({ ...prev, usersId: e.target.value }))}
              required
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            >
              <option value="">Select user...</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.email}{user.fullName ? ` (${user.fullName})` : ''}
                </option>
              ))}
            </select>
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Meal name
            <input
              value={form.name}
              onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))}
              required
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Meal type
            <select
              value={form.mealType}
              onChange={(e) => setForm((prev) => ({ ...prev, mealType: e.target.value }))}
              required
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            >
              <option value="breakfast">Breakfast</option>
              <option value="lunch">Lunch</option>
              <option value="dinner">Dinner</option>
              <option value="snack">Snack</option>
            </select>
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Meal date
            <input
              type="date"
              value={form.mealDate}
              onChange={(e) => setForm((prev) => ({ ...prev, mealDate: e.target.value }))}
              style={{ display: 'block', width: '100%', marginTop: '4px' }}
            />
          </label>

          <label style={{ display: 'block', marginBottom: '12px' }}>
            Description
            <textarea
              value={form.description}
              onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
              rows={4}
              style={{ display: 'block', width: '100%', marginTop: '4px', resize: 'vertical' }}
            />
          </label>

          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <button type="submit" disabled={saving}>
              {saving ? 'Saving...' : editingId ? 'Update Meal' : 'Create Meal'}
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
          <h2 style={{ marginTop: 0 }}>Existing Meals</h2>

          {meals.length === 0 && (
            <p style={{ color: '#666' }}>
              No meals found. Create a meal with the form, or confirm the backend /admin/meals endpoint returns data.
            </p>
          )}

          <label style={{ display: 'block', marginBottom: '16px' }}>
            Select a meal to edit
            <select
              value={selectedMealId}
              onChange={handleSelectMeal}
              style={{ display: 'block', width: '100%', marginTop: '8px', padding: '10px' }}
            >
              <option value="">Choose a meal...</option>
              {[...meals]
                .sort((a, b) => (a.name || '').localeCompare(b.name || ''))
                .map((meal) => {
                  const owner = usersById.get(String(meal.usersId))
                  const ownerLabel = owner?.email || `User #${meal.usersId}`
                  return (
                    <option key={meal.id} value={meal.id}>
                      {meal.name || 'Untitled meal'} • {meal.mealType || 'type?'} • {ownerLabel}
                    </option>
                  )
                })}
            </select>
          </label>

          {selectedMeal ? (
            <div style={{ background: '#fafafa', borderRadius: '8px', padding: '16px' }}>
              <p><strong>Meal ID:</strong> {selectedMeal.id}</p>
              <p><strong>Name:</strong> {selectedMeal.name || '—'}</p>
              <p><strong>Type:</strong> {selectedMeal.mealType || '—'}</p>
              <p><strong>Date:</strong> {formatMealDateForInput(selectedMeal.mealDate) || '—'}</p>
              <p><strong>Description:</strong> {selectedMeal.description || '—'}</p>
              <p><strong>Owner ID:</strong> {selectedMeal.usersId}</p>
              <p><strong>Owner Email:</strong> {selectedOwner?.email || '—'}</p>
              <p><strong>Owner Name:</strong> {selectedOwner?.fullName || '—'}</p>
              <p><strong>Foods in meal:</strong> {mealFoodCountByMealId.get(String(selectedMeal.id)) || 0}</p>

              <div style={{ marginTop: '12px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                <button type="button" onClick={handleDelete}>
                  Delete selected meal
                </button>
              </div>
            </div>
          ) : (
            <p style={{ color: '#666' }}>Use the dropdown to choose a meal to edit or delete.</p>
          )}
        </div>
      </div>
    </div>
  )
}
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
  const [selectedFoodId, setSelectedFoodId] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [authorized, setAuthorized] = useState(false)

  const loadFoods = async (adminUser) => {
    let response = await fetch(`${API_BASE}/admin/foods`, {
      headers: getUserRequestHeaders(adminUser.id, false),
    })

    if (response.status === 404) {
      response = await fetch(`${API_BASE}/foods`)
    }

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
    setSelectedFoodId('')
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
        if (response.status === 404) {
          throw new Error('Admin food routes are not deployed on the backend yet. Redeploy the backend service and try again.')
        }
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
    setSelectedFoodId(String(food.id))
    setMessage('')
    setError('')
  }

  const handleSelectFood = (e) => {
    const nextId = e.target.value
    setSelectedFoodId(nextId)

    if (!nextId) {
      resetForm()
      return
    }

    const selectedFood = foods.find((food) => String(food.id) === nextId)
    if (selectedFood) {
      handleEdit(selectedFood)
    }
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
        if (response.status === 404) {
          throw new Error('Admin food routes are not deployed on the backend yet. Redeploy the backend service and try again.')
        }
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

          <label style={{ display: 'block', marginBottom: '16px' }}>
            Select a food to edit
            <select
              value={selectedFoodId}
              onChange={handleSelectFood}
              style={{ display: 'block', width: '100%', marginTop: '8px', padding: '10px' }}
            >
              <option value="">Choose a food...</option>
              {[...foods]
                .sort((a, b) => (a.name || '').localeCompare(b.name || ''))
                .map((food) => (
                  <option key={food.id} value={food.id}>
                    {food.name} {food.calories != null ? `(${food.calories} cal)` : ''}
                  </option>
                ))}
            </select>
          </label>

          {selectedFoodId ? (
            (() => {
              const selectedFood = foods.find((food) => String(food.id) === selectedFoodId)

              if (!selectedFood) {
                return <p>Food not found.</p>
              }

              return (
                <div style={{ background: '#fafafa', borderRadius: '8px', padding: '16px' }}>
                  <p><strong>Name:</strong> {selectedFood.name}</p>
                  <p><strong>Calories:</strong> {selectedFood.calories ?? '—'}</p>
                  <p><strong>Protein:</strong> {selectedFood.protein ?? '—'}</p>
                  <p><strong>Carbs:</strong> {selectedFood.carbs ?? '—'}</p>
                  <p><strong>Fat:</strong> {selectedFood.fat ?? '—'}</p>
                  <p><strong>Public:</strong> {selectedFood.isPublic ? 'Yes' : 'No'}</p>

                  <div style={{ marginTop: '12px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                    <button type="button" onClick={() => handleEdit(selectedFood)}>
                      Load into form
                    </button>
                    <button type="button" onClick={() => handleDelete(selectedFood)}>
                      Delete selected food
                    </button>
                  </div>
                </div>
              )
            })()
          ) : (
            <p style={{ color: '#666' }}>Use the dropdown to choose a food to edit or delete.</p>
          )}
        </div>
      </div>
    </div>
  )
}
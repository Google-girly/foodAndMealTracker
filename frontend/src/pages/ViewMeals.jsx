import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { supabase } from '../supaBaseClient'

const API_BASE = 'http://localhost:8080'

export default function ViewMeals() {
  const [meals, setMeals] = useState([])
  const [userId, setUserId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [expandedMeal, setExpandedMeal] = useState(null)
  const [editingMeal, setEditingMeal] = useState(null)
  const [editName, setEditName] = useState('')
  const [editType, setEditType] = useState('snack')

  useEffect(() => {
    const fetchUser = async () => {
      const { data } = await supabase.auth.getUser()
      setUserId(1)
      fetchMeals(1)
    }
    fetchUser()
  }, [])

  const fetchMeals = async (id) => {
    setLoading(true)
    try {
      const response = await fetch(`${API_BASE}/meals`)
      if (!response.ok) throw new Error('Failed to fetch meals')
      const allMeals = await response.json()
      
      // Filter meals for current user
      const userMeals = allMeals.filter((m) => m.usersId === id)
      setMeals(userMeals)
    } catch (error) {
      console.error('fetchMeals error', error)
      alert('Error fetching meals')
    } finally {
      setLoading(false)
    }
  }

  const deleteMeal = async (mealId) => {
    if (!window.confirm('Are you sure you want to delete this meal?')) return

    try {
      const response = await fetch(`${API_BASE}/meals/${mealId}`, {
        method: 'DELETE',
      })

      if (!response.ok) throw new Error('Failed to delete meal')
      setMeals((prev) => prev.filter((m) => m.id !== mealId))
      alert('Meal deleted successfully!')
    } catch (error) {
      console.error('deleteMeal error', error)
      alert('Error deleting meal: ' + error.message)
    }
  }

  const startEditingMeal = (meal) => {
    setEditingMeal(meal.id)
    setEditName(meal.name)
    setEditType(meal.mealType)
  }

  const saveEditMeal = async (mealId) => {
    if (!editName.trim()) {
      alert('Meal name cannot be empty')
      return
    }

    try {
      const response = await fetch(`${API_BASE}/meals/${mealId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: editName,
          mealType: editType,
        }),
      })

      if (!response.ok) throw new Error('Failed to update meal')
      const updatedMeal = await response.json()
      
      setMeals((prev) =>
        prev.map((m) => (m.id === mealId ? updatedMeal : m))
      )
      setEditingMeal(null)
      alert('Meal updated successfully!')
    } catch (error) {
      console.error('saveEditMeal error', error)
      alert('Error updating meal: ' + error.message)
    }
  }

  const cancelEditMeal = () => {
    setEditingMeal(null)
    setEditName('')
    setEditType('snack')
  }

  const handleLogout = async () => {
    await supabase.auth.signOut()
    window.location.href = '/login'
  }

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>My Meals</h1>
        <div>
          <Link to="/dashboard" style={{ marginRight: '10px' }}>
            <button>Dashboard</button>
          </Link>
          <Link to="/meals" style={{ marginRight: '10px' }}>
            <button>Create Meal</button>
          </Link>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </div>

      {loading ? (
        <p>Loading meals...</p>
      ) : meals.length === 0 ? (
        <p>
          No meals yet.{' '}
          <Link to="/meals">
            <button>Create your first meal</button>
          </Link>
        </p>
      ) : (
        <div>
          {meals.map((meal) => (
            <div
              key={meal.id}
              style={{
                border: '1px solid #ddd',
                padding: '10px',
                marginBottom: '10px',
                borderRadius: '4px',
              }}
            >
              {editingMeal === meal.id ? (
                <div style={{ marginBottom: '10px' }}>
                  <h3>Edit Meal</h3>
                  <label>
                    Name:{' '}
                    <input
                      value={editName}
                      onChange={(e) => setEditName(e.target.value)}
                    />
                  </label>
                  <label style={{ marginLeft: '10px' }}>
                    Type:{' '}
                    <select
                      value={editType}
                      onChange={(e) => setEditType(e.target.value)}
                    >
                      <option value="breakfast">Breakfast</option>
                      <option value="lunch">Lunch</option>
                      <option value="dinner">Dinner</option>
                      <option value="snack">Snack</option>
                    </select>
                  </label>
                  <button
                    onClick={() => saveEditMeal(meal.id)}
                    style={{ marginLeft: '10px' }}
                  >
                    Save
                  </button>
                  <button
                    onClick={cancelEditMeal}
                    style={{ marginLeft: '8px' }}
                  >
                    Cancel
                  </button>
                </div>
              ) : (
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    cursor: 'pointer',
                  }}
                  onClick={() =>
                    setExpandedMeal(expandedMeal === meal.id ? null : meal.id)
                  }
                >
                  <div>
                    <h3>{meal.name}</h3>
                    <p>
                      Type: <strong>{meal.mealType}</strong>
                    </p>
                  </div>
                  <div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        startEditingMeal(meal)
                      }}
                      style={{ marginRight: '8px' }}
                    >
                      Edit
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        deleteMeal(meal.id)
                      }}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              )}

              {expandedMeal === meal.id && (
                <MealDetails mealId={meal.id} />
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function MealDetails({ mealId }) {
  const [foods, setFoods] = useState([])
  const [loading, setLoading] = useState(true)
  const [editingFood, setEditingFood] = useState(null)
  const [editQuantity, setEditQuantity] = useState('')

  useEffect(() => {
    const fetchMealFoods = async () => {
      try {
        const response = await fetch(`${API_BASE}/meal-foods`)
        if (!response.ok) throw new Error('Failed to fetch meal foods')
        const allMealFoods = await response.json()
        
        // Filter for this meal
        const mealFoods = allMealFoods.filter((mf) => mf.mealId === mealId)
        setFoods(mealFoods)
      } catch (error) {
        console.error('fetchMealFoods error', error)
      } finally {
        setLoading(false)
      }
    }

    fetchMealFoods()
  }, [mealId])

  const startEditingFood = (mealFood) => {
    setEditingFood(mealFood.id)
    setEditQuantity(mealFood.quantity.toString())
  }

  const saveEditFood = async (mealFoodId) => {
    if (!editQuantity || parseFloat(editQuantity) <= 0) {
      alert('Quantity must be greater than 0')
      return
    }

    try {
      const response = await fetch(`${API_BASE}/meal-foods/${mealFoodId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          quantity: parseFloat(editQuantity),
        }),
      })

      if (!response.ok) throw new Error('Failed to update food quantity')
      const updatedMealFood = await response.json()
      
      setFoods((prev) =>
        prev.map((f) => (f.id === mealFoodId ? updatedMealFood : f))
      )
      setEditingFood(null)
      setEditQuantity('')
    } catch (error) {
      console.error('saveEditFood error', error)
      alert('Error updating food: ' + error.message)
    }
  }

  const removeFood = async (mealFoodId) => {
    if (!window.confirm('Remove this food from the meal?')) return

    try {
      const response = await fetch(`${API_BASE}/meal-foods/${mealFoodId}`, {
        method: 'DELETE',
      })

      if (!response.ok) throw new Error('Failed to remove food')
      setFoods((prev) => prev.filter((f) => f.id !== mealFoodId))
    } catch (error) {
      console.error('removeFood error', error)
      alert('Error removing food: ' + error.message)
    }
  }

  if (loading) return <p>Loading foods...</p>

  return (
    <div style={{ marginTop: '10px', paddingTop: '10px', borderTop: '1px solid #eee' }}>
      <h4>Foods in this meal:</h4>
      {foods.length === 0 ? (
        <p>No foods added</p>
      ) : (
        <ul>
          {foods.map((mf) => (
            <li key={mf.id} style={{ marginBottom: '8px' }}>
              {editingFood === mf.id ? (
                <div>
                  <input
                    type="number"
                    value={editQuantity}
                    onChange={(e) => setEditQuantity(e.target.value)}
                    step="0.01"
                    style={{ width: '80px', marginRight: '8px' }}
                  />
                  {mf.unit}
                  <button
                    onClick={() => saveEditFood(mf.id)}
                    style={{ marginLeft: '8px' }}
                  >
                    Save
                  </button>
                  <button
                    onClick={() => setEditingFood(null)}
                    style={{ marginLeft: '6px' }}
                  >
                    Cancel
                  </button>
                </div>
              ) : (
                <div>
                  {mf.food?.name || 'Unknown'} -{' '}
                  <strong>
                    {mf.quantity} {mf.unit}
                  </strong>
                  <button
                    onClick={() => startEditingFood(mf)}
                    style={{ marginLeft: '8px' }}
                  >
                    Edit Qty
                  </button>
                  <button
                    onClick={() => removeFood(mf.id)}
                    style={{ marginLeft: '6px' }}
                  >
                    Remove
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

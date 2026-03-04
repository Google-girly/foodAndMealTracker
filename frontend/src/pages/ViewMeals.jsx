import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { supabase } from '../supaBaseClient'

const API_BASE = 'http://localhost:8080'

export default function ViewMeals() {
  const [meals, setMeals] = useState([])
  const [userId, setUserId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [expandedMeal, setExpandedMeal] = useState(null)

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
                  <button onClick={(e) => {
                    e.stopPropagation()
                    deleteMeal(meal.id)
                  }}>
                    Delete
                  </button>
                </div>
              </div>

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

  if (loading) return <p>Loading foods...</p>

  return (
    <div style={{ marginTop: '10px', paddingTop: '10px', borderTop: '1px solid #eee' }}>
      <h4>Foods in this meal:</h4>
      {foods.length === 0 ? (
        <p>No foods added</p>
      ) : (
        <ul>
          {foods.map((mf) => (
            <li key={mf.id}>
              {mf.food?.name || 'Unknown'} - {mf.quantity} {mf.unit}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

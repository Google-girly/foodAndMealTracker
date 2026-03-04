import { useEffect, useState } from 'react'
import { supabase } from '../supaBaseClient'

const API_BASE = 'http://localhost:8080'

export default function MealBuilder() {
  const [user, setUser] = useState(null)
  const [userId, setUserId] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [results, setResults] = useState([])
  const [selected, setSelected] = useState([])
  const [newFoodName, setNewFoodName] = useState('')
  const [mealName, setMealName] = useState('')
  const [mealType, setMealType] = useState('snack')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const fetchUser = async () => {
      const { data } = await supabase.auth.getUser()
      setUser(data.user)
      // For now, we'll use a placeholder ID (1) since the backend uses Long IDs
      // In production, you'd need to sync Supabase users with your backend DB
      setUserId(1)
    }
    fetchUser()
  }, [])

  const searchFoods = async () => {
    if (searchTerm.trim() === '') {
      setResults([])
      return
    }

    setLoading(true)
    try {
      const response = await fetch(`${API_BASE}/foods`)
      if (!response.ok) throw new Error('Failed to fetch foods')
      const allFoods = await response.json()

      // Filter by name match
      const filtered = allFoods.filter((f) =>
        f.name.toLowerCase().includes(searchTerm.toLowerCase())
      )
      setResults(filtered)
    } catch (error) {
      console.error('searchFoods error', error)
      alert('Error searching foods')
    } finally {
      setLoading(false)
    }
  }

  const addToMeal = (food) => {
    if (selected.some((f) => f.id === food.id)) return
    setSelected((prev) => [...prev, { ...food, quantity: 1, unit: 'serving' }])
  }

  const addNewFood = async () => {
    if (!newFoodName.trim() || !userId) return

    setLoading(true)
    try {
      const response = await fetch(`${API_BASE}/foods`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: newFoodName,
          createdById: userId,
          isPublic: true,
        }),
      })

      if (!response.ok) throw new Error('Failed to create food')
      const newFood = await response.json()
      addToMeal(newFood)
      setNewFoodName('')
    } catch (error) {
      console.error('addNewFood error', error)
      alert('Error creating food')
    } finally {
      setLoading(false)
    }
  }

  const updateQuantity = (id, qty) => {
    setSelected((prev) =>
      prev.map((f) => (f.id === id ? { ...f, quantity: qty } : f))
    )
  }

  const removeFood = (id) => {
    setSelected((prev) => prev.filter((f) => f.id !== id))
  }

  const saveMeal = async () => {
    if (!mealName.trim()) {
      alert('Please give the meal a name')
      return
    }
    if (!userId) {
      alert('User not found')
      return
    }

    setLoading(true)
    try {
      // Create meal record
      const mealResponse = await fetch(`${API_BASE}/meals`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          usersId: userId,
          name: mealName,
          mealType: mealType,
        }),
      })

      if (!mealResponse.ok) throw new Error('Failed to create meal')
      const meal = await mealResponse.json()

      // Create meal_foods entries
      const mealFoodPromises = selected.map((f) =>
        fetch(`${API_BASE}/meal-foods`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            mealId: meal.id,
            foodId: f.id,
            quantity: parseFloat(f.quantity),
            unit: f.unit,
          }),
        })
      )

      const results = await Promise.all(mealFoodPromises)
      const allOk = results.every((r) => r.ok)

      if (!allOk) throw new Error('Some meal foods failed to save')

      alert('Meal saved successfully!')
      setSelected([])
      setMealName('')
      setMealType('snack')
    } catch (error) {
      console.error('saveMeal error', error)
      alert('Error saving meal: ' + error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>Build a Meal</h1>

      <div style={{ marginBottom: '20px' }}>
        <label>
          Meal name:{' '}
          <input
            value={mealName}
            onChange={(e) => setMealName(e.target.value)}
            placeholder="e.g. My Lunch"
          />
        </label>
        <label style={{ marginLeft: '20px' }}>
          Type:{' '}
          <select
            value={mealType}
            onChange={(e) => setMealType(e.target.value)}
          >
            <option value="breakfast">Breakfast</option>
            <option value="lunch">Lunch</option>
            <option value="dinner">Dinner</option>
            <option value="snack">Snack</option>
          </select>
        </label>
      </div>

      <div style={{ marginBottom: '10px' }}>
        <input
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search foods"
        />
        <button onClick={searchFoods} style={{ marginLeft: '8px' }}>
          Search
        </button>
      </div>

      <ul>
        {results.map((f) => (
          <li key={f.id} style={{ marginBottom: '4px' }}>
            {f.name} {f.calories != null && `(${f.calories} cal)`}{' '}
            <button onClick={() => addToMeal(f)}>Add</button>
          </li>
        ))}
      </ul>

      <div style={{ marginTop: '20px' }}>
        <input
          value={newFoodName}
          onChange={(e) => setNewFoodName(e.target.value)}
          placeholder="New food name"
        />
        <button onClick={addNewFood} style={{ marginLeft: '8px' }}>
          Create food and add
        </button>
      </div>

      <h2 style={{ marginTop: '30px' }}>Selected Foods</h2>
      <ul>
        {selected.map((f) => (
          <li key={f.id} style={{ marginBottom: '6px' }}>
            {f.name}{' '}
            <input
              type="number"
              value={f.quantity}
              min="0.01"
              step="0.01"
              style={{ width: '60px', marginLeft: '8px' }}
              onChange={(e) => updateQuantity(f.id, e.target.value)}
            />{' '}
            {f.unit}{' '}
            <button onClick={() => removeFood(f.id)}>Remove</button>
          </li>
        ))}
      </ul>

      <button onClick={saveMeal} style={{ marginTop: '20px' }}>
        Save meal
      </button>
    </div>
  )
}

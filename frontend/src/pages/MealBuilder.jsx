import { useEffect, useState } from 'react'
import { supabase } from '../supaBaseClient'

export default function MealBuilder() {
  const [user, setUser] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [results, setResults] = useState([])
  const [selected, setSelected] = useState([])
  const [newFoodName, setNewFoodName] = useState('')
  const [mealName, setMealName] = useState('')
  const [mealType, setMealType] = useState('snack')

  useEffect(() => {
    const fetchUser = async () => {
      const { data } = await supabase.auth.getUser()
      setUser(data.user)
    }
    fetchUser()
  }, [])

  const searchFoods = async () => {
    if (!user || searchTerm.trim() === '') {
      setResults([])
      return
    }

    const filter = `is_public.eq.true,created_by.eq.${user.id}`
    const { data, error } = await supabase
      .from('foods')
      .select('*')
      .ilike('name', `%${searchTerm}%`)
      .or(filter)

    if (error) {
      console.error('searchFoods error', error)
    } else {
      setResults(data)
    }
  }

  const addToMeal = (food) => {
    if (selected.some((f) => f.id === food.id)) return
    setSelected((prev) => [...prev, { ...food, quantity: 1, unit: 'serving' }])
  }

  const addNewFood = async () => {
    if (!newFoodName.trim() || !user) return
    const { data, error } = await supabase
      .from('foods')
      .insert({
        name: newFoodName,
        created_by: user.id,
        is_public: true,
      })
      .select()
      .single()

    if (error) {
      console.error('addNewFood error', error)
    } else {
      addToMeal(data)
      setNewFoodName('')
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
    if (!user) return

    // create meal record
    const { data: meal, error } = await supabase
      .from('meals')
      .insert({
        users_id: user.id,
        name: mealName,
        meal_type: mealType,
      })
      .select()
      .single()

    if (error) {
      console.error('saveMeal error', error)
      return
    }

    const inserts = selected.map((f) => ({
      meal_id: meal.id,
      food_id: f.id,
      quantity: f.quantity,
      unit: f.unit,
    }))
    const { error: err2 } = await supabase.from('meal_foods').insert(inserts)

    if (err2) {
      console.error('meal_foods insert error', err2)
    } else {
      alert('Meal saved successfully!')
      setSelected([])
      setMealName('')
      setMealType('snack')
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

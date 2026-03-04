import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import MealBuilder from './pages/MealBuilder'
import ViewMeals from './pages/ViewMeals'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/meals"
          element={
            <ProtectedRoute>
              <MealBuilder />
            </ProtectedRoute>
          }
        />

        <Route
          path="/view-meals"
          element={
            <ProtectedRoute>
              <ViewMeals />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function PrivateRoute() {
  const { isAuthenticated, loading } = useAuth()

  if (loading) return null

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />
}

export function PublicOnlyRoute() {
  const { isAuthenticated, loading } = useAuth()

  if (loading) return null

  return isAuthenticated ? <Navigate to="/" replace /> : <Outlet />
}

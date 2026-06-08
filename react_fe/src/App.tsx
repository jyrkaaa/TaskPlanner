import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { AlertProvider } from './context/AlertContext'
import { PrivateRoute, PublicOnlyRoute } from './components/RouteGuards'
import { useAuth } from './context/AuthContext'
import HomePage from './pages/HomePage'
import LoginPage from './pages/Login'
import RegisterPage from './pages/Register'
import Navbar from './components/Navbar'

function CatchAll() {
  const { permissions, loading } = useAuth()
  if (loading) return null
  return <Navigate to={permissions !== null ? '/' : '/login'} replace />
}

function App() {
  return (
    <AlertProvider>
      <AuthProvider>
        <BrowserRouter>
          <Navbar>
            <Routes>
              {/* Public-only routes — redirect authenticated users to home */}
              <Route element={<PublicOnlyRoute />}>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
              </Route>

              {/* Protected routes — redirect unauthenticated users to login */}
              <Route element={<PrivateRoute />}>
                <Route path="/" element={<HomePage />} />
              </Route>

              <Route path="*" element={<CatchAll />} />
            </Routes>
          </Navbar>
        </BrowserRouter>
      </AuthProvider>
    </AlertProvider>
  )
}

export default App

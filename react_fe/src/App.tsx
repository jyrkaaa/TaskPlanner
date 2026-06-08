import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Suspense, lazy } from 'react'
import { AuthProvider } from './context/AuthContext'
import { AlertProvider } from './context/AlertContext'
import { PrivateRoute, PublicOnlyRoute } from './components/RouteGuards'
import { useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'

const HomePage = lazy(() => import('./pages/HomePage'))
const LoginPage = lazy(() => import('./pages/Login'))
const RegisterPage = lazy(() => import('./pages/Register'))
const IssuePage = lazy(() => import('./pages/IssuePage'))
const TaskPage = lazy(() => import('./pages/TaskPage'))
const AdminPage = lazy(() => import('./pages/AdminPage'))
const MiscPage = lazy(() => import('./pages/MiscPage'))
const TaskDetailPage = lazy(() => import('./pages/TaskDetailsPage'))
const IssueDetailPage = lazy(() => import('./pages/IssueDetailsPage'))

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
            <Suspense fallback={null}>
            <Routes>
              {/* Public-only routes — redirect authenticated users to home */}
              <Route element={<PublicOnlyRoute />}>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
              </Route>

              {/* Protected routes — redirect unauthenticated users to login */}
              <Route element={<PrivateRoute />}>
                <Route path="/issues" element={<IssuePage />} />
                <Route path="/issues/:tenantId/:id" element={<IssueDetailPage />} />
                <Route path="/tasks" element={<TaskPage />} />
                <Route path="/tasks/:tenantId/:id" element={<TaskDetailPage />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/misc" element={<MiscPage />} />
                <Route path="/" element={<HomePage />} />
              </Route>

              <Route path="*" element={<CatchAll />} />
            </Routes>
            </Suspense>
          </Navbar>
        </BrowserRouter>
      </AuthProvider>
    </AlertProvider>
  )
}

export default App

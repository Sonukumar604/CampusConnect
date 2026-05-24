import { Navigate, Outlet } from 'react-router-dom'
import LoadingState from '../components/LoadingState'
import { useAuth } from '../hooks/useAuth'

export default function ProtectedRoute({ roles }) {
  const { isAuthenticated, authReady, user } = useAuth()

  if (!authReady) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <LoadingState label="Preparing your workspace" />
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (roles && !roles.includes(user?.role)) {
    return <Navigate to="/forbidden" replace />
  }

  return <Outlet />
}

import { Outlet } from 'react-router-dom'
import Sidebar from '../components/Sidebar'

export default function AppLayout() {
  return (
    <div className="min-h-screen bg-ink-950">
      <div className="grid min-h-screen grid-cols-1 lg:grid-cols-[280px_1fr]">
        <div className="hidden lg:block">
          <Sidebar />
        </div>
        <main className="surface-grid min-h-screen px-6 py-10">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

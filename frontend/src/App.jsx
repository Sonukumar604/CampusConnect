import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import AppRoutes from './routes/AppRoutes.jsx'
import { AuthProvider } from './context/AuthContext.jsx'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: '#171823',
              color: '#f7f2ea',
              border: '1px solid rgba(255, 255, 255, 0.12)'
            }
          }}
        />
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App

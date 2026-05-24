import { Routes, Route } from 'react-router-dom'
import PublicLayout from '../layouts/PublicLayout'
import AuthLayout from '../layouts/AuthLayout'
import AppLayout from '../layouts/AppLayout'
import ProtectedRoute from './ProtectedRoute'

import LandingPage from '../pages/LandingPage'
import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage'
import ResetPasswordPage from '../pages/auth/ResetPasswordPage'
import VerifyEmailPage from '../pages/auth/VerifyEmailPage'

import DashboardPage from '../pages/DashboardPage'
import ProfilePage from '../pages/ProfilePage'
import HackathonsPage from '../pages/HackathonsPage'
import HackathonRegistrationPage from '../pages/HackathonRegistrationPage'
import InternshipsPage from '../pages/InternshipsPage'
import InternshipApplicationsPage from '../pages/InternshipApplicationsPage'
import CoursesPage from '../pages/CoursesPage'
import MyActivityPage from '../pages/MyActivityPage'
import EventsPage from '../pages/EventsPage'
import EventRegistrationPage from '../pages/EventRegistrationPage'
import ScholarshipsPage from '../pages/ScholarshipsPage'
import AdminDashboardPage from '../pages/admin/AdminDashboardPage'

import NotFoundPage from '../pages/errors/NotFoundPage'
import ForbiddenPage from '../pages/errors/ForbiddenPage'
import ServerErrorPage from '../pages/errors/ServerErrorPage'

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route index element={<LandingPage />} />
      </Route>

      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        <Route path="/verify-email" element={<VerifyEmailPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/hackathons" element={<HackathonsPage />} />
          <Route path="/hackathons/:id/register" element={<HackathonRegistrationPage />} />
          <Route path="/internships" element={<InternshipsPage />} />
          <Route path="/internships/applications" element={<InternshipApplicationsPage />} />
          <Route path="/courses" element={<CoursesPage />} />
          <Route path="/my-activity" element={<MyActivityPage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/events/:id/register" element={<EventRegistrationPage />} />
          <Route path="/scholarships" element={<ScholarshipsPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={['ADMIN']} />}>
        <Route element={<AppLayout />}>
          <Route path="/admin" element={<AdminDashboardPage />} />
        </Route>
      </Route>

      <Route path="/forbidden" element={<ForbiddenPage />} />
      <Route path="/error" element={<ServerErrorPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import LoginPage from './pages/LoginPage';
import ProfileSetupPage from './pages/ProfileSetupPage';
import LearningPage from './pages/LearningPage';
import GamePage from './pages/GamePage';
import ShopPage from './pages/ShopPage';
import ProfilePage from './pages/ProfilePage';
import AdminPage from './pages/AdminPage';
import AppLayout from './components/AppLayout';

function PrivateRoute({ children, adminOnly = false }) {
  const token = useAuthStore((s) => s.token);
  const user  = useAuthStore((s) => s.user);
  if (!token) return <Navigate to="/login" replace />;
  if (adminOnly && user?.role !== 'ADMIN') return <Navigate to="/learning" replace />;
  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/profile-setup"
          element={
            <PrivateRoute>
              <ProfileSetupPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/"
          element={
            <PrivateRoute>
              <AppLayout />
            </PrivateRoute>
          }
        >
          <Route index element={<Navigate to="/learning" replace />} />
          <Route path="learning" element={<LearningPage />} />
          <Route path="game"     element={<GamePage />} />
          <Route path="shop"     element={<ShopPage />} />
          <Route path="profile"  element={<ProfilePage />} />
          <Route
            path="admin"
            element={
              <PrivateRoute adminOnly>
                <AdminPage />
              </PrivateRoute>
            }
          />
        </Route>

        <Route path="*" element={<Navigate to="/learning" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

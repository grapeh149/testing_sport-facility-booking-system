import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navigation from './components/Navigation';
import Footer from './components/Footer';
import Banner from './components/Banner';
import Login from './components/Login';
import Register from './components/Register';
import FacilityList from './components/FacilityList';
import FacilityDetail from './components/FacilityDetail';
import CourtDetail from './components/CourtDetail';
import CourtSearch from './components/CourtSearch';
import BookingForm from './components/BookingForm';
import MyBookings from './components/MyBookings';
import PaymentReturn from './components/PaymentReturn';
import OwnerDashboard from './components/OwnerDashboard';
import AdminDashboard from './components/AdminDashboard';
import UserProfile from './components/UserProfile';
import EditProfile from './components/EditProfile';
import ChangePassword from './components/ChangePassword';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="d-flex flex-column min-vh-100">
          <Navigation />
          <main className="flex-grow-1">
            <Routes>
              <Route 
                path="/" 
                element={
                  <>
                    <Banner />
                    <FacilityList />
                  </>
                } 
              />
              <Route path="/facility/:id" element={<FacilityDetail />} />
              <Route path="/court-search" element={<CourtSearch />} />
              <Route
                path="/court/:courtId"
                element={<CourtDetail />}
              />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route
                path="/booking/:courtId"
                element={
                  <ProtectedRoute>
                    <BookingForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-bookings"
                element={
                  <ProtectedRoute>
                    <MyBookings />
                  </ProtectedRoute>
                }
              />
              <Route path="/payment/vnpay-return" element={<PaymentReturn />} />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <UserProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile/edit"
                element={
                  <ProtectedRoute>
                    <EditProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile/change-password"
                element={
                  <ProtectedRoute>
                    <ChangePassword />
                  </ProtectedRoute>
                }
              />
              {/* Owner Dashboard */}
              <Route
                path="/owner/dashboard"
                element={
                  <ProtectedRoute requiredRole="OWNER">
                    <OwnerDashboard />
                  </ProtectedRoute>
                }
              />
              {/* Admin Dashboard */}
              <Route
                path="/admin/dashboard"
                element={
                  <ProtectedRoute requiredRole="ADMIN">
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;

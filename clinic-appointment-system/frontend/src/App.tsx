import { Link, Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth';
import Login from './pages/Login';
import Book from './pages/Book';
import Calendar from './pages/Calendar';
import Admin from './pages/Admin';

export default function App() {
  const { user, logout } = useAuth();
  return (
    <>
      <nav className="nav">
        <Link to="/" className="brand">Clinic</Link>
        <div className="spacer" />
        {user?.role === 'PATIENT' && <Link to="/book">Book appointment</Link>}
        {user && <Link to="/">{user.role === 'DOCTOR' ? 'My schedule' : 'Appointments'}</Link>}
        {user?.role === 'ADMIN' && <Link to="/admin">Admin</Link>}
        {user ? <button onClick={logout}>Logout ({user.name})</button> : <Link to="/login">Login</Link>}
      </nav>
      <main className="container">
        <Routes>
          <Route path="/" element={user ? <Calendar /> : <Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/book" element={user?.role === 'PATIENT' ? <Book /> : <Navigate to="/" />} />
          <Route path="/admin" element={user?.role === 'ADMIN' ? <Admin /> : <Navigate to="/" />} />
        </Routes>
      </main>
    </>
  );
}

import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth';

export default function Login() {
  const { login, register } = useAuth();
  const nav = useNavigate();
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      if (mode === 'login') await login(email, password);
      else await register(name, email, password);
      nav('/');
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <form className="form" onSubmit={submit}>
      <h2>{mode === 'login' ? 'Login' : 'Register as patient'}</h2>
      {mode === 'register' && <input placeholder="Full name" value={name} onChange={e => setName(e.target.value)} required />}
      <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required />
      <input type="password" placeholder="Password (min 6)" value={password} onChange={e => setPassword(e.target.value)} required />
      {error && <p className="error">{error}</p>}
      <button type="submit">{mode === 'login' ? 'Login' : 'Register'}</button>
      <a href="#" onClick={e => { e.preventDefault(); setMode(mode === 'login' ? 'register' : 'login'); }}>
        {mode === 'login' ? 'New patient? Register' : 'Have an account? Login'}
      </a>
    </form>
  );
}

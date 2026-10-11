import { createContext, useContext, useState, ReactNode } from 'react';
import { api } from './api';

interface User { email: string; name: string; role: string }
interface AuthCtx {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}
const Ctx = createContext<AuthCtx>(null!);
export const useAuth = () => useContext(Ctx);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  });

  const save = (r: { token: string; email: string; name: string; role: string }) => {
    localStorage.setItem('token', r.token);
    const u = { email: r.email, name: r.name, role: r.role };
    localStorage.setItem('user', JSON.stringify(u));
    setUser(u);
  };

  const login = async (email: string, password: string) =>
    save(await api('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }));
  const register = async (name: string, email: string, password: string) =>
    save(await api('/api/auth/register', { method: 'POST', body: JSON.stringify({ name, email, password }) }));
  const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); setUser(null); };

  return <Ctx.Provider value={{ user, login, register, logout }}>{children}</Ctx.Provider>;
}

const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export interface Doctor { id: number; name: string; specialty: string }
export interface Slot { start: string; end: string; available: boolean }
export interface Appointment {
  id: number; doctorId: number; doctorName: string; specialty: string; patientName: string;
  patientEmail: string; start: string; end: string; status: string; reason?: string;
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const res = await fetch(BASE + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers ?? {}),
    },
  });
  if (!res.ok) {
    let msg = res.statusText;
    try { msg = (await res.json()).message ?? msg; } catch { /* ignore */ }
    throw new Error(msg || `Request failed (${res.status})`);
  }
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

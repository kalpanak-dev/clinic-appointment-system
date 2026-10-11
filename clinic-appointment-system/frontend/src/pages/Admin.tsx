import { useEffect, useState } from 'react';
import { api, Doctor } from '../api';

export default function Admin() {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [form, setForm] = useState({ name: '', email: '', password: '', specialty: '' });
  const [error, setError] = useState('');
  const load = () => { api<Doctor[]>('/api/doctors').then(setDoctors); };
  useEffect(load, []);

  const add = async () => {
    setError('');
    try {
      await api('/api/admin/doctors', { method: 'POST', body: JSON.stringify(form) });
      setForm({ name: '', email: '', password: '', specialty: '' });
      load();
    } catch (e) { setError((e as Error).message); }
  };
  const f = (k: keyof typeof form) => ({ value: form[k], onChange: (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value }) });

  return (
    <>
      <h2>Manage doctors</h2>
      <div className="filters">
        <input placeholder="Name" {...f('name')} />
        <input placeholder="Specialty" {...f('specialty')} />
        <input type="email" placeholder="Email" {...f('email')} />
        <input type="password" placeholder="Password (min 6)" {...f('password')} />
        <button onClick={add}>Add doctor</button>
      </div>
      {error && <p className="error">{error}</p>}
      <table>
        <thead><tr><th>Name</th><th>Specialty</th></tr></thead>
        <tbody>{doctors.map(d => <tr key={d.id}><td>{d.name}</td><td>{d.specialty}</td></tr>)}</tbody>
      </table>
    </>
  );
}

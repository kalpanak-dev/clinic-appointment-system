import { useEffect, useState } from 'react';
import { api, Appointment } from '../api';
import { useAuth } from '../auth';

function startOfWeek(d: Date) {
  const x = new Date(d);
  const day = (x.getDay() + 6) % 7; // Monday = 0
  x.setDate(x.getDate() - day);
  x.setHours(0, 0, 0, 0);
  return x;
}
const key = (d: Date) => d.toLocaleDateString('en-CA');

export default function Calendar() {
  const { user } = useAuth();
  const [items, setItems] = useState<Appointment[]>([]);
  const [weekStart, setWeekStart] = useState(startOfWeek(new Date()));
  const [error, setError] = useState('');

  const load = () => { api<Appointment[]>('/api/appointments/mine').then(setItems).catch(e => setError(e.message)); };
  useEffect(load, []);

  const act = async (id: number, action: 'cancel' | 'complete') => {
    try { await api(`/api/appointments/${id}/${action}`, { method: 'PATCH' }); load(); }
    catch (e) { setError((e as Error).message); }
  };

  const days = Array.from({ length: 7 }, (_, i) => { const d = new Date(weekStart); d.setDate(d.getDate() + i); return d; });
  const shift = (n: number) => { const d = new Date(weekStart); d.setDate(d.getDate() + n * 7); setWeekStart(d); };

  return (
    <>
      <div className="filters">
        <button onClick={() => shift(-1)}>Previous week</button>
        <button onClick={() => setWeekStart(startOfWeek(new Date()))}>This week</button>
        <button onClick={() => shift(1)}>Next week</button>
        <b>Week of {weekStart.toLocaleDateString()}</b>
      </div>
      {error && <p className="error">{error}</p>}
      <div className="week">
        {days.map(d => (
          <div className="day" key={key(d)}>
            <h4>{d.toLocaleDateString([], { weekday: 'short', day: 'numeric', month: 'short' })}</h4>
            {items.filter(a => a.start.startsWith(key(d))).map(a => (
              <div className={`appt ${a.status}`} key={a.id}>
                <b>{new Date(a.start).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</b>
                <div>{user?.role === 'PATIENT' ? `Dr. ${a.doctorName}` : a.patientName}</div>
                <small>{a.status}{a.reason ? ` · ${a.reason}` : ''}</small>
                {a.status === 'BOOKED' && (
                  <div>
                    <button onClick={() => act(a.id, 'cancel')}>Cancel</button>
                    {user?.role !== 'PATIENT' && <button onClick={() => act(a.id, 'complete')}>Done</button>}
                  </div>
                )}
              </div>
            ))}
          </div>
        ))}
      </div>
    </>
  );
}

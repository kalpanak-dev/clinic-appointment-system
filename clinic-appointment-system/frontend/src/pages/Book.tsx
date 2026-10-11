import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, Doctor, Slot } from '../api';

const today = () => new Date().toLocaleDateString('en-CA'); // YYYY-MM-DD

export default function Book() {
  const nav = useNavigate();
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [doctorId, setDoctorId] = useState<number | ''>('');
  const [date, setDate] = useState(today());
  const [slots, setSlots] = useState<Slot[]>([]);
  const [reason, setReason] = useState('');
  const [error, setError] = useState('');

  useEffect(() => { api<Doctor[]>('/api/doctors').then(setDoctors); }, []);
  const loadSlots = () => {
    if (doctorId === '') return;
    api<Slot[]>(`/api/doctors/${doctorId}/slots?date=${date}`).then(setSlots).catch(e => setError(e.message));
  };
  useEffect(loadSlots, [doctorId, date]);

  const book = async (slot: Slot) => {
    setError('');
    try {
      await api('/api/appointments', { method: 'POST', body: JSON.stringify({ doctorId, start: slot.start, reason }) });
      nav('/');
    } catch (e) {
      setError((e as Error).message);
      loadSlots(); // someone else may have taken the slot
    }
  };

  return (
    <>
      <h2>Book an appointment</h2>
      <div className="filters">
        <select value={doctorId} onChange={e => setDoctorId(e.target.value ? Number(e.target.value) : '')}>
          <option value="">Choose a doctor</option>
          {doctors.map(d => <option key={d.id} value={d.id}>{d.name} ({d.specialty})</option>)}
        </select>
        <input type="date" min={today()} value={date} onChange={e => setDate(e.target.value)} />
        <input placeholder="Reason for visit (optional)" value={reason} onChange={e => setReason(e.target.value)} />
      </div>
      {error && <p className="error">{error}</p>}
      {doctorId !== '' && slots.length === 0 && <p>No slots on this day (the clinic is closed at weekends).</p>}
      <div className="slots">
        {slots.map(s => (
          <button key={s.start} disabled={!s.available} onClick={() => book(s)}>
            {new Date(s.start).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          </button>
        ))}
      </div>
    </>
  );
}

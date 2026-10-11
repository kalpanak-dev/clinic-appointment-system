# Clinic Appointment System

Booking platform for patients, doctors and admins: slot scheduling, double-booking prevention,
email reminders and a weekly calendar view, secured with JWT and role-based access.

**Tech:** Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA, MySQL, JavaMail (MailHog for local testing),
React 18, TypeScript, Vite, Docker, GitHub Actions.

## Features
- Roles: `PATIENT` (self-register), `DOCTOR` and `ADMIN` (created by the admin / seeded)
- Patients pick a doctor and a date, see free 30-minute slots (Mon-Fri, 09:00-17:00) and book one
- **Double-booking prevention:** a unique `slotKey` column (`doctorId|startTime`, cleared on cancel) makes it impossible
  to book the same slot twice, even for simultaneous requests. The API returns `409 Conflict` to the second person
- Weekly calendar view for patients (own appointments), doctors (schedule) and admins (everyone); cancel or mark as done
- Email reminder sent once, when an appointment is less than 24 hours away (scheduled job, runs every minute)
- Admin can add doctors

## Run locally
Prerequisites: Java 21, Maven, Node 20+, Docker.

```bash
# 1. MySQL + MailHog (fake mail server, inbox at http://localhost:8025)
docker compose up -d mysql mailhog

# 2. backend (http://localhost:8080)
cd backend
mvn spring-boot:run

# 3. frontend (http://localhost:5173)
cd ../frontend
cp .env.example .env
npm install
npm run dev
```

Demo accounts (created on first start; change before any real deployment):

| Role | Email | Password |
|---|---|---|
| Admin | admin@clinic.com | admin123 |
| Doctor | dr.silva@clinic.com | doctor123 |
| Doctor | dr.perera@clinic.com | doctor123 |

Register a new patient from the login page. To see a reminder email, book a slot for tomorrow morning and
open MailHog within a minute.

## Tests
`cd backend && mvn test` runs unit tests for the slot generation and validation logic.
A good next step is an integration test that books the same slot from two threads and checks that only one succeeds.

## API overview
| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | public |
| GET | /api/doctors, /api/doctors/{id}/slots?date=YYYY-MM-DD | logged in |
| POST | /api/appointments | patient |
| GET | /api/appointments/mine | logged in |
| PATCH | /api/appointments/{id}/cancel | owner, doctor or admin |
| PATCH | /api/appointments/{id}/complete | doctor or admin |
| POST | /api/admin/doctors | admin |

## Ideas to extend
Doctor working hours per day, SMS reminders, appointment notes, Flyway migrations, Testcontainers integration tests.

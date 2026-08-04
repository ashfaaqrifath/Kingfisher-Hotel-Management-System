# Kingfisher Hotel Management System

A hotel operations dashboard for managing rooms, bookings, guests, employees, and inventory with a React frontend and Supabase backend.

## Tech stack

- Frontend: React + Vite + Tailwind CSS
- Backend/auth: Supabase (PostgreSQL + Auth)
- Reporting: PDF export support

## Project structure

- `docs/schema.sql` — Supabase database schema and seed data
- `frontend/` — React application
- `frontend/src/pages/` — Dashboard, bookings, rooms, guests, employees, inventory, reports, login
- `frontend/src/components/` — shared UI components
- `frontend/src/lib/` — Supabase client and helpers

## Quick start

### 1) Set up the database

1. Create a Supabase project.
2. Open the Supabase SQL Editor.
3. Run the contents of `docs/schema.sql`.
4. This will create the tables, policies, seed data, and booking-to-room sync trigger.

### 2) Create your first login user

1. Go to Supabase Auth → Users.
2. Add a user with email/password.
3. In SQL Editor, update that user profile role to `owner` or `admin`.

Example:

```sql
UPDATE profiles
SET role = 'owner'
WHERE id = '<auth-user-id>';
```

### 3) Configure the frontend environment

Inside `frontend/`, create a `.env` file:

```env
VITE_SUPABASE_URL=https://your-project-ref.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key-here
```

You can get these values from Supabase Dashboard → Project Settings → API.

### 4) Install and run

```bash
cd frontend
npm install
npm run dev
```

Then open:

```text
http://localhost:5173
```

## Main features

- Authentication with role-based access
- Dashboard with occupancy and booking insights
- Guest, room, employee, booking, and inventory management
- Booking workflow with check-in, check-out, and cancellation
- Inventory low-stock monitoring
- Activity log for admin visibility
- Reports with export options

## Useful commands

```bash
cd frontend
npm run dev
npm run build
npm run test
```

## Notes

- The app expects the database schema from `docs/schema.sql` to be loaded first.
- The login user must have a matching `profiles` row with a valid role.
- Role defaults are handled by the schema, but the first owner/admin account should be promoted manually in SQL.

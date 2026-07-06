# Kingfisher Hotel Management Dashboard

Web-based analytics dashboard for Kingfisher Beach Resort, Yala.
React + Tailwind CSS on the frontend, Supabase (Postgres + Auth) as the backend.

## Folder structure

```
kingfisher-hms/
├── supabase/
│   └── schema.sql          ← run this in Supabase SQL editor first
├── frontend/
│   ├── src/
│   │   ├── pages/          Dashboard, Guests, Employees, Rooms, Bookings, Inventory, Reports, ActivityLog, Login
│   │   ├── components/     Sidebar, Layout, Modal, StatCard, Toolbar, ProtectedRoute
│   │   ├── context/        AuthContext (session + role)
│   │   ├── lib/            supabaseClient, activityLog helper
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── tailwind.config.js
│   ├── vite.config.js
│   └── package.json
└── README.md
```

## 1. Set up Supabase

1. Create a free project at https://supabase.com
2. Open **SQL Editor** → paste the contents of `supabase/schema.sql` → Run.
   This creates all tables, RLS policies, triggers, and seeds sample rooms/inventory.
3. Go to **Authentication → Users** → add your first user (email + password).
4. In **SQL Editor**, promote that user to admin:
   ```sql
   update profiles set role = 'admin' where id = '<paste-user-uuid-here>';
   ```
5. Add more users the same way for your other 4 team members (they default to `staff`).

## 2. Run the frontend

```bash
cd frontend
npm install
cp .env.example .env
```

Edit `.env` and paste your Supabase project URL + anon key
(Project Settings → API in the Supabase dashboard):

```
VITE_SUPABASE_URL=https://your-project-ref.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key-here
```

Then:

```bash
npm run dev
```

Open http://localhost:5173 and log in with the user you created.

## Features implemented

- **Auth**: Supabase Auth login, role-based access (Admin / Staff)
- **Dashboard**: KPI cards, 7-day booking trend chart, room status donut, inventory bar chart, live room key-board
- **Guests**: full CRUD, search
- **Employees**: full CRUD, search (admin only)
- **Rooms**: card-grid view, live status colors, full CRUD
- **Bookings**: create booking (auto-calculates total from nights × room price), check-in / check-out / cancel workflow, auto-syncs room status via a DB trigger
- **Inventory**: full CRUD, low-stock flagging and filter
- **Reports**: date-range filtered booking report + inventory report, CSV and PDF export
- **Activity Log**: audit trail of every create/update/delete action (admin only)

## Design notes

Flat, no gradients — navy (`#0F2B46`) + teal (`#0E7C7B`) on a warm sand background, `Space Grotesk` for headings, `Inter` for body text, `IBM Plex Mono` for numbers/data. The room grid on the Dashboard and Rooms page mimics a physical hotel key-board, color-coded by status.

## Notes for your report

- Database: Supabase (PostgreSQL) — matches the "Supabase" tech choice in your proposal.
- Room status updates automatically via a Postgres trigger when a booking is checked in/out or cancelled — no manual sync needed.
- Row Level Security is enabled on every table; only authenticated staff can read/write operational data, and only admins can see the Activity Log.

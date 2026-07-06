-- ============================================================
-- KINGFISHER HOTEL MANAGEMENT SYSTEM — SUPABASE SCHEMA
-- Run this in the Supabase SQL editor (Project > SQL Editor)
-- ============================================================

create extension if not exists "pgcrypto";

-- ------------------------------------------------------------
-- 1. PROFILES (extends Supabase auth.users with role info)
-- ------------------------------------------------------------
create table if not exists profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  full_name text not null,
  role text not null check (role in ('admin','staff')) default 'staff',
  created_at timestamptz default now()
);

-- Auto-create a profile row whenever a new auth user signs up
create or replace function handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, full_name, role)
  values (new.id, coalesce(new.raw_user_meta_data->>'full_name', new.email), 'staff');
  return new;
end;
$$ language plpgsql security definer;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure handle_new_user();

-- ------------------------------------------------------------
-- 2. GUESTS
-- ------------------------------------------------------------
create table if not exists guests (
  id uuid primary key default gen_random_uuid(),
  full_name text not null,
  email text,
  phone text,
  nic text,
  address text,
  gender text check (gender in ('Male','Female','Other')),
  created_at timestamptz default now()
);

-- ------------------------------------------------------------
-- 3. EMPLOYEES
-- ------------------------------------------------------------
create table if not exists employees (
  id uuid primary key default gen_random_uuid(),
  full_name text not null,
  email text,
  phone text,
  job_role text not null check (job_role in ('Reception','Housekeeping','Chef','Safari Guide','Manager','Maintenance')),
  salary numeric(10,2) default 0,
  hire_date date default current_date,
  status text not null check (status in ('Active','On Leave','Terminated')) default 'Active',
  created_at timestamptz default now()
);

-- ------------------------------------------------------------
-- 4. ROOMS
-- ------------------------------------------------------------
create table if not exists rooms (
  id uuid primary key default gen_random_uuid(),
  room_number text not null unique,
  room_type text not null check (room_type in ('Standard','Deluxe','Suite','Beach Villa')),
  price_per_night numeric(10,2) not null default 0,
  status text not null check (status in ('Available','Occupied','Maintenance')) default 'Available',
  created_at timestamptz default now()
);

-- ------------------------------------------------------------
-- 5. BOOKINGS
-- ------------------------------------------------------------
create table if not exists bookings (
  id uuid primary key default gen_random_uuid(),
  guest_id uuid references guests(id) on delete set null,
  room_id uuid references rooms(id) on delete set null,
  check_in date not null,
  check_out date not null,
  status text not null check (status in ('Booked','Checked In','Checked Out','Cancelled')) default 'Booked',
  total_amount numeric(10,2) default 0,
  created_at timestamptz default now(),
  constraint valid_dates check (check_out > check_in)
);

-- Keep room status in sync with booking status
create or replace function sync_room_status()
returns trigger as $$
begin
  if new.status = 'Checked In' then
    update rooms set status = 'Occupied' where id = new.room_id;
  elsif new.status in ('Checked Out','Cancelled') then
    update rooms set status = 'Available' where id = new.room_id;
  end if;
  return new;
end;
$$ language plpgsql security definer;

drop trigger if exists on_booking_status_change on bookings;
create trigger on_booking_status_change
  after insert or update of status on bookings
  for each row execute procedure sync_room_status();

-- ------------------------------------------------------------
-- 6. INVENTORY
-- ------------------------------------------------------------
create table if not exists inventory_items (
  id uuid primary key default gen_random_uuid(),
  item_name text not null,
  category text not null check (category in ('Linen','Toiletries','Food & Beverage','Kitchen','Maintenance','Office')),
  quantity int not null default 0,
  unit text not null default 'pcs',
  low_stock_threshold int not null default 10,
  unit_price numeric(10,2) default 0,
  updated_at timestamptz default now()
);

-- ------------------------------------------------------------
-- 7. ACTIVITY LOG (admin-only audit trail)
-- ------------------------------------------------------------
create table if not exists activity_logs (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references profiles(id) on delete set null,
  action text not null,
  details text,
  created_at timestamptz default now()
);

-- ============================================================
-- ROW LEVEL SECURITY
-- ============================================================
alter table profiles enable row level security;
alter table guests enable row level security;
alter table employees enable row level security;
alter table rooms enable row level security;
alter table bookings enable row level security;
alter table inventory_items enable row level security;
alter table activity_logs enable row level security;

-- Helper: is the current user an admin?
create or replace function is_admin()
returns boolean as $$
  select exists (
    select 1 from profiles where id = auth.uid() and role = 'admin'
  );
$$ language sql security definer;

-- PROFILES: everyone can read all profiles (needed for name lookups); only admin can edit roles
create policy "profiles_select" on profiles for select using (true);
create policy "profiles_update_self_or_admin" on profiles for update using (id = auth.uid() or is_admin());

-- GUESTS / EMPLOYEES / ROOMS / BOOKINGS / INVENTORY:
-- any authenticated staff can read & write (day-to-day ops), matching the brief's
-- "Staff manage bookings/guests" and "Admin manages everything" model.
create policy "guests_all_authenticated" on guests for all using (auth.role() = 'authenticated');
create policy "employees_all_authenticated" on employees for all using (auth.role() = 'authenticated');
create policy "rooms_all_authenticated" on rooms for all using (auth.role() = 'authenticated');
create policy "bookings_all_authenticated" on bookings for all using (auth.role() = 'authenticated');
create policy "inventory_all_authenticated" on inventory_items for all using (auth.role() = 'authenticated');

-- ACTIVITY LOG: admin only
create policy "activity_log_select_admin" on activity_logs for select using (is_admin());
create policy "activity_log_insert_authenticated" on activity_logs for insert with check (auth.role() = 'authenticated');

-- ============================================================
-- SEED DATA (sample rooms so the dashboard isn't empty on first run)
-- ============================================================
insert into rooms (room_number, room_type, price_per_night, status) values
  ('101','Standard',12000,'Available'),
  ('102','Standard',12000,'Available'),
  ('103','Deluxe',18000,'Available'),
  ('104','Deluxe',18000,'Occupied'),
  ('201','Suite',28000,'Available'),
  ('202','Suite',28000,'Maintenance'),
  ('301','Beach Villa',45000,'Available'),
  ('302','Beach Villa',45000,'Available')
on conflict (room_number) do nothing;

insert into inventory_items (item_name, category, quantity, unit, low_stock_threshold, unit_price) values
  ('Bath Towels','Linen',120,'pcs',30,850),
  ('Bedsheets (King)','Linen',60,'pcs',20,2200),
  ('Shampoo Bottles','Toiletries',8,'pcs',25,180),
  ('Rice (Basmati)','Food & Beverage',40,'kg',15,320),
  ('Cooking Gas Cylinders','Kitchen',3,'cylinders',4,4500),
  ('Printer Paper','Office',5,'reams',5,950)
on conflict do nothing;

-- Note: after running this, create your first admin user via Supabase Auth,
-- then run:  update profiles set role = 'admin' where id = '<that user''s uuid>';

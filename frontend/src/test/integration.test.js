import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, staffClient, makeCleanup, TEST_PREFIX, ADMIN_EMAIL, ADMIN_PASSWORD } from './helpers'
import { logActivity } from '../lib/activityLog'
import { supabase as singletonSupabase } from '../lib/supabaseClient'

// TC-S9-01..05

describe('Cross-module integration', () => {
  let client
  const cleanup = makeCleanup()

  beforeAll(async () => {
    client = await adminClient()
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-S9-01: a booking created with status Booked does not mark the room Occupied', async () => {
    const { data: room } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}I1`, room_type: 'Standard', price_per_night: 10000, status: 'Available' })
      .select()
      .single()
    cleanup.track('rooms', room.id)

    const { data: guest } = await client.from('guests').insert({ full_name: `${TEST_PREFIX}Integration Guest 1` }).select().single()
    cleanup.track('guests', guest.id)

    const { data: booking, error } = await client
      .from('bookings')
      .insert({ guest_id: guest.id, room_id: room.id, check_in: '2026-10-01', check_out: '2026-10-03', status: 'Booked' })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('bookings', booking.id)

    const { data: refreshedRoom } = await client.from('rooms').select('status').eq('id', room.id).single()
    // Expected (per spec): status Booked sets the room to Occupied.
    // Actual: the sync_room_status() DB trigger only flips rooms to
    // Occupied on status = 'Checked In'; 'Booked' leaves the room untouched.
    expect(refreshedRoom.status).toBe('Occupied')
  })

  it('TC-S9-02: a booking cannot be created without a valid guest ID', async () => {
    const { data: room } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}I2`, room_type: 'Standard', price_per_night: 10000 })
      .select()
      .single()
    cleanup.track('rooms', room.id)

    const { data, error } = await client
      .from('bookings')
      .insert({ guest_id: null, room_id: room.id, check_in: '2026-10-05', check_out: '2026-10-06' })
      .select()
      .single()
    // Expected: the DB rejects a booking with no guest attached.
    // Actual: bookings.guest_id is nullable (`references guests(id) on
    // delete set null`, no NOT NULL constraint), so this insert succeeds.
    if (!error) cleanup.track('bookings', data.id)
    expect(error).toBeTruthy()
  })

  it('TC-S9-03: an inventory mutation followed by logActivity produces an activity_logs row', async () => {
    const { data: item } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Activity Item`, category: 'Office', quantity: 5 })
      .select()
      .single()
    cleanup.track('inventory_items', item.id)

    // Mirrors what Inventory.jsx's handleSubmit actually does: insert, then
    // call logActivity(). logActivity() always uses the app's singleton
    // supabase client (not whatever client the caller used), so that
    // singleton has to be authenticated for this to behave like the real app.
    await singletonSupabase.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
    await logActivity('Added inventory item', item.item_name)
    await singletonSupabase.auth.signOut()

    const { data: logs, error } = await client
      .from('activity_logs')
      .select('*')
      .eq('details', item.item_name)
      .order('created_at', { ascending: false })
      .limit(1)
    expect(error).toBeNull()
    expect(logs.length).toBe(1)
    expect(logs[0].action).toBe('Added inventory item')
    // activity_logs has no DELETE policy in schema.sql, so this row is not
    // cleaned up by cleanup.run() — it remains as a legitimate audit entry.
  })

  it('TC-S9-04: values read back from Supabase match what was written (guests, rooms, employees, inventory)', async () => {
    const { data: guest } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Consistency Guest`, phone: '0712223334' })
      .select()
      .single()
    cleanup.track('guests', guest.id)

    const { data: room } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}I4`, room_type: 'Suite', price_per_night: 27500 })
      .select()
      .single()
    cleanup.track('rooms', room.id)

    const { data: employee } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Consistency Employee`, job_role: 'Chef', salary: 72000 })
      .select()
      .single()
    cleanup.track('employees', employee.id)

    const { data: item } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Consistency Item`, category: 'Kitchen', quantity: 8 })
      .select()
      .single()
    cleanup.track('inventory_items', item.id)

    const [refetchedGuest, refetchedRoom, refetchedEmployee, refetchedItem] = await Promise.all([
      client.from('guests').select().eq('id', guest.id).single(),
      client.from('rooms').select().eq('id', room.id).single(),
      client.from('employees').select().eq('id', employee.id).single(),
      client.from('inventory_items').select().eq('id', item.id).single(),
    ])

    expect(refetchedGuest.data.phone).toBe('0712223334')
    expect(Number(refetchedRoom.data.price_per_night)).toBe(27500)
    expect(Number(refetchedEmployee.data.salary)).toBe(72000)
    expect(refetchedItem.data.quantity).toBe(8)
  })

  it('TC-S9-05: RLS blocks staff from writing to profiles, but not from writing to employees', async () => {
    const staff = await staffClient()

    const { data: profileWrite, error: profileError } = await staff
      .from('profiles')
      .update({ full_name: 'Hijacked Name' })
      .eq('email', 'itsthejana@gmail.com')
      .select()
    expect(profileError || (profileWrite && profileWrite.length === 0)).toBeTruthy()

    const { data: empWrite, error: empError } = await staff
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Staff-Written Employee`, job_role: 'Reception' })
      .select()
      .single()
    // Expected: employees is an admin-only table, so this write should be blocked.
    // Actual: schema.sql's "employees_all_authenticated" RLS policy grants
    // ALL (select/insert/update/delete) to any authenticated user, staff
    // included — only the frontend route guard (adminOnly on /employees)
    // keeps staff out of the UI, not the database itself.
    if (!empError) cleanup.track('employees', empWrite.id)
    expect(empError).toBeTruthy()
  })
})

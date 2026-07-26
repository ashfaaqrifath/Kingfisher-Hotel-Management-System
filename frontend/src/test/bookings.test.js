import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, makeCleanup, TEST_PREFIX } from './helpers'

// TC-BOOK-01..07 / TC-S5-01..07

describe('Bookings module', () => {
  let client
  let guestId
  let roomIdCounter = 0
  const cleanup = makeCleanup()

  async function makeRoom(status = 'Available') {
    roomIdCounter += 1
    const { data } = await client
      .from('rooms')
      .insert({
        room_number: `${TEST_PREFIX}B${roomIdCounter}`,
        room_type: 'Standard',
        price_per_night: 10000,
        status,
      })
      .select()
      .single()
    cleanup.track('rooms', data.id)
    return data
  }

  beforeAll(async () => {
    client = await adminClient()
    const { data: guest } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Booking Guest` })
      .select()
      .single()
    guestId = guest.id
    cleanup.track('guests', guestId)
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-BOOK-01 / TC-S5-01: creates a booking for a valid guest and room', async () => {
    const room = await makeRoom()
    const { data, error } = await client
      .from('bookings')
      .insert({
        guest_id: guestId,
        room_id: room.id,
        check_in: '2026-08-01',
        check_out: '2026-08-05',
        total_amount: 40000,
      })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('bookings', data.id)
    expect(data.guest_id).toBe(guestId)
  })

  it('TC-BOOK-02 / TC-S5-02: check_out must be after check_in', async () => {
    const room = await makeRoom()
    const { data, error } = await client
      .from('bookings')
      .insert({
        guest_id: guestId,
        room_id: room.id,
        check_in: '2026-08-10',
        check_out: '2026-08-05',
      })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-BOOK-03 / TC-S5-03: a new booking defaults to Booked status', async () => {
    const room = await makeRoom()
    const { data, error } = await client
      .from('bookings')
      .insert({ guest_id: guestId, room_id: room.id, check_in: '2026-09-01', check_out: '2026-09-03' })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('bookings', data.id)
    expect(data.status).toBe('Booked')
  })

  it('TC-BOOK-04 / TC-S5-04: setting a booking to Checked In marks the room Occupied', async () => {
    const room = await makeRoom()
    const { data: booking } = await client
      .from('bookings')
      .insert({ guest_id: guestId, room_id: room.id, check_in: '2026-09-05', check_out: '2026-09-07' })
      .select()
      .single()
    cleanup.track('bookings', booking.id)

    const { error } = await client.from('bookings').update({ status: 'Checked In' }).eq('id', booking.id)
    expect(error).toBeNull()

    const { data: refreshedRoom } = await client.from('rooms').select('status').eq('id', room.id).single()
    expect(refreshedRoom.status).toBe('Occupied')
  })

  it('TC-BOOK-05 / TC-S5-05: setting a booking to Checked Out frees the room back to Available', async () => {
    const room = await makeRoom()
    const { data: booking } = await client
      .from('bookings')
      .insert({ guest_id: guestId, room_id: room.id, check_in: '2026-09-10', check_out: '2026-09-12', status: 'Checked In' })
      .select()
      .single()
    cleanup.track('bookings', booking.id)

    await client.from('bookings').update({ status: 'Checked Out' }).eq('id', booking.id)

    const { data: refreshedRoom } = await client.from('rooms').select('status').eq('id', room.id).single()
    expect(refreshedRoom.status).toBe('Available')
  })

  it('TC-BOOK-06 / TC-S5-06: cancelling a booking frees the room back to Available', async () => {
    const room = await makeRoom()
    const { data: booking } = await client
      .from('bookings')
      .insert({ guest_id: guestId, room_id: room.id, check_in: '2026-09-15', check_out: '2026-09-16', status: 'Checked In' })
      .select()
      .single()
    cleanup.track('bookings', booking.id)

    const { data: cancelled, error } = await client
      .from('bookings')
      .update({ status: 'Cancelled' })
      .eq('id', booking.id)
      .select()
      .single()
    expect(error).toBeNull()
    expect(cancelled.status).toBe('Cancelled')

    const { data: refreshedRoom } = await client.from('rooms').select('status').eq('id', room.id).single()
    expect(refreshedRoom.status).toBe('Available')
  })

  it('TC-BOOK-07 / TC-S5-07: booking status is restricted to the allowed enum values', async () => {
    const room = await makeRoom()
    const { data, error } = await client
      .from('bookings')
      .insert({
        guest_id: guestId,
        room_id: room.id,
        check_in: '2026-09-20',
        check_out: '2026-09-21',
        status: 'Pending Review',
      })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })
})

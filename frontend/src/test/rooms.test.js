import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, makeCleanup, TEST_PREFIX } from './helpers'

// TC-ROOM-01..06 / TC-S6-01..06

describe('Rooms module', () => {
  let client
  const cleanup = makeCleanup()
  const roomNumber = `${TEST_PREFIX}901`

  beforeAll(async () => {
    client = await adminClient()
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-ROOM-01 / TC-S6-01: creates a room with a unique room number', async () => {
    const { data, error } = await client
      .from('rooms')
      .insert({ room_number: roomNumber, room_type: 'Deluxe', price_per_night: 20000, status: 'Available' })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('rooms', data.id)
    expect(data.room_number).toBe(roomNumber)
  })

  it('TC-ROOM-02 / TC-S6-02: room_number must be unique', async () => {
    const { data, error } = await client
      .from('rooms')
      .insert({ room_number: roomNumber, room_type: 'Suite', price_per_night: 30000 })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-ROOM-03 / TC-S6-03: room_type is restricted to the allowed enum', async () => {
    const { data, error } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}902`, room_type: 'Treehouse', price_per_night: 15000 })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-ROOM-04 / TC-S6-04: updates a room\'s price and status', async () => {
    const { data: created } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}903`, room_type: 'Standard', price_per_night: 10000 })
      .select()
      .single()
    cleanup.track('rooms', created.id)

    const { data: updated, error } = await client
      .from('rooms')
      .update({ price_per_night: 12500, status: 'Maintenance' })
      .eq('id', created.id)
      .select()
      .single()
    expect(error).toBeNull()
    expect(Number(updated.price_per_night)).toBe(12500)
    expect(updated.status).toBe('Maintenance')
  })

  it('TC-ROOM-05 / TC-S6-05: deletes a room', async () => {
    const { data: created } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}904`, room_type: 'Standard', price_per_night: 10000 })
      .select()
      .single()

    const { error: deleteError } = await client.from('rooms').delete().eq('id', created.id)
    expect(deleteError).toBeNull()

    const { data: refetch } = await client.from('rooms').select().eq('id', created.id).maybeSingle()
    expect(refetch).toBeNull()
  })

  it('TC-ROOM-06 / TC-S6-06: room search matches by room number or room type substring', async () => {
    const { data: created } = await client
      .from('rooms')
      .insert({ room_number: `${TEST_PREFIX}905`, room_type: 'Beach Villa', price_per_night: 40000 })
      .select()
      .single()
    cleanup.track('rooms', created.id)

    const { data: allRooms } = await client.from('rooms').select()
    const term = 'beach villa'
    const bySearch = allRooms.filter(
      (r) =>
        String(r.room_number).toLowerCase().includes(term) ||
        String(r.room_type).toLowerCase().includes(term)
    )
    expect(bySearch.some((r) => r.id === created.id)).toBe(true)
  })
})

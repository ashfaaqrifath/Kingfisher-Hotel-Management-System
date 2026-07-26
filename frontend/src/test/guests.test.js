import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, makeCleanup, TEST_PREFIX } from './helpers'

// TC-GUEST-01..07 / TC-S3-01..07

describe('Guests module', () => {
  let client
  const cleanup = makeCleanup()

  beforeAll(async () => {
    client = await adminClient()
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-GUEST-01 / TC-S3-01: creates a guest with full details', async () => {
    const payload = {
      full_name: `${TEST_PREFIX}Full Guest`,
      email: 'test.full.guest@example.com',
      phone: '0771234567',
      nic: '199912345678',
      address: '12 Beach Road, Galle',
      gender: 'Female',
    }
    const { data, error } = await client.from('guests').insert(payload).select().single()
    expect(error).toBeNull()
    cleanup.track('guests', data.id)
    expect(data.full_name).toBe(payload.full_name)
    expect(data.email).toBe(payload.email)
  })

  it('TC-GUEST-02 / TC-S3-02: creates a guest with only the required full_name field', async () => {
    const { data, error } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Minimal Guest` })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('guests', data.id)
    expect(data.email).toBeNull()
    expect(data.gender).toBeNull()
  })

  it('TC-GUEST-03 / TC-S3-03: gender is restricted to Male/Female/Other by a DB check constraint', async () => {
    const { data, error } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Bad Gender Guest`, gender: 'Alien' })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-GUEST-04 / TC-S3-04: updates an existing guest\'s contact details', async () => {
    const { data: created } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Update Guest`, phone: '0770000000' })
      .select()
      .single()
    cleanup.track('guests', created.id)

    const { data: updated, error } = await client
      .from('guests')
      .update({ phone: '0779999999' })
      .eq('id', created.id)
      .select()
      .single()
    expect(error).toBeNull()
    expect(updated.phone).toBe('0779999999')
  })

  it('TC-GUEST-05 / TC-S3-05: deletes a guest', async () => {
    const { data: created } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Delete Guest` })
      .select()
      .single()

    const { error: deleteError } = await client.from('guests').delete().eq('id', created.id)
    expect(deleteError).toBeNull()

    const { data: refetch } = await client.from('guests').select().eq('id', created.id).maybeSingle()
    expect(refetch).toBeNull()
  })

  it('TC-GUEST-06 / TC-S3-06: guest list can be searched by name substring', async () => {
    const uniqueName = `${TEST_PREFIX}Zzyx Searchable Guest`
    const { data: created } = await client.from('guests').insert({ full_name: uniqueName }).select().single()
    cleanup.track('guests', created.id)

    const { data: found, error } = await client.from('guests').select().ilike('full_name', '%Zzyx Searchable%')
    expect(error).toBeNull()
    expect(found.some((g) => g.id === created.id)).toBe(true)
  })

  it('TC-GUEST-07 / TC-S3-07: guest email has no server-side format validation', async () => {
    const { data, error } = await client
      .from('guests')
      .insert({ full_name: `${TEST_PREFIX}Bad Email Guest`, email: 'not-an-email' })
      .select()
      .single()
    // Expected: a malformed email address should be rejected.
    // Actual: guests.email is a plain `text` column with no format check,
    // and the UI only relies on the browser's type="email" hint, so this succeeds.
    cleanup.track('guests', data?.id)
    expect(error).toBeNull()
    expect(data.email).toBe('not-an-email')
  })
})

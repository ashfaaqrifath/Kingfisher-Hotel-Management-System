import { describe, it, expect, afterEach } from 'vitest'
import { freshClient, ADMIN_EMAIL, ADMIN_PASSWORD, STAFF_EMAIL, STAFF_PASSWORD } from './helpers'

// TC-AUTH-01..08 / TC-S2-01..08

describe('Authentication', () => {
  let client

  afterEach(async () => {
    if (client) await client.auth.signOut()
  })

  it('TC-AUTH-01 / TC-S2-01: admin logs in with valid credentials', async () => {
    client = freshClient()
    const { data, error } = await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
    expect(error).toBeNull()
    expect(data.session).toBeTruthy()
    expect(data.user.email).toBe(ADMIN_EMAIL)
  })

  it('TC-AUTH-02 / TC-S2-02: staff logs in with valid credentials', async () => {
    client = freshClient()
    const { data, error } = await client.auth.signInWithPassword({ email: STAFF_EMAIL, password: STAFF_PASSWORD })
    expect(error).toBeNull()
    expect(data.session).toBeTruthy()
    expect(data.user.email).toBe(STAFF_EMAIL)
  })

  it('TC-AUTH-03 / TC-S2-03: login fails with an incorrect password', async () => {
    client = freshClient()
    const { data, error } = await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: 'wrong-password-xyz' })
    expect(error).toBeTruthy()
    expect(data.session).toBeNull()
  })

  it('TC-AUTH-04 / TC-S2-04: login fails for a non-existent account', async () => {
    client = freshClient()
    const { data, error } = await client.auth.signInWithPassword({
      email: 'no-such-user-kingfisher-test@example.com',
      password: 'whatever123',
    })
    expect(error).toBeTruthy()
    expect(data.session).toBeNull()
  })

  it('TC-AUTH-05 / TC-S2-05: an authenticated session resolves a matching profile row', async () => {
    client = freshClient()
    const { data: signInData } = await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
    const { data: profile, error } = await client
      .from('profiles')
      .select('id, full_name, role, email')
      .eq('id', signInData.user.id)
      .single()
    expect(error).toBeNull()
    expect(profile.id).toBe(signInData.user.id)
    expect(profile.email).toBe(ADMIN_EMAIL)
  })

  it('TC-AUTH-06 / TC-S2-06: admin account resolves to an admin/owner role', async () => {
    client = freshClient()
    const { data: signInData } = await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
    const { data: profile } = await client.from('profiles').select('role').eq('id', signInData.user.id).single()
    expect(['owner', 'admin']).toContain(profile.role)
  })

  it('TC-AUTH-07 / TC-S2-07: staff account resolves to the staff role, not admin', async () => {
    client = freshClient()
    const { data: signInData } = await client.auth.signInWithPassword({ email: STAFF_EMAIL, password: STAFF_PASSWORD })
    const { data: profile } = await client.from('profiles').select('role').eq('id', signInData.user.id).single()
    expect(profile.role).toBe('staff')
    expect(['owner', 'admin']).not.toContain(profile.role)
  })

  it('TC-AUTH-08 / TC-S2-08: logout clears the session', async () => {
    client = freshClient()
    await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
    const before = await client.auth.getSession()
    expect(before.data.session).toBeTruthy()

    await client.auth.signOut()
    const after = await client.auth.getSession()
    expect(after.data.session).toBeNull()
  })
})

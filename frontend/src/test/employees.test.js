import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, staffClient, makeCleanup, TEST_PREFIX } from './helpers'

// TC-EMP-01..07 / TC-S4-01..07

describe('Employees module', () => {
  let client
  const cleanup = makeCleanup()

  beforeAll(async () => {
    client = await adminClient()
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-EMP-01 / TC-S4-01: creates an employee with all fields', async () => {
    const payload = {
      full_name: `${TEST_PREFIX}Full Employee`,
      email: 'test.employee@example.com',
      phone: '0711112222',
      job_role: 'Reception',
      salary: 85000,
      hire_date: '2025-01-15',
      status: 'Active',
    }
    const { data, error } = await client.from('employees').insert(payload).select().single()
    expect(error).toBeNull()
    cleanup.track('employees', data.id)
    expect(data.job_role).toBe('Reception')
    expect(Number(data.salary)).toBe(85000)
  })

  it('TC-EMP-02 / TC-S4-02: job_role is restricted to the allowed enum by a DB check constraint', async () => {
    const { data, error } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Bad Role Employee`, job_role: 'Astronaut' })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-EMP-03 / TC-S4-03: status is restricted to Active/On Leave/Terminated', async () => {
    const { data, error } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Bad Status Employee`, job_role: 'Chef', status: 'Retired' })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-EMP-04 / TC-S4-04: updates an employee\'s salary and status', async () => {
    const { data: created } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Update Employee`, job_role: 'Housekeeping', salary: 50000 })
      .select()
      .single()
    cleanup.track('employees', created.id)

    const { data: updated, error } = await client
      .from('employees')
      .update({ salary: 60000, status: 'On Leave' })
      .eq('id', created.id)
      .select()
      .single()
    expect(error).toBeNull()
    expect(Number(updated.salary)).toBe(60000)
    expect(updated.status).toBe('On Leave')
  })

  it('TC-EMP-05 / TC-S4-05: deletes an employee', async () => {
    const { data: created } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Delete Employee`, job_role: 'Maintenance' })
      .select()
      .single()

    const { error: deleteError } = await client.from('employees').delete().eq('id', created.id)
    expect(deleteError).toBeNull()

    const { data: refetch } = await client.from('employees').select().eq('id', created.id).maybeSingle()
    expect(refetch).toBeNull()
  })

  it('TC-EMP-06 / TC-S4-06: hire_date has no validation against future dates', async () => {
    const farFuture = '2099-01-01'
    const { data, error } = await client
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Future Hire Employee`, job_role: 'Manager', hire_date: farFuture })
      .select()
      .single()
    // Expected: an unreasonable future hire date should be rejected.
    // Actual: hire_date is a plain `date` column with no range check, and the
    // employee form has no client-side validation on it either, so it's accepted.
    cleanup.track('employees', data?.id)
    expect(error).toBeNull()
    expect(data.hire_date).toBe(farFuture)
  })

  it('TC-EMP-07 / TC-S4-07: employee list can be filtered by job role and status', async () => {
    const staff = await staffClient()
    const { data: created } = await staff
      .from('employees')
      .insert({ full_name: `${TEST_PREFIX}Filter Employee`, job_role: 'Safari Guide', status: 'Active' })
      .select()
      .single()
    cleanup.track('employees', created.id)

    const { data: found, error } = await staff
      .from('employees')
      .select()
      .eq('job_role', 'Safari Guide')
      .eq('status', 'Active')
    expect(error).toBeNull()
    expect(found.some((e) => e.id === created.id)).toBe(true)
  })
})

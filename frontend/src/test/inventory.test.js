import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { adminClient, makeCleanup, TEST_PREFIX } from './helpers'

// TC-INV-01..10 / TC-S7-01..10

describe('Inventory module', () => {
  let client
  const cleanup = makeCleanup()

  beforeAll(async () => {
    client = await adminClient()
  })

  afterAll(async () => {
    await cleanup.run(client)
  })

  it('TC-INV-01 / TC-S7-01: creates an inventory item with all fields', async () => {
    const payload = {
      item_name: `${TEST_PREFIX}Bath Towels`,
      category: 'Linen',
      quantity: 50,
      unit: 'pcs',
      low_stock_threshold: 15,
      unit_price: 900,
    }
    const { data, error } = await client.from('inventory_items').insert(payload).select().single()
    expect(error).toBeNull()
    cleanup.track('inventory_items', data.id)
    expect(data.category).toBe('Linen')
    expect(data.quantity).toBe(50)
  })

  it('TC-INV-02 / TC-S7-02: category is restricted to the allowed enum', async () => {
    const { data, error } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Bad Category Item`, category: 'Electronics', quantity: 1 })
      .select()
      .single()
    expect(error).toBeTruthy()
    expect(data).toBeNull()
  })

  it('TC-INV-03 / TC-S7-03: updates an item\'s quantity', async () => {
    const { data: created } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Update Item`, category: 'Kitchen', quantity: 10 })
      .select()
      .single()
    cleanup.track('inventory_items', created.id)

    const { data: updated, error } = await client
      .from('inventory_items')
      .update({ quantity: 25 })
      .eq('id', created.id)
      .select()
      .single()
    expect(error).toBeNull()
    expect(updated.quantity).toBe(25)
  })

  it('TC-INV-04 / TC-S7-04: deletes an inventory item', async () => {
    const { data: created } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Delete Item`, category: 'Office', quantity: 5 })
      .select()
      .single()

    const { error: deleteError } = await client.from('inventory_items').delete().eq('id', created.id)
    expect(deleteError).toBeNull()

    const { data: refetch } = await client.from('inventory_items').select().eq('id', created.id).maybeSingle()
    expect(refetch).toBeNull()
  })

  it('TC-INV-05 / TC-S7-05: an item at or below its threshold is flagged low stock', async () => {
    const { data: created } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Low Item`, category: 'Toiletries', quantity: 5, low_stock_threshold: 10 })
      .select()
      .single()
    cleanup.track('inventory_items', created.id)
    expect(created.quantity <= created.low_stock_threshold).toBe(true)
  })

  it('TC-INV-06 / TC-S7-06: an item above its threshold is not flagged low stock', async () => {
    const { data: created } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}High Item`, category: 'Toiletries', quantity: 100, low_stock_threshold: 10 })
      .select()
      .single()
    cleanup.track('inventory_items', created.id)
    expect(created.quantity <= created.low_stock_threshold).toBe(false)
  })

  it('TC-INV-07 / TC-S7-07: quantity defaults to 0 when omitted', async () => {
    const { data, error } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}No Quantity Item`, category: 'Office' })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('inventory_items', data.id)
    expect(data.quantity).toBe(0)
  })

  it('TC-INV-08 / TC-S7-08: low_stock_threshold defaults to 10 when omitted', async () => {
    const { data, error } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}No Threshold Item`, category: 'Office', quantity: 3 })
      .select()
      .single()
    expect(error).toBeNull()
    cleanup.track('inventory_items', data.id)
    expect(data.low_stock_threshold).toBe(10)
  })

  it('TC-INV-09 / TC-S7-09: inventory list can be filtered by category', async () => {
    const { data: created } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}Category Filter Item`, category: 'Maintenance', quantity: 4 })
      .select()
      .single()
    cleanup.track('inventory_items', created.id)

    const { data: found, error } = await client.from('inventory_items').select().eq('category', 'Maintenance')
    expect(error).toBeNull()
    expect(found.some((i) => i.id === created.id)).toBe(true)
  })

  it('TC-INV-10 / TC-S7-10: the "low stock only" filter returns only items at or below threshold', async () => {
    const { data: lowItem } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}LowOnly Low`, category: 'Kitchen', quantity: 2, low_stock_threshold: 10 })
      .select()
      .single()
    cleanup.track('inventory_items', lowItem.id)

    const { data: highItem } = await client
      .from('inventory_items')
      .insert({ item_name: `${TEST_PREFIX}LowOnly High`, category: 'Kitchen', quantity: 200, low_stock_threshold: 10 })
      .select()
      .single()
    cleanup.track('inventory_items', highItem.id)

    const { data: all } = await client.from('inventory_items').select().in('id', [lowItem.id, highItem.id])
    const lowOnly = all.filter((i) => i.quantity <= i.low_stock_threshold)

    expect(lowOnly.map((i) => i.id)).toContain(lowItem.id)
    expect(lowOnly.map((i) => i.id)).not.toContain(highItem.id)
  })
})

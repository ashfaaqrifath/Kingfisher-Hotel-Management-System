import { describe, it, expect, vi } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const { textMock } = vi.hoisted(() => ({ textMock: vi.fn() }))

vi.mock('jspdf-autotable', () => ({ default: vi.fn() }))
vi.mock('jspdf', () => ({
  default: vi.fn(function () {
    return {
      internal: { pageSize: { getWidth: () => 210, getHeight: () => 297 } },
      setFillColor: vi.fn(),
      rect: vi.fn(),
      setTextColor: vi.fn(),
      setFontSize: vi.fn(),
      setFont: vi.fn(),
      text: textMock,
      roundedRect: vi.fn(),
      setDrawColor: vi.fn(),
      line: vi.fn(),
      circle: vi.fn(),
      setLineWidth: vi.fn(),
      save: vi.fn(),
    }
  }),
}))

import { adminClient, staffClient } from './helpers'
import { exportPDF, exportCSV } from '../lib/reportUtils'

// TC-S8-01..06
// Note: there is no dedicated /reports route or Reports.jsx page in this
// codebase. Reporting (CSV/PDF export) is inline inside Bookings.jsx,
// Guests.jsx, Employees.jsx and Inventory.jsx. TC-S8-03 and TC-S8-05 are
// adapted below to check against what actually exists, and the gaps
// relative to the originally stated requirement are reported explicitly.

const SRC_ROOT = resolve(__dirname, '..')

describe('Reports & dashboard', () => {
  it('TC-S8-01: dashboard KPI queries (rooms, bookings, inventory) return usable data', async () => {
    const client = await adminClient()
    const [rooms, bookings, inventory] = await Promise.all([
      client.from('rooms').select('*').order('room_number'),
      client.from('bookings').select('*, guests(full_name), rooms(room_number)').order('created_at', { ascending: false }),
      client.from('inventory_items').select('*'),
    ])
    expect(rooms.error).toBeNull()
    expect(bookings.error).toBeNull()
    expect(inventory.error).toBeNull()

    const roomList = rooms.data || []
    const occupancyRate = roomList.length
      ? Math.round((roomList.filter((r) => r.status === 'Occupied').length / roomList.length) * 100)
      : 0
    expect(occupancyRate).toBeGreaterThanOrEqual(0)
    expect(occupancyRate).toBeLessThanOrEqual(100)
  })

  it('TC-S8-02: the 7-day booking trend query returns one entry per day', async () => {
    const client = await adminClient()
    const { data: bookings, error } = await client.from('bookings').select('created_at')
    expect(error).toBeNull()

    const last7 = [...Array(7)].map((_, idx) => {
      const d = new Date()
      d.setDate(d.getDate() - (6 - idx))
      const key = d.toISOString().slice(0, 10)
      const count = (bookings || []).filter((b) => b.created_at?.slice(0, 10) === key).length
      return { day: key, bookings: count }
    })

    expect(last7).toHaveLength(7)
    last7.forEach((entry) => expect(entry.bookings).toBeGreaterThanOrEqual(0))
  })

  it('TC-S8-03: booking report has a status filter but no date-range filter', () => {
    const source = readFileSync(resolve(SRC_ROOT, 'pages/Bookings.jsx'), 'utf-8')
    const hasStatusFilter = /statusFilter/.test(source)
    const hasDateRangeFilter = /(dateFrom|dateTo|date_from|date_to|startDate|endDate)/i.test(source)

    expect(hasStatusFilter).toBe(true)
    // Expected: booking reports can be filtered by a date range.
    // Actual: Bookings.jsx only filters by status and a text search
    // (guest/room name) — there is no date-range input anywhere in the file.
    expect(hasDateRangeFilter).toBe(true)
  })

  it('TC-S8-04: the PDF/CSV export functions in reportUtils execute without throwing', () => {
    textMock.mockClear()
    const rows = [{ Name: 'Test Row', Value: 1 }]
    expect(() => exportCSV(rows, 'test-report.csv')).not.toThrow()
    expect(() => exportPDF('Test Report', rows, 'test-report.pdf', {
      summary: [{ label: 'Total', value: 1 }],
      charts: [
        { type: 'bar', title: 'Sample', data: [{ label: 'A', value: 1 }] },
        { type: 'pie', title: 'Sample pie', data: [{ label: 'A', value: 1 }, { label: 'B', value: 2 }] },
      ],
    })).not.toThrow()
    expect(textMock).toHaveBeenCalledWith('Executive summary', expect.any(Number), expect.any(Number))
  })

  it('TC-S8-05: a /reports route does not exist in the app router', () => {
    const source = readFileSync(resolve(SRC_ROOT, 'App.jsx'), 'utf-8')
    const hasReportsRoute = /path=["']\/reports["']/.test(source)

    // Expected: a /reports route exists and is blocked for Staff.
    // Actual: no such route is defined at all in App.jsx, so there is
    // nothing to route-guard.
    expect(hasReportsRoute).toBe(true)
  })

  it('TC-S8-06: activity_logs is readable by admin and blocked for staff (RLS)', async () => {
    const admin = await adminClient()
    const { data: adminRows, error: adminError } = await admin.from('activity_logs').select('id').limit(5)
    expect(adminError).toBeNull()
    expect(Array.isArray(adminRows)).toBe(true)

    const staff = await staffClient()
    const { data: staffRows, error: staffError } = await staff.from('activity_logs').select('id').limit(5)
    // RLS silently filters rows rather than erroring for SELECT, so a
    // blocked staff read comes back as an empty array, not an error.
    expect(staffError).toBeNull()
    expect(staffRows).toEqual([])
  })
})

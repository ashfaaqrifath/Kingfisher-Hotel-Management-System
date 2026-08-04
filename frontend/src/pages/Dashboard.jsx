import { useEffect, useMemo, useState } from 'react'
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, BarChart, Bar,
} from 'recharts'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import StatCard from '../components/StatCard'
import { supabase } from '../lib/supabaseClient'

const ROOM_COLORS = {
  Available: '#1F8A55',
  Booked: '#1D4FD8',
  Occupied: '#B3432B',
  Maintenance: '#959595',
}

export default function Dashboard() {
  const [rooms, setRooms] = useState([])
  const [bookings, setBookings] = useState([])
  const [inventory, setInventory] = useState([])
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedBookingYear, setSelectedBookingYear] = useState(new Date().getFullYear())
  const [selectedRevenueYear, setSelectedRevenueYear] = useState(new Date().getFullYear())
  const [notificationModalOpen, setNotificationModalOpen] = useState(false)

  useEffect(() => {
    async function load() {
      const [r, b, i, e] = await Promise.all([
        supabase.from('rooms').select('*').order('room_number'),
        supabase.from('bookings').select('*, guests(full_name, phone), rooms(room_number)').order('created_at', { ascending: false }),
        supabase.from('inventory_items').select('*'),
        supabase.from('employees').select('*'),
      ])
      setRooms(r.data || [])
      setBookings(b.data || [])
      setInventory(i.data || [])
      setEmployees(e.data || [])
      setLoading(false)
    }
    load()
  }, [])

  const occupancySummary = useMemo(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const counts = { Available: 0, Booked: 0, Occupied: 0, Maintenance: 0 }

    rooms.forEach((room) => {
      if (room.status === 'Maintenance') {
        counts.Maintenance += 1
        return
      }

      const activeBooking = (bookings || []).find((booking) => {
        if (!booking.room_id || booking.room_id !== room.id) return false
        if (['Cancelled', 'Checked Out'].includes(booking.status)) return false

        const bookingCheckOut = new Date(`${booking.check_out}T00:00:00`)
        return bookingCheckOut > today
      })

      if (!activeBooking) {
        counts.Available += 1
        return
      }

      if (activeBooking.status === 'Checked In') {
        counts.Occupied += 1
        return
      }

      counts.Booked += 1
    })

    return counts
  }, [rooms, bookings])

  const occupancyRate = useMemo(() => {
    if (!rooms.length) return 0
    return Math.round((occupancySummary.Occupied / rooms.length) * 100)
  }, [rooms.length, occupancySummary.Occupied])

  const revenueThisMonth = useMemo(() => {
    const now = new Date()
    return bookings
      .filter((b) => {
        const d = new Date(b.created_at)
        return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
      })
      .reduce((sum, b) => sum + Number(b.total_amount || 0), 0)
  }, [bookings])

  const lowStockCount = useMemo(
    () => inventory.filter((i) => i.quantity <= i.low_stock_threshold).length,
    [inventory]
  )

  const occupancyPie = useMemo(() => {
    return Object.entries(occupancySummary).map(([name, value]) => ({ name, value }))
  }, [occupancySummary])

  const bookingYears = useMemo(() => {
    const years = bookings
      .map((booking) => {
        const dateValue = booking.created_at
        if (!dateValue) return null
        const bookingDate = new Date(dateValue)
        return bookingDate.getFullYear()
      })
      .filter((year) => Number.isInteger(year))

    return [...new Set(years)].sort((a, b) => a - b)
  }, [bookings])

  const bookingTrend = useMemo(() => {
    return [...Array(12)].map((_, idx) => {
      const d = new Date(selectedBookingYear, idx, 1)
      const label = d.toLocaleDateString('en-GB', { month: 'short' })
      const count = bookings.reduce((sum, booking) => {
        const dateValue = booking.created_at
        if (!dateValue) return sum

        const bookingDate = new Date(dateValue)
        if (
          bookingDate.getMonth() === d.getMonth() &&
          bookingDate.getFullYear() === d.getFullYear()
        ) {
          return sum + 1
        }

        return sum
      }, 0)

      return { month: label, bookings: count }
    })
  }, [bookings, selectedBookingYear])

  const revenueYears = useMemo(() => {
    const years = bookings
      .map((booking) => {
        const dateValue = booking.check_in || booking.created_at
        if (!dateValue) return null
        const bookingDate = new Date(dateValue)
        return bookingDate.getFullYear()
      })
      .filter((year) => Number.isInteger(year))

    return [...new Set(years)].sort((a, b) => a - b)
  }, [bookings])

  const monthlyRevenueTrend = useMemo(() => {
    return [...Array(12)].map((_, idx) => {
      const d = new Date(selectedRevenueYear, idx, 1)
      const label = d.toLocaleDateString('en-GB', { month: 'short' })
      const revenue = bookings.reduce((sum, booking) => {
        const dateValue = booking.check_in || booking.created_at
        if (!dateValue) return sum

        const bookingDate = new Date(dateValue)
        if (
          bookingDate.getMonth() === d.getMonth() &&
          bookingDate.getFullYear() === d.getFullYear()
        ) {
          return sum + Number(booking.total_amount || 0)
        }

        return sum
      }, 0)

      return { month: label, revenue }
    })
  }, [bookings, selectedRevenueYear])

  const stockChart = useMemo(
    () => inventory.slice(0, 6).map((i) => ({ name: i.item_name, qty: i.quantity })),
    [inventory]
  )

  const upcomingCheckins = useMemo(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const maxDate = new Date(today)
    maxDate.setDate(today.getDate() + 3)

    return bookings
      .filter((booking) => {
        if (['Cancelled', 'Checked Out'].includes(booking.status)) return false
        if (!booking.check_in) return false
        const date = new Date(`${booking.check_in}T00:00:00`)
        return date >= today && date <= maxDate
      })
      .sort((a, b) => new Date(a.check_in) - new Date(b.check_in))
      .map((booking) => ({
        guestName: booking.guests?.full_name || '—',
        phone: booking.guests?.phone || '—',
        checkIn: booking.check_in || '—',
        checkOut: booking.check_out || '—',
        room: booking.rooms?.room_number || '—',
      }))
  }, [bookings])

  const upcomingCheckouts = useMemo(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const maxDate = new Date(today)
    maxDate.setDate(today.getDate() + 3)

    return bookings
      .filter((booking) => {
        if (['Cancelled', 'Checked Out'].includes(booking.status)) return false
        if (!booking.check_out) return false
        const date = new Date(`${booking.check_out}T00:00:00`)
        return date >= today && date <= maxDate
      })
      .sort((a, b) => new Date(a.check_out) - new Date(b.check_out))
      .map((booking) => ({
        guestName: booking.guests?.full_name || '—',
        phone: booking.guests?.phone || '—',
        checkIn: booking.check_in || '—',
        checkOut: booking.check_out || '—',
        room: booking.rooms?.room_number || '—',
      }))
  }, [bookings])

  const lowStockAlerts = useMemo(
    () => inventory.filter((item) => item.quantity <= item.low_stock_threshold),
    [inventory]
  )

  const notificationCount = useMemo(
    () => upcomingCheckins.length + upcomingCheckouts.length + lowStockAlerts.length,
    [upcomingCheckins.length, upcomingCheckouts.length, lowStockAlerts.length]
  )

  const inventoryStatusChart = useMemo(() => {
    const lowStock = inventory.filter((item) => item.quantity <= item.low_stock_threshold)
    return lowStock.slice(0, 6).map((item) => ({
      name: item.item_name,
      qty: item.quantity,
      threshold: item.low_stock_threshold,
    }))
  }, [inventory])

  const employeeStatusChart = useMemo(() => {
    const counts = {}
    employees.forEach((employee) => {
      counts[employee.status] = (counts[employee.status] || 0) + 1
    })
    return Object.entries(counts).map(([name, value]) => ({ name, value }))
  }, [employees])

  return (
    <Layout
      title="Dashboard"
      subtitle="Live overview of resort operations"
      actions={(
        <button
          type="button"
          className="relative btn btn-secondary"
          onClick={() => setNotificationModalOpen(true)}
          aria-label="Open notifications"
        >
          <span className="text-lg">🔔</span>
          {notificationCount > 0 && (
            <span className="absolute -right-2 -top-2 inline-flex h-5 min-w-[20px] items-center justify-center rounded-full bg-rose-600 px-1 text-[10px] font-semibold text-white">
              {notificationCount}
            </span>
          )}
        </button>
      )}
    >
      {loading ? (
        <p className="text-navy-700">Loading dashboard…</p>
      ) : (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <StatCard label="Occupancy Rate" value={`${occupancyRate}%`} accent="teal" hint={`${rooms.length} total rooms`} />
            <StatCard label="Active Bookings" value={bookings.filter(b => ['Booked', 'Checked In'].includes(b.status)).length} accent="navy" />
            <StatCard label="Revenue (this month)" value={`LKR ${revenueThisMonth.toLocaleString()}`} accent="navy" />
            <StatCard label="Low Stock Alerts" value={lowStockCount} accent={lowStockCount > 0 ? 'rust' : 'teal'} hint="Items below threshold" />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mb-6">
            <div className="card lg:col-span-2">
              <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between mb-4">
                <h3 className="font-display font-semibold">Bookings - selected year</h3>
                <label className="flex items-center gap-2 text-sm text-navy-700">
                  <span>Year</span>
                  <select
                    className="input input-sm min-w-[110px]"
                    value={selectedBookingYear}
                    onChange={(e) => setSelectedBookingYear(Number(e.target.value))}
                  >
                    {bookingYears.length === 0 ? (
                      <option value={new Date().getFullYear()}>{new Date().getFullYear()}</option>
                    ) : (
                      bookingYears.map((year) => (
                        <option key={year} value={year}>{year}</option>
                      ))
                    )}
                  </select>
                </label>
              </div>
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={bookingTrend}>
                  <CartesianGrid stroke="#E2DDD1" vertical={false} />
                  <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#1F4E76' }} axisLine={{ stroke: '#E2DDD1' }} tickLine={false} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4, borderColor: '#E2DDD1' }} />
                  <Line type="monotone" dataKey="bookings" stroke="#0F2B46" strokeWidth={2} dot={{ r: 3, fill: '#0E7C7B' }} />
                </LineChart>
              </ResponsiveContainer>
            </div>

            <div className="card">
              <h3 className="font-display font-semibold mb-4">Room status split</h3>
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie data={occupancyPie} dataKey="value" nameKey="name" innerRadius={50} outerRadius={80} paddingAngle={2}>
                    {occupancyPie.map((entry) => (
                      <Cell key={entry.name} fill={ROOM_COLORS[entry.name]} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4 }} />
                </PieChart>
              </ResponsiveContainer>
              <div className="flex justify-center gap-4 text-xs mt-2">
                {Object.entries(ROOM_COLORS).map(([k, c]) => (
                  <span key={k} className="flex items-center gap-1">
                    <span className="w-2.5 h-2.5 inline-block rounded-sm" style={{ background: c }} /> {k}
                  </span>
                ))}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
            <div className="card lg:col-span-3">
              <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between mb-4">
                <h3 className="font-display font-semibold">Monthly Revenue Trend</h3>
                <label className="flex items-center gap-2 text-sm text-navy-700">
                  <span>Year</span>
                  <select
                    className="input input-sm min-w-[110px]"
                    value={selectedRevenueYear}
                    onChange={(e) => setSelectedRevenueYear(Number(e.target.value))}
                  >
                    {revenueYears.length === 0 ? (
                      <option value={new Date().getFullYear()}>{new Date().getFullYear()}</option>
                    ) : (
                      revenueYears.map((year) => (
                        <option key={year} value={year}>{year}</option>
                      ))
                    )}
                  </select>
                </label>
              </div>
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={monthlyRevenueTrend}>
                  <CartesianGrid stroke="#E2DDD1" vertical={false} />
                  <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#1F4E76' }} axisLine={{ stroke: '#E2DDD1' }} tickLine={false} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <Tooltip
                    formatter={(value) => [`LKR ${Number(value).toLocaleString()}`, 'Revenue']}
                    contentStyle={{ fontSize: 12, borderRadius: 4, borderColor: '#E2DDD1' }}
                  />
                  <Line type="monotone" dataKey="revenue" stroke="#129593" strokeWidth={2} dot={{ r: 3, fill: '#0F2B46' }} />
                </LineChart>
              </ResponsiveContainer>
            </div>

            <div className="card lg:col-span-3">
              <div className="flex items-center justify-between mb-3">

              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

                <div className="border border-sand-300 rounded-lg p-3 bg-sand-50">
                  <div className="flex items-center justify-between mb-3">
                    <h4 className="font-semibold text-navy-950">Upcoming check-outs</h4>
                    <span className="text-xs text-navy-700">{upcomingCheckouts.length}</span>
                  </div>

                  <div className="max-h-72 overflow-y-auto space-y-2 pr-1">
                    {upcomingCheckouts.length === 0 ? (
                      <p className="text-sm text-navy-700">No check-outs scheduled in the next 7 days.</p>
                    ) : (
                      upcomingCheckouts.map((booking, index) => (
                        <div key={`checkout-${booking.guestName}-${booking.checkOut}-${index}`} className="border border-sand-300 rounded-lg p-3 bg-white">
                          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-2">
                            <div>
                              <p className="font-semibold text-navy-950">{booking.guestName}</p>
                              <p className="text-sm text-navy-700">Phone: {booking.phone}</p>
                            </div>
                            <div className="text-sm text-navy-700">
                              <p>Check-in: {booking.checkIn}</p>
                              <p>Check-out: {booking.checkOut}</p>
                              <p>Room: {booking.room}</p>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>

                <div className="border border-sand-300 rounded-lg p-3 bg-sand-50">
                  <div className="flex items-center justify-between mb-3">
                    <h4 className="font-semibold text-navy-950">Upcoming check-ins</h4>
                    <span className="text-xs text-navy-700">{upcomingCheckins.length}</span>
                  </div>

                  <div className="max-h-72 overflow-y-auto space-y-2 pr-1">
                    {upcomingCheckins.length === 0 ? (
                      <p className="text-sm text-navy-700">No check-ins scheduled in the next 7 days.</p>
                    ) : (
                      upcomingCheckins.map((booking, index) => (
                        <div key={`checkin-${booking.guestName}-${booking.checkIn}-${index}`} className="border border-sand-300 rounded-lg p-3 bg-white">
                          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-2">
                            <div>
                              <p className="font-semibold text-navy-950">{booking.guestName}</p>
                              <p className="text-sm text-navy-700">Phone: {booking.phone}</p>
                            </div>
                            <div className="text-sm text-navy-700">
                              <p>Check-in: {booking.checkIn}</p>
                              <p>Check-out: {booking.checkOut}</p>
                              <p>Room: {booking.room}</p>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>


              </div>
            </div>

            <div className="card">
              <h3 className="font-display font-semibold mb-4">Inventory snapshot</h3>
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={stockChart} layout="vertical" margin={{ left: 10 }}>
                  <CartesianGrid stroke="#E2DDD1" horizontal={false} />
                  <XAxis type="number" tick={{ fontSize: 11, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <YAxis dataKey="name" type="category" width={90} tick={{ fontSize: 10, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4 }} />
                  <Bar dataKey="qty" fill="#129593" radius={[0, 2, 2, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="card">
              <h3 className="font-display font-semibold mb-4">Low stock alerts</h3>
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={inventoryStatusChart} layout="vertical" margin={{ left: 10 }}>
                  <CartesianGrid stroke="#E2DDD1" horizontal={false} />
                  <XAxis type="number" tick={{ fontSize: 11, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <YAxis dataKey="name" type="category" width={90} tick={{ fontSize: 10, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4 }} />
                  <Bar dataKey="qty" fill="#B3432B" radius={[0, 2, 2, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="card">
              <h3 className="font-display font-semibold mb-4">Employee status mix</h3>
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie data={employeeStatusChart} dataKey="value" nameKey="name" innerRadius={42} outerRadius={76} paddingAngle={2}>
                    {employeeStatusChart.map((entry, index) => (
                      <Cell key={`${entry.name}-${index}`} fill={['#0F2B46', '#1F8A55', '#B3432B', '#C97A2B', '#129593'][index % 5]} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4 }} />
                </PieChart>
              </ResponsiveContainer>
              <div className="flex flex-wrap justify-center gap-3 text-xs mt-2">
                {employeeStatusChart.map((entry, index) => (
                  <span key={entry.name} className="flex items-center gap-1">
                    <span className="w-2.5 h-2.5 inline-block rounded-sm" style={{ background: ['#0F2B46', '#1F8A55', '#B3432B', '#C97A2B', '#129593'][index % 5] }} />
                    {entry.name}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </>
      )}

      <Modal open={notificationModalOpen} title="Notifications" onClose={() => setNotificationModalOpen(false)} width="max-w-3xl">
        <div className="space-y-5">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="rounded border border-sand-300 bg-sand-50 p-4">
              <div className="mb-3 flex items-center justify-between">
                <h4 className="font-semibold text-navy-950">Check-ins in next 3 days</h4>
                <span className="text-xs text-navy-700">{upcomingCheckins.length}</span>
              </div>
              <div className="space-y-2 max-h-72 overflow-auto pr-1">
                {upcomingCheckins.length === 0 ? (
                  <p className="text-sm text-navy-700">No upcoming check-ins found.</p>
                ) : (
                  upcomingCheckins.map((booking, index) => (
                    <div key={`notify-checkin-${booking.guestName}-${booking.checkIn}-${index}`} className="rounded border border-sand-300 bg-white p-3 text-sm text-navy-800">
                      <p className="font-semibold text-navy-950">{booking.guestName}</p>
                      <p>Room: {booking.room}</p>
                      <p>Check-in: {booking.checkIn}</p>
                      <p>Check-out: {booking.checkOut}</p>
                      <p>Phone: {booking.phone}</p>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="rounded border border-sand-300 bg-sand-50 p-4">
              <div className="mb-3 flex items-center justify-between">
                <h4 className="font-semibold text-navy-950">Check-outs in next 3 days</h4>
                <span className="text-xs text-navy-700">{upcomingCheckouts.length}</span>
              </div>
              <div className="space-y-2 max-h-72 overflow-auto pr-1">
                {upcomingCheckouts.length === 0 ? (
                  <p className="text-sm text-navy-700">No upcoming check-outs found.</p>
                ) : (
                  upcomingCheckouts.map((booking, index) => (
                    <div key={`notify-checkout-${booking.guestName}-${booking.checkOut}-${index}`} className="rounded border border-sand-300 bg-white p-3 text-sm text-navy-800">
                      <p className="font-semibold text-navy-950">{booking.guestName}</p>
                      <p>Room: {booking.room}</p>
                      <p>Check-in: {booking.checkIn}</p>
                      <p>Check-out: {booking.checkOut}</p>
                      <p>Phone: {booking.phone}</p>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

          <div className="rounded border border-sand-300 bg-sand-50 p-4">
            <div className="mb-3 flex items-center justify-between">
              <h4 className="font-semibold text-navy-950">Low stock alerts</h4>
              <span className="text-xs text-navy-700">{lowStockAlerts.length}</span>
            </div>
            <div className="space-y-2 max-h-72 overflow-auto pr-1">
              {lowStockAlerts.length === 0 ? (
                <p className="text-sm text-navy-700">No low stock alerts at the moment.</p>
              ) : (
                lowStockAlerts.map((item) => (
                  <div key={item.id} className="rounded border border-rose-200 bg-white p-3 text-sm text-navy-800">
                    <div className="flex items-center justify-between gap-3">
                      <p className="font-semibold text-navy-950">{item.item_name}</p>
                      <span className="rounded bg-rose-50 px-2 py-1 text-[11px] font-semibold text-rose-700">Low stock</span>
                    </div>
                    <p>Quantity: {item.quantity}</p>
                    <p>Threshold: {item.low_stock_threshold}</p>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="flex justify-end">
            <button type="button" className="btn btn-secondary" onClick={() => setNotificationModalOpen(false)}>Close</button>
          </div>
        </div>
      </Modal>
    </Layout>
  )
}

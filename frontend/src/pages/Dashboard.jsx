import { useEffect, useMemo, useState } from 'react'
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, BarChart, Bar,
} from 'recharts'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import { supabase } from '../lib/supabaseClient'

const ROOM_COLORS = {
  Available: '#1F8A55',
  Occupied: '#B3432B',
  Maintenance: '#C97A2B',
}

export default function Dashboard() {
  const [rooms, setRooms] = useState([])
  const [bookings, setBookings] = useState([])
  const [inventory, setInventory] = useState([])
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)

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

  const occupancyRate = useMemo(() => {
    if (!rooms.length) return 0
    const occupied = rooms.filter((r) => r.status === 'Occupied').length
    return Math.round((occupied / rooms.length) * 100)
  }, [rooms])

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
    const counts = { Available: 0, Occupied: 0, Maintenance: 0 }
    rooms.forEach((r) => { counts[r.status] = (counts[r.status] || 0) + 1 })
    return Object.entries(counts).map(([name, value]) => ({ name, value }))
  }, [rooms])

  const bookingTrend = useMemo(() => {
    const last7 = [...Array(7)].map((_, idx) => {
      const d = new Date()
      d.setDate(d.getDate() - (6 - idx))
      const key = d.toISOString().slice(0, 10)
      const label = d.toLocaleDateString('en-GB', { weekday: 'short' })
      const count = bookings.filter((b) => b.created_at?.slice(0, 10) === key).length
      return { day: label, bookings: count }
    })
    return last7
  }, [bookings])

  const monthlyRevenueTrend = useMemo(() => {
    return [...Array(6)].map((_, idx) => {
      const d = new Date()
      d.setMonth(d.getMonth() - (5 - idx), 1)
      const key = d.toISOString().slice(0, 7)
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
  }, [bookings])

  const stockChart = useMemo(
    () => inventory.slice(0, 6).map((i) => ({ name: i.item_name, qty: i.quantity })),
    [inventory]
  )

  const upcomingCheckouts = useMemo(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const maxDate = new Date(today)
    maxDate.setDate(today.getDate() + 3)

    return bookings
      .filter((booking) => {
        if (booking.status === 'Cancelled') return false
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
    <Layout title="Dashboard" subtitle="Live overview of resort operations">
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
              <h3 className="font-display font-semibold mb-4">Bookings - last 7 days</h3>
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={bookingTrend}>
                  <CartesianGrid stroke="#E2DDD1" vertical={false} />
                  <XAxis dataKey="day" tick={{ fontSize: 12, fill: '#1F4E76' }} axisLine={{ stroke: '#E2DDD1' }} tickLine={false} />
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
              <h3 className="font-display font-semibold mb-4">Monthly Revenue Trend</h3>
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
                <h3 className="font-display font-semibold">Upcoming checkouts (next 3 days)</h3>
                <span className="text-xs text-navy-700">{upcomingCheckouts.length} booking(s)</span>
              </div>

              {upcomingCheckouts.length === 0 ? (
                <p className="text-sm text-navy-700">No checkouts scheduled in the next 3 days.</p>
              ) : (
                <div className="space-y-2">
                  {upcomingCheckouts.map((booking, index) => (
                    <div key={`${booking.guestName}-${booking.checkOut}-${index}`} className="border border-sand-300 rounded-lg p-3 bg-sand-50">
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
                  ))}
                </div>
              )}
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
    </Layout>
  )
}

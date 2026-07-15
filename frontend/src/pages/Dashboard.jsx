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
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function load() {
      const [r, b, i] = await Promise.all([
        supabase.from('rooms').select('*').order('room_number'),
        supabase.from('bookings').select('*, guests(full_name), rooms(room_number)').order('created_at', { ascending: false }),
        supabase.from('inventory_items').select('*'),
      ])
      setRooms(r.data || [])
      setBookings(b.data || [])
      setInventory(i.data || [])
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

  const stockChart = useMemo(
    () => inventory.slice(0, 6).map((i) => ({ name: i.item_name, qty: i.quantity })),
    [inventory]
  )

  return (
    <Layout title="Dashboard" subtitle="Live overview of resort operations">
      {loading ? (
        <p className="text-navy-700">Loading dashboard…</p>
      ) : (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <StatCard label="Occupancy Rate" value={`${occupancyRate}%`} accent="teal" hint={`${rooms.length} total rooms`} />
            <StatCard label="Active Bookings" value={bookings.filter(b => ['Booked','Checked In'].includes(b.status)).length} accent="navy" />
            <StatCard label="Revenue (this month)" value={`LKR ${revenueThisMonth.toLocaleString()}`} accent="navy" />
            <StatCard label="Low Stock Alerts" value={lowStockCount} accent={lowStockCount > 0 ? 'rust' : 'teal'} hint="Items below threshold" />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mb-6">
            <div className="card lg:col-span-2">
              <h3 className="font-display font-semibold mb-4">Bookings — last 7 days</h3>
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
            <div className="card lg:col-span-2">
              <h3 className="font-display font-semibold mb-1">Room board</h3>
              <p className="text-xs text-navy-700 mb-4">Live key-card view of every room</p>
              <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
                {rooms.map((r) => (
                  <div
                    key={r.id}
                    className="aspect-square rounded-sm border flex flex-col items-center justify-center text-xs font-mono"
                    style={{
                      borderColor: ROOM_COLORS[r.status],
                      color: ROOM_COLORS[r.status],
                      background: `${ROOM_COLORS[r.status]}0D`,
                    }}
                    title={`${r.room_type} · ${r.status}`}
                  >
                    <span className="font-semibold">{r.room_number}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="card">
              <h3 className="font-display font-semibold mb-4">Inventory snapshot</h3>
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={stockChart} layout="vertical" margin={{ left: 10 }}>
                  <CartesianGrid stroke="#E2DDD1" horizontal={false} />
                  <XAxis type="number" tick={{ fontSize: 11, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <YAxis dataKey="name" type="category" width={90} tick={{ fontSize: 10, fill: '#1F4E76' }} axisLine={false} tickLine={false} />
                  <Tooltip contentStyle={{ fontSize: 12, borderRadius: 4 }} />
                  <Bar dataKey="qty" fill="#129593" radius={[0, 2, 2, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </>
      )}
    </Layout>
  )
}

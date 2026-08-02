import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportCSV, exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { logActivity } from '../lib/activityLog'
import { validateFullName, validateEmail, validatePhoneNumber } from '../lib/validation'

const STATUSES = ['Booked', 'Checked In', 'Checked Out', 'Cancelled']
const EMPTY = {
  guest_id: '',
  full_name: '',
  email: '',
  phone: '',
  nic: '',
  address: '',
  gender: 'Male',
  room_id: '',
  check_in: '',
  check_out: '',
  status: 'Booked',
  discount_amount: '',
  total_amount: '',
}

const STATUS_BADGE = {
  Booked: 'badge-maintenance',
  'Checked In': 'badge-occupied',
  'Checked Out': 'badge-available',
  Cancelled: 'bg-navy-700/10 text-navy-700',
}

function nightsBetween(a, b) {
  const d1 = new Date(a), d2 = new Date(b)
  return Math.max(1, Math.round((d2 - d1) / (1000 * 60 * 60 * 24)))
}

export default function Bookings() {
  const [bookings, setBookings] = useState([])
  const [guests, setGuests] = useState([])
  const [rooms, setRooms] = useState([])
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [formError, setFormError] = useState('')
  const [loading, setLoading] = useState(true)

  async function load() {
    setLoading(true)
    const [b, g, r] = await Promise.all([
      supabase.from('bookings').select('*, guests(full_name), rooms(room_number, price_per_night)').order('created_at', { ascending: false }),
      supabase.from('guests').select('id, full_name, email, phone, nic, address, gender').order('full_name'),
      supabase.from('rooms').select('id, room_number, price_per_night, status').order('room_number'),
    ])
    setBookings(b.data || [])
    setGuests(g.data || [])
    setRooms(r.data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() {
    setForm({ ...EMPTY })
    setFormError('')
    setModalOpen(true)
  }

  function calcTotal(roomId, checkIn, checkOut) {
    const room = rooms.find((r) => r.id === roomId)
    if (!room || !checkIn || !checkOut) return ''
    return room.price_per_night * nightsBetween(checkIn, checkOut)
  }

  function getFinalTotal(roomId, checkIn, checkOut, discountAmount) {
    const baseTotal = Number(calcTotal(roomId, checkIn, checkOut)) || 0
    const discount = Number(discountAmount || 0)

    if (!Number.isFinite(discount) || discount < 0) {
      return baseTotal
    }

    return Math.max(0, baseTotal - discount)
  }

  function handleRoomOrDateChange(patch) {
    const next = { ...form, ...patch }
    next.total_amount = getFinalTotal(next.room_id, next.check_in, next.check_out, next.discount_amount)
    setForm(next)
    if (formError) setFormError('')
  }

  function validateBookingForm() {
    const guestId = String(form.guest_id || '').trim()

    if (!guestId) {
      // Validate full name
      const nameValidation = validateFullName(form.full_name)
      if (!nameValidation.valid) {
        return nameValidation.error
      }

      // Validate email
      const emailValidation = validateEmail(form.email)
      if (!emailValidation.valid) {
        return emailValidation.error
      }

      // Validate phone
      const phoneValidation = validatePhoneNumber(form.phone)
      if (!phoneValidation.valid) {
        return phoneValidation.error
      }

      const nic = String(form.nic || '').trim()
      const address = String(form.address || '').trim()

      if (!nic || !address) {
        return 'Please choose an existing guest or complete all guest details before saving.'
      }
      if (nic.length < 5) {
        return 'Please enter a valid NIC number.'
      }
    }

    if (!form.room_id) {
      return 'Please select an available room for this booking.'
    }

    if (!form.check_in || !form.check_out) {
      return 'Please choose both a check-in and a check-out date.'
    }

    const checkIn = new Date(form.check_in)
    const checkOut = new Date(form.check_out)

    if (Number.isNaN(checkIn.getTime()) || Number.isNaN(checkOut.getTime())) {
      return 'Please use valid check-in and check-out dates.'
    }

    if (checkOut <= checkIn) {
      return 'Check-out must be later than check-in.'
    }

    const selectedRoom = rooms.find((r) => r.id === form.room_id)
    if (!selectedRoom) {
      return 'The selected room could not be found. Please choose another room.'
    }

    if (selectedRoom.status !== 'Available') {
      return 'Please choose a room that is currently available.'
    }

    const discount = Number(form.discount_amount || 0)
    if (!Number.isFinite(discount) || discount < 0) {
      return 'Please enter a valid discount amount.'
    }

    const computedTotal = getFinalTotal(form.room_id, form.check_in, form.check_out, form.discount_amount)
    if (!Number.isFinite(computedTotal) || computedTotal <= 0) {
      return 'The booking total could not be calculated. Please verify the room, stay dates, and discount amount.'
    }

    return ''
  }

  async function handleSubmit(e) {
    e.preventDefault()

    const validationMessage = validateBookingForm()
    if (validationMessage) {
      setFormError(validationMessage)
      return
    }

    let guestId = String(form.guest_id || '').trim()

    if (!guestId) {
      const guestPayload = {
        full_name: String(form.full_name || '').trim(),
        email: String(form.email || '').trim(),
        phone: String(form.phone || '').trim(),
        nic: String(form.nic || '').trim(),
        address: String(form.address || '').trim(),
        gender: form.gender,
      }

      const { data: createdGuest, error: guestError } = await supabase
        .from('guests')
        .insert(guestPayload)
        .select('id')
        .single()

      if (guestError) {
        setFormError(`Could not create the guest: ${guestError.message}`)
        return
      }

      guestId = createdGuest?.id || ''
    }

    const payload = {
      guest_id: guestId,
      room_id: form.room_id,
      check_in: form.check_in,
      check_out: form.check_out,
      status: form.status,
      total_amount: getFinalTotal(form.room_id, form.check_in, form.check_out, form.discount_amount),
    }

    const { error: bookingError } = await supabase.from('bookings').insert(payload)
    if (bookingError) {
      setFormError(`Could not create the booking: ${bookingError.message}`)
      return
    }

    await logActivity('Created booking', `Room ${rooms.find(r => r.id === form.room_id)?.room_number}`)
    setFormError('')
    setModalOpen(false)
    load()
  }

  function canCheckIn(booking) {
    if (!booking?.check_in) return false
    const checkIn = new Date(`${booking.check_in}T00:00:00`)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return checkIn <= today
  }

  async function updateStatus(booking, status) {
    if (status === 'Checked In' && !canCheckIn(booking)) {
      return
    }

    await supabase.from('bookings').update({ status }).eq('id', booking.id)
    await logActivity(`Booking ${status}`, `${booking.guests?.full_name} · Room ${booking.rooms?.room_number}`)
    load()
  }

  const availableRooms = rooms.filter((r) => r.status === 'Available')

  const filtered = bookings.filter((b) => {
    const matchesSearch = `${b.guests?.full_name} ${b.rooms?.room_number}`.toLowerCase().includes(search.toLowerCase())
    const matchesStatus = statusFilter === 'All' || b.status === statusFilter
    return matchesSearch && matchesStatus
  })

  return (
    <Layout
      title="Manage Bookings"
      subtitle="Reservations, check-ins, and check-outs"
      actions={<button className="btn btn-primary" onClick={openCreate}>+ New Booking</button>}
    >
      <Toolbar search={search} onSearch={setSearch} placeholder="Search guest or room…">
        <select className="input max-w-[160px]" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option>All</option>
          {STATUSES.map((s) => <option key={s}>{s}</option>)}
        </select>
        <div className="ml-auto flex gap-2">
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((b) => ({
              Guest: b.guests?.full_name || '—',
              Room: b.rooms?.room_number || '—',
              'Check-in': b.check_in,
              'Check-out': b.check_out,
              Status: b.status,
              'Total (LKR)': b.total_amount,
            }))
            exportCSV(rows, 'bookings-report.csv')
          }} disabled={filtered.length === 0}>Export CSV</button>
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((b) => ({
              Guest: b.guests?.full_name || '—',
              Room: b.rooms?.room_number || '—',
              'Check-in': b.check_in,
              'Check-out': b.check_out,
              Status: b.status,
              'Total (LKR)': b.total_amount,
            }))
            const statusBreakdown = ['Booked', 'Checked In', 'Checked Out', 'Cancelled'].map((status) => ({
              label: status,
              value: filtered.filter((b) => b.status === status).length,
            }))
            const trend = [...Array(7)].map((_, index) => {
              const date = new Date()
              date.setDate(date.getDate() - (6 - index))
              const key = date.toISOString().slice(0, 10)
              return {
                label: date.toLocaleDateString('en', { month: 'short', day: 'numeric' }),
                value: filtered.filter((b) => b.check_in?.slice(0, 10) === key).length,
              }
            })
            exportPDF('Booking Report', rows, 'bookings-report.pdf', {
              summary: [
                { label: 'Bookings', value: filtered.length },
                { label: 'Revenue', value: `LKR ${filtered.reduce((sum, b) => sum + Number(b.total_amount || 0), 0).toLocaleString()}` },
                { label: 'Checked in', value: filtered.filter((b) => b.status === 'Checked In').length },
              ],
              charts: [
                { type: 'pie', title: 'Status distribution', data: statusBreakdown.filter((item) => item.value > 0) },
                { type: 'line', title: 'Recent stay trend', data: trend },
              ],
            })
          }} disabled={filtered.length === 0}>Export PDF</button>
        </div>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr>
              <th>Guest</th><th>Room</th><th>Check-in</th><th>Check-out</th><th>Total (LKR)</th><th>Status</th><th className="text-right"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={7} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filtered.length === 0 && (
              <tr><td colSpan={7} className="text-center py-6 text-navy-700">No bookings found.</td></tr>
            )}
            {filtered.map((b) => (
              <tr key={b.id}>
                <td className="font-medium">{b.guests?.full_name || '—'}</td>
                <td>{b.rooms?.room_number || '—'}</td>
                <td>{b.check_in}</td>
                <td>{b.check_out}</td>
                <td className="font-mono">{Number(b.total_amount).toLocaleString()}</td>
                <td><span className={`badge ${STATUS_BADGE[b.status]}`}>{b.status}</span></td>
                <td className="text-right whitespace-nowrap">
                  <div className="flex justify-end gap-2">
                    {b.status === 'Booked' && (
                      <button
                        className="btn btn-sm btn-primary"
                        onClick={() => updateStatus(b, 'Checked In')}
                        disabled={!canCheckIn(b)}
                        title={canCheckIn(b) ? 'Check in booking' : 'Check-in is only available on or after the booking check-in date.'}
                      >
                        Check in
                      </button>
                    )}
                    {b.status === 'Checked In' && (
                      <button className="btn btn-sm btn-primary" onClick={() => updateStatus(b, 'Checked Out')}>Check out</button>
                    )}
                    {['Booked', 'Checked In'].includes(b.status) && (
                      <button className="btn btn-sm btn-danger" onClick={() => updateStatus(b, 'Cancelled')}>Cancel</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} title="New Booking" onClose={() => setModalOpen(false)} width="max-w-5xl">
        <form onSubmit={handleSubmit} className="space-y-5">
          {formError && <p className="rounded bg-rose-50 px-3 py-2 text-sm text-rose-700">{formError}</p>}
          <div className="grid gap-5 lg:grid-cols-[1.1fr_0.9fr]">
            <div className="space-y-4">
              <div className="border-b border-sand-300 pb-2">
                <h4 className="font-semibold text-navy-950">Guest details</h4>
                <p className="text-sm text-navy-700">Add a new guest here or choose an existing one.</p>
              </div>

              <div>
                <label className="label">Existing guest (optional)</label>
                <select
                  className="input"
                  value={form.guest_id}
                  onChange={(e) => {
                    const selectedGuest = guests.find((g) => g.id === e.target.value)
                    setForm({
                      ...form,
                      guest_id: e.target.value,
                      full_name: selectedGuest?.full_name || '',
                      email: selectedGuest?.email || '',
                      phone: selectedGuest?.phone || '',
                      nic: selectedGuest?.nic || '',
                      address: selectedGuest?.address || '',
                      gender: selectedGuest?.gender || form.gender,
                    })
                  }}
                >
                  <option value="">New guest</option>
                  {guests.map((g) => <option key={g.id} value={g.id}>{g.full_name}</option>)}
                </select>
              </div>

              <div className="grid md:grid-cols-2 gap-3">
                <div>
                  <label className="label">Full name</label>
                  <input required={!form.guest_id} className="input" value={form.full_name} onChange={(e) => setForm({ ...form, full_name: e.target.value })} />
                </div>
                <div>
                  <label className="label">Email</label>
                  <input type="email" required={!form.guest_id} className="input" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
                </div>
              </div>

              <div className="grid md:grid-cols-2 gap-3">
                <div>
                  <label className="label">Phone</label>
                  <input required={!form.guest_id} className="input" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
                </div>
                <div>
                  <label className="label">NIC</label>
                  <input required={!form.guest_id} className="input" value={form.nic} onChange={(e) => setForm({ ...form, nic: e.target.value })} />
                </div>
              </div>

              <div className="grid md:grid-cols-2 gap-3">
                <div>
                  <label className="label">Gender</label>
                  <select className="input" value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                    <option>Male</option>
                    <option>Female</option>
                  </select>
                </div>
                <div>
                  <label className="label">Address</label>
                  <input required={!form.guest_id} className="input" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <div className="border-b border-sand-300 pb-2">
                <h4 className="font-semibold text-navy-950">Booking details</h4>
                <p className="text-sm text-navy-700">Choose room and stay dates for this reservation.</p>
              </div>

              <div>
                <label className="label">Room (available only)</label>
                <select required className="input" value={form.room_id} onChange={(e) => handleRoomOrDateChange({ room_id: e.target.value })}>
                  <option value="">Select room…</option>
                  {availableRooms.map((r) => (
                    <option key={r.id} value={r.id}>{r.room_number} — LKR {r.price_per_night.toLocaleString()}/night</option>
                  ))}
                </select>
              </div>

              <div className="grid md:grid-cols-2 gap-3">
                <div>
                  <label className="label">Check-in</label>
                  <input type="date" required className="input" value={form.check_in} onChange={(e) => handleRoomOrDateChange({ check_in: e.target.value })} />
                </div>
                <div>
                  <label className="label">Check-out</label>
                  <input type="date" required className="input" value={form.check_out} onChange={(e) => handleRoomOrDateChange({ check_out: e.target.value })} />
                </div>
              </div>

              <div>
                <label className="label">Discount amount (LKR)</label>
                <input
                  type="number"
                  min="0"
                  className="input"
                  value={form.discount_amount}
                  onChange={(e) => handleRoomOrDateChange({ discount_amount: e.target.value })}
                />
                
              </div>

              <div>
                <label className="label">Total amount (LKR)</label>
                <input type="number" min="0" className="input" value={form.total_amount} readOnly />
                <p className="text-xs text-navy-700 mt-1">Auto-calculated from room price × nights and the discount amount.</p>
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-2 border-t border-sand-300">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary">Create booking</button>
          </div>
        </form>
      </Modal>
    </Layout>
  )
}

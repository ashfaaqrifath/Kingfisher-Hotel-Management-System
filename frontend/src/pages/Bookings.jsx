import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportCSV, exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { buildChangeSummary, logActivity } from '../lib/activityLog'
import { validateFullName, validateEmail, validatePhoneNumber } from '../lib/validation'
import { useAuth } from '../context/AuthContext'

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
  const { isAdmin } = useAuth()
  const [bookings, setBookings] = useState([])
  const [guests, setGuests] = useState([])
  const [rooms, setRooms] = useState([])
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('All')
  const [hiddenStatusFilter, setHiddenStatusFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [formError, setFormError] = useState('')
  const [loading, setLoading] = useState(true)
  const [guestSearch, setGuestSearch] = useState('')
  const [invoiceBooking, setInvoiceBooking] = useState(null)

  async function load() {
    setLoading(true)
    const [b, g, r] = await Promise.all([
      supabase.from('bookings').select('*, guests(full_name, email, phone, nic, address, gender), rooms(room_number, room_type, price_per_night)').order('created_at', { ascending: false }),
      supabase.from('guests').select('id, full_name, email, phone, nic, address, gender').order('full_name'),
      supabase.from('rooms').select('id, room_number, room_type, price_per_night, status').order('room_number'),
    ])
    setBookings(b.data || [])
    setGuests(g.data || [])
    setRooms(r.data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() {
    setForm({ ...EMPTY })
    setGuestSearch('')
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

    if (getDerivedRoomStatus(selectedRoom.id) !== 'Available') {
      return 'Please choose a room that is currently available.'
    }

    const hasConflict = bookings.some((booking) => {
      if (!booking?.room_id || booking.room_id !== form.room_id) {
        return false
      }

      if (['Cancelled', 'Checked Out'].includes(booking.status)) {
        return false
      }

      const existingCheckIn = new Date(`${booking.check_in}T00:00:00`)
      const existingCheckOut = new Date(`${booking.check_out}T00:00:00`)

      return checkIn < existingCheckOut && checkOut > existingCheckIn
    })

    if (hasConflict) {
      return 'This room is already booked for the selected check-in date or stay period. Please choose another room or date.'
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

    const roomName = rooms.find((r) => r.id === form.room_id)?.room_number || '—'
    const bookingGuestName = guests.find((g) => g.id === guestId)?.full_name || form.full_name || 'Guest'
    await logActivity('Created booking', `${bookingGuestName} • Room ${roomName} • ${form.status}`)
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

    const previousStatus = booking.status || '—'
    await supabase.from('bookings').update({ status }).eq('id', booking.id)
    await logActivity(
      `Booking ${status}`,
      `${booking.guests?.full_name || 'Guest'} • Room ${booking.rooms?.room_number || '—'} • ${previousStatus} → ${status}`
    )
    load()
  }

  async function handleDelete(booking) {
    if (!confirm(`Delete cancelled booking for ${booking.guests?.full_name || 'this guest'}?`)) {
      return
    }

    const { error } = await supabase.from('bookings').delete().eq('id', booking.id)
    if (error) {
      setFormError(`Could not delete the booking: ${error.message}`)
      return
    }

    await logActivity('Deleted booking', `${booking.guests?.full_name || 'Guest'} • Room ${booking.rooms?.room_number || '—'} • ${booking.status || '—'}`)
    setFormError('')
    load()
  }

  function openInvoice(booking) {
    setInvoiceBooking(booking)
  }

  function closeInvoice() {
    setInvoiceBooking(null)
  }

  function exportInvoicePdf() {
    if (!invoiceBooking) return

    const room = rooms.find((r) => r.id === invoiceBooking.room_id)
    const nights = nightsBetween(invoiceBooking.check_in, invoiceBooking.check_out)
    const baseTotal = Number(room?.price_per_night || 0) * nights
    const total = Number(invoiceBooking.total_amount || baseTotal || 0)
    const discount = Math.max(0, baseTotal - total)

    exportPDF('Booking Invoice', [{
      Guest: invoiceBooking.guests?.full_name || '—',
      Phone: invoiceBooking.guests?.phone || '—',
      Email: invoiceBooking.guests?.email || '—',
      NIC: invoiceBooking.guests?.nic || '—',
      Address: invoiceBooking.guests?.address || '—',
      Room: invoiceBooking.rooms?.room_number || '—',
      'Check-in': invoiceBooking.check_in,
      'Check-out': invoiceBooking.check_out,
      Status: invoiceBooking.status,
      Nights: nights,
      'Room rate / night': `LKR ${Number(room?.price_per_night || 0).toLocaleString()}`,
      'Base total': `LKR ${baseTotal.toLocaleString()}`,
      'Discount (LKR)': `LKR ${discount.toLocaleString()}`,
      'Total (LKR)': total,
    }], `invoice-${invoiceBooking.id}.pdf`, {
      summary: [
        { label: 'Guest', value: invoiceBooking.guests?.full_name || '—' },
        { label: 'Room', value: invoiceBooking.rooms?.room_number || '—' },
        { label: 'Invoice total', value: `LKR ${total.toLocaleString()}` },
      ],
    })
  }

  function getDerivedRoomStatus(roomId) {
    const room = rooms.find((r) => r.id === roomId)
    if (!room) return 'Available'
    if (room.status === 'Maintenance') return 'Maintenance'

    const now = new Date()
    now.setHours(0, 0, 0, 0)

    const activeBooking = bookings.find((booking) => {
      if (!booking.room_id || booking.room_id !== roomId) return false
      if (['Cancelled', 'Checked Out'].includes(booking.status)) return false

      const bookingCheckOut = new Date(`${booking.check_out}T00:00:00`)
      return bookingCheckOut > now
    })

    if (!activeBooking) return 'Available'
    if (activeBooking.status === 'Checked In') return 'Occupied'
    return 'Booked'
  }

  const availableRooms = rooms
    .map((room) => ({ ...room, derivedStatus: getDerivedRoomStatus(room.id) }))
    .filter((room) => room.derivedStatus === 'Available')

  const guestSearchResults = guests.filter((guest) => {
    const search = guestSearch.trim().toLowerCase()
    if (!search) return false

    return [guest.full_name, guest.email, guest.phone, guest.nic]
      .filter(Boolean)
      .some((value) => String(value).toLowerCase().includes(search))
  })

  const filtered = bookings.filter((b) => {
    const matchesSearch = `${b.guests?.full_name} ${b.rooms?.room_number}`.toLowerCase().includes(search.toLowerCase())
    const matchesStatus = statusFilter === 'All' || b.status === statusFilter
    const hidesStatus = hiddenStatusFilter === 'All' || b.status !== hiddenStatusFilter
    return matchesSearch && matchesStatus && hidesStatus
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
        <select className="input max-w-[180px]" value={hiddenStatusFilter} onChange={(e) => setHiddenStatusFilter(e.target.value)}>
          <option value="All">Hide status: All</option>
          {STATUSES.map((s) => <option key={`hide-${s}`} value={s}>Hide {s}</option>)}
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
              <th>Guest</th><th>Room</th><th>Check-in</th><th>Check-out</th><th>Status</th><th className="text-right"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={6} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filtered.length === 0 && (
              <tr><td colSpan={6} className="text-center py-6 text-navy-700">No bookings found.</td></tr>
            )}
            {filtered.map((b) => (
              <tr key={b.id}>
                <td className="font-medium">{b.guests?.full_name || '—'}</td>
                <td>{b.rooms?.room_number || '—'}</td>
                <td>{b.check_in}</td>
                <td>{b.check_out}</td>
                <td><span className={`badge ${STATUS_BADGE[b.status]}`}>{b.status}</span></td>
                <td className="text-right whitespace-nowrap">
                  <div className="flex justify-end gap-2">
                    {b.status !== 'Cancelled' && (
                      <button className="btn btn-sm btn-secondary" onClick={() => openInvoice(b)}>Invoice</button>
                    )}
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
                    {b.status === 'Cancelled' && isAdmin && (
                      <button className="btn btn-sm btn-danger" onClick={() => handleDelete(b)}>Delete</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={Boolean(invoiceBooking)} title="Booking Invoice" onClose={closeInvoice} width="max-w-2xl">
        {invoiceBooking && (
          <div className="space-y-4">
            <div className="rounded border border-sand-300 bg-sand-50 p-4">
              <div className="grid md:grid-cols-2 gap-3 text-sm text-navy-800">
                <div>
                  <p className="font-semibold text-navy-950">Guest</p>
                  <p>{invoiceBooking.guests?.full_name || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Phone</p>
                  <p>{invoiceBooking.guests?.phone || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Email</p>
                  <p>{invoiceBooking.guests?.email || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">NIC</p>
                  <p>{invoiceBooking.guests?.nic || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Address</p>
                  <p>{invoiceBooking.guests?.address || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Room</p>
                  <p>{invoiceBooking.rooms?.room_number || '—'}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Status</p>
                  <p>{invoiceBooking.status}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Check-in</p>
                  <p>{invoiceBooking.check_in}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Check-out</p>
                  <p>{invoiceBooking.check_out}</p>
                </div>
                <div>
                  <p className="font-semibold text-navy-950">Gender</p>
                  <p>{invoiceBooking.guests?.gender || '—'}</p>
                </div>
              </div>
            </div>

            <div className="rounded border border-sand-300 p-4 space-y-2 text-sm text-navy-800">
              <div className="flex justify-between">
                <span>Room rate / night</span>
                <span className="font-semibold">LKR {Number(rooms.find((r) => r.id === invoiceBooking.room_id)?.price_per_night || 0).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span>Nights</span>
                <span className="font-semibold">{nightsBetween(invoiceBooking.check_in, invoiceBooking.check_out)}</span>
              </div>
              <div className="flex justify-between">
                <span>Base total</span>
                <span className="font-semibold">LKR {Number(calcTotal(invoiceBooking.room_id, invoiceBooking.check_in, invoiceBooking.check_out) || 0).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span>Discount</span>
                <span className="font-semibold">LKR {Math.max(0, Number(calcTotal(invoiceBooking.room_id, invoiceBooking.check_in, invoiceBooking.check_out) || 0) - Number(invoiceBooking.total_amount || 0)).toLocaleString()}</span>
              </div>
              <div className="flex justify-between border-t border-sand-300 pt-2 text-base text-navy-950">
                <span className="font-semibold">Total due</span>
                <span className="font-semibold">LKR {Number(invoiceBooking.total_amount || calcTotal(invoiceBooking.room_id, invoiceBooking.check_in, invoiceBooking.check_out) || 0).toLocaleString()}</span>
              </div>
            </div>

            <div className="flex justify-end gap-2">
              <button type="button" className="btn btn-secondary" onClick={closeInvoice}>Close</button>
              <button type="button" className="btn btn-primary" onClick={exportInvoicePdf}>Export PDF</button>
            </div>
          </div>
        )}
      </Modal>

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
                <label className="label">Search existing guest</label>
                <input
                  className="input"
                  placeholder="Search by name, phone, email, or NIC"
                  value={guestSearch}
                  onChange={(e) => {
                    const nextValue = e.target.value
                    setGuestSearch(nextValue)

                    if (!nextValue.trim()) {
                      setForm({
                        ...form,
                        guest_id: '',
                        full_name: '',
                        email: '',
                        phone: '',
                        nic: '',
                        address: '',
                        gender: form.gender,
                      })
                    }
                  }}
                />

                {guestSearch.trim() && guestSearchResults.length > 0 && (
                  <div className="mt-2 rounded border border-sand-300 bg-sand-50 max-h-40 overflow-auto">
                    {guestSearchResults.map((guest) => (
                      <button
                        key={guest.id}
                        type="button"
                        className="block w-full text-left px-3 py-2 text-sm hover:bg-sand-200"
                        onClick={() => {
                          setGuestSearch('')
                          setForm({
                            ...form,
                            guest_id: guest.id,
                            full_name: guest.full_name || '',
                            email: guest.email || '',
                            phone: guest.phone || '',
                            nic: guest.nic || '',
                            address: guest.address || '',
                            gender: guest.gender || form.gender,
                          })
                        }}
                      >
                        <span className="font-medium text-navy-950">{guest.full_name}</span>
                        <span className="block text-navy-700">{guest.phone || guest.email || guest.nic || 'No contact details'}</span>
                      </button>
                    ))}
                  </div>
                )}
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
                    <option key={r.id} value={r.id}>{r.room_number} — {r.room_type || 'Room'} — LKR {Number(r.price_per_night || 0).toLocaleString()}/night</option>
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

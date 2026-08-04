import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { supabase } from '../lib/supabaseClient'
import { buildChangeSummary, logActivity } from '../lib/activityLog'

const ROOM_TYPES = ['Standard', 'Deluxe', 'Suite']
const STATUSES = ['Available', 'Booked', 'Occupied', 'Maintenance']
const EMPTY = { room_number: '', room_type: 'Standard', price_per_night: '', status: 'Available' }

const STATUS_STYLE = {
  Available: { border: 'border-moss', bg: '#c7f4c7', text: '#1f8a55' },
  Booked: { border: 'border-sky', bg: '#dbeafe', text: '#1d4fd8d1' },
  Occupied: { border: 'border-rust', bg: '#f5ba9f', text: '#b3432b' },
  Maintenance: { border: 'border-amber', bg: '#d1d1d1', text: '#06060694' },
}

export default function Rooms() {
  const [rooms, setRooms] = useState([])
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [formError, setFormError] = useState('')
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)

  async function load() {
    setLoading(true)
    const [roomsResult, bookingsResult] = await Promise.all([
      supabase.from('rooms').select('*').order('room_number'),
      supabase.from('bookings').select('id, room_id, status, check_in, check_out').order('check_in', { ascending: true }),
    ])

    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const roomsWithDerivedStatus = (roomsResult.data || []).map((room) => {
      if (room.status === 'Maintenance') {
        return { ...room, derivedStatus: 'Maintenance' }
      }

      const activeBooking = (bookingsResult.data || []).find((booking) => {
        if (!booking.room_id || booking.room_id !== room.id) return false
        if (['Cancelled', 'Checked Out'].includes(booking.status)) return false

        const bookingCheckOut = new Date(`${booking.check_out}T00:00:00`)
        return bookingCheckOut > today
      })

      if (!activeBooking) {
        return { ...room, derivedStatus: 'Available' }
      }

      if (activeBooking.status === 'Checked In') {
        return { ...room, derivedStatus: 'Occupied' }
      }

      return { ...room, derivedStatus: 'Booked' }
    })

    setRooms(roomsWithDerivedStatus)
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() {
    setForm(EMPTY)
    setFormError('')
    setEditingId(null)
    setModalOpen(true)
  }

  function openEdit(r) {
    setForm({
      ...r,
      status: r.status,
      room_number: r.room_number,
      room_type: r.room_type,
      price_per_night: r.price_per_night,
    })
    setFormError('')
    setEditingId(r.id)
    setModalOpen(true)
  }

  function validateRoomForm() {
    const roomNumber = String(form.room_number || '').trim()
    const pricePerNight = Number(form.price_per_night)

    if (!roomNumber) {
      return 'Please enter a room number.'
    }

    const duplicate = rooms.some((r) => r.id !== editingId && String(r.room_number).trim().toLowerCase() === roomNumber.toLowerCase())
    if (duplicate) {
      return 'This room number already exists. Please use a unique value.'
    }

    if (!Number.isFinite(pricePerNight) || pricePerNight <= 0) {
      return 'Price per night must be greater than 0.'
    }

    return ''
  }

  async function handleSubmit(e) {
    e.preventDefault()

    const validationMessage = validateRoomForm()
    if (validationMessage) {
      setFormError(validationMessage)
      return
    }

    const payload = {
      room_number: String(form.room_number || '').trim(),
      room_type: form.room_type,
      price_per_night: Number(form.price_per_night),
      status: form.status,
    }

    const { error } = editingId
      ? await supabase.from('rooms').update(payload).eq('id', editingId)
      : await supabase.from('rooms').insert(payload)

    if (error) {
      setFormError(`Could not save the room: ${error.message}`)
      return
    }

    if (editingId) {
      const existingRoom = rooms.find((room) => room.id === editingId)
      await logActivity(
        'Updated room',
        buildChangeSummary(payload.room_number, existingRoom || {}, payload, [
          { key: 'room_type', label: 'Type' },
          { key: 'status', label: 'Status' },
          { key: 'price_per_night', label: 'Price' },
        ])
      )
    } else {
      await logActivity('Added room', `${payload.room_number} • ${payload.room_type} • ${payload.status}`)
    }
    setFormError('')
    setModalOpen(false)
    load()
  }

  async function handleDelete(r) {
    if (!confirm(`Delete room ${r.room_number}?`)) return
    await supabase.from('rooms').delete().eq('id', r.id)
    await logActivity('Deleted room', `${r.room_number} • ${r.room_type || '—'} • ${r.status || '—'}`)
    load()
  }

  const normalizedSearch = search.toLowerCase()
  const filtered = rooms.filter(
    (r) =>
      (String(r.room_number).toLowerCase().includes(normalizedSearch) ||
        String(r.room_type).toLowerCase().includes(normalizedSearch)) &&
      (statusFilter === 'All' || r.derivedStatus === statusFilter)
  )

  return (
    <Layout
      title="Manage Rooms"
      subtitle="Room inventory, type, pricing, and live status"
      actions={<button className="btn btn-primary" onClick={openCreate}>+ Add Room</button>}
    >
      <Toolbar search={search} onSearch={setSearch} placeholder="Search room number or type…">
        <select className="input max-w-[160px]" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option>All</option>
          {STATUSES.map((s) => <option key={s}>{s}</option>)}
        </select>
      </Toolbar>

      {loading ? (
        <p className="text-navy-700">Loading…</p>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
          {filtered.map((r) => (
            <div
              key={r.id}
              className={`card border-2 border-l-4 ${STATUS_STYLE[r.derivedStatus].border} cursor-pointer transition hover:shadow-lg`}
              onClick={() => openEdit(r)}
              style={{ backgroundColor: STATUS_STYLE[r.derivedStatus].bg, color: STATUS_STYLE[r.derivedStatus].text }}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="font-display font-semibold text-lg" style={{ color: STATUS_STYLE[r.derivedStatus].text }}>{r.room_number}</span>
                <span className="badge badge-available" style={{ background: 'transparent', padding: 0, color: STATUS_STYLE[r.derivedStatus].text }}>{r.derivedStatus}</span>
              </div>
              <p className="text-xs mb-1" style={{ color: STATUS_STYLE[r.derivedStatus].text }}>{r.room_type}</p>
              <p className="font-mono text-sm" style={{ color: STATUS_STYLE[r.derivedStatus].text }}>LKR {Number(r.price_per_night).toLocaleString()}/night</p>
              <button
                className="btn btn-sm btn-danger mt-3"
                onClick={(e) => { e.stopPropagation(); handleDelete(r) }}
              >
                Delete
              </button>
            </div>
          ))}
          {filtered.length === 0 && <p className="text-navy-700 col-span-full">No rooms match your filters.</p>}
        </div>
      )}

      <Modal open={modalOpen} title={editingId ? 'Edit Room' : 'Add Room'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-4">
          {formError && <p className="rounded bg-rose-50 px-3 py-2 text-sm text-rose-700">{formError}</p>}
          <div>
            <label className="label">Room number</label>
            <input required className="input" value={form.room_number} onChange={(e) => { setForm({ ...form, room_number: e.target.value }); if (formError) setFormError('') }} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Room type</label>
              <select className="input" value={form.room_type} onChange={(e) => setForm({ ...form, room_type: e.target.value })}>
                {ROOM_TYPES.map((t) => <option key={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Price / night (LKR)</label>
              <input type="number" min="0" className="input" value={form.price_per_night} onChange={(e) => { setForm({ ...form, price_per_night: e.target.value }); if (formError) setFormError('') }} />
            </div>
          </div>
          <div>
            <label className="label">Status</label>
            <select className="input" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
              {STATUSES.map((s) => <option key={s}>{s}</option>)}
            </select>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary">{editingId ? 'Save changes' : 'Add room'}</button>
          </div>
        </form>
      </Modal>
    </Layout>
  )
}

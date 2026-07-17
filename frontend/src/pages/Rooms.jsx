import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { supabase } from '../lib/supabaseClient'
import { logActivity } from '../lib/activityLog'

const ROOM_TYPES = ['Standard', 'Deluxe', 'Suite', 'Beach Villa']
const STATUSES = ['Available', 'Occupied', 'Maintenance']
const EMPTY = { room_number: '', room_type: 'Standard', price_per_night: '', status: 'Available' }

const STATUS_STYLE = {
  Available: { border: 'border-moss', bg: '#c7f4c7', text: '#1f8a55' },
  Occupied: { border: 'border-rust', bg: '#f5ba9f', text: '#b3432b' },
  Maintenance: { border: 'border-amber', bg: '#ffedd3', text: '#c97a2b' },
}

export default function Rooms() {
  const [rooms, setRooms] = useState([])
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)

  async function load() {
    setLoading(true)
    const { data } = await supabase.from('rooms').select('*').order('room_number')
    setRooms(data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() { setForm(EMPTY); setEditingId(null); setModalOpen(true) }
  function openEdit(r) { setForm(r); setEditingId(r.id); setModalOpen(true) }

  async function handleSubmit(e) {
    e.preventDefault()
    const payload = { ...form, price_per_night: Number(form.price_per_night) || 0 }
    if (editingId) {
      await supabase.from('rooms').update(payload).eq('id', editingId)
      await logActivity('Updated room', form.room_number)
    } else {
      await supabase.from('rooms').insert(payload)
      await logActivity('Added room', form.room_number)
    }
    setModalOpen(false)
    load()
  }

  async function handleDelete(r) {
    if (!confirm(`Delete room ${r.room_number}?`)) return
    await supabase.from('rooms').delete().eq('id', r.id)
    await logActivity('Deleted room', r.room_number)
    load()
  }

  const normalizedSearch = search.toLowerCase()
  const filtered = rooms.filter(
    (r) =>
      (String(r.room_number).toLowerCase().includes(normalizedSearch) ||
        String(r.room_type).toLowerCase().includes(normalizedSearch)) &&
      (statusFilter === 'All' || r.status === statusFilter)
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
              className={`card border-2 border-l-4 ${STATUS_STYLE[r.status].border} cursor-pointer transition hover:shadow-lg`}
              onClick={() => openEdit(r)}
              style={{ backgroundColor: STATUS_STYLE[r.status].bg, color: STATUS_STYLE[r.status].text }}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="font-display font-semibold text-lg" style={{ color: STATUS_STYLE[r.status].text }}>{r.room_number}</span>
                <span className="badge badge-available" style={{ background: 'transparent', padding: 0, color: STATUS_STYLE[r.status].text }}>{r.status}</span>
              </div>
              <p className="text-xs mb-1" style={{ color: STATUS_STYLE[r.status].text }}>{r.room_type}</p>
              <p className="font-mono text-sm" style={{ color: STATUS_STYLE[r.status].text }}>LKR {Number(r.price_per_night).toLocaleString()}/night</p>
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
          <div>
            <label className="label">Room number</label>
            <input required className="input" value={form.room_number} onChange={(e) => setForm({ ...form, room_number: e.target.value })} />
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
              <input type="number" min="0" className="input" value={form.price_per_night} onChange={(e) => setForm({ ...form, price_per_night: e.target.value })} />
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

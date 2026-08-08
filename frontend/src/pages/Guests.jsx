import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { buildChangeSummary, logActivity } from '../lib/activityLog'
import { validateFullName, validateEmail, validatePhoneNumber } from '../lib/validation'

const EMPTY = { full_name: '', email: '', phone: '', nic: '', address: '', gender: 'Male' }

export default function Guests() {
  const [guests, setGuests] = useState([])
  const [search, setSearch] = useState('')
  const [genderFilter, setGenderFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    setLoading(true)
    const { data } = await supabase.from('guests').select('*').order('created_at', { ascending: false })
    setGuests(data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() {
    setForm(EMPTY)
    setEditingId(null)
    setError('')
    setModalOpen(true)
  }

  function openEdit(g) {
    setForm(g)
    setEditingId(g.id)
    setError('')
    setModalOpen(true)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    const nameValidation = validateFullName(form.full_name)
    if (!nameValidation.valid) {
      setError(nameValidation.error)
      return
    }

    const emailValidation = validateEmail(form.email)
    if (!emailValidation.valid) {
      setError(emailValidation.error)
      return
    }

    const phoneValidation = validatePhoneNumber(form.phone)
    if (!phoneValidation.valid) {
      setError(phoneValidation.error)
      return
    }

    const updatedForm = { ...form, full_name: nameValidation.value, email: emailValidation.value, phone: phoneValidation.value }
    if (editingId) {
      const existingGuest = guests.find((guest) => guest.id === editingId)
      await supabase.from('guests').update(updatedForm).eq('id', editingId)
      await logActivity(
        'Updated guest',
        buildChangeSummary(updatedForm.full_name, existingGuest || {}, updatedForm, [
          { key: 'gender', label: 'Gender' },
          { key: 'email', label: 'Email' },
          { key: 'phone', label: 'Phone' },
          { key: 'nic', label: 'NIC' },
        ])
      )
    } else {
      await supabase.from('guests').insert(updatedForm)
      await logActivity('Added guest', `${updatedForm.full_name} • Gender: ${updatedForm.gender}`)
    }
    setModalOpen(false)
    load()
  }

  async function handleDelete(g) {
    if (!confirm(`Delete guest "${g.full_name}"? This cannot be undone.`)) return
    await supabase.from('guests').delete().eq('id', g.id)
    await logActivity('Deleted guest', `${g.full_name} • Gender: ${g.gender || '—'}`)
    load()
  }

  const filtered = guests.filter((g) => {
    const haystack = `${g.full_name} ${g.email} ${g.phone} ${g.nic}`.toLowerCase()
    const matchesSearch = haystack.includes(search.toLowerCase())
    const matchesGender = genderFilter === 'All' || g.gender === genderFilter
    return matchesSearch && matchesGender
  })

  return (
    <Layout
      title="Manage Guests"
      subtitle="Guest records and contact details"
      actions={<button className="btn btn-primary" onClick={openCreate}>+ Add Guest</button>}
    >
      <Toolbar search={search} onSearch={setSearch} placeholder="Search by name, email, phone, NIC…">

        <div className="ml-auto flex gap-2">
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((g) => ({
              Name: g.full_name,
              Email: g.email || '—',
              Phone: g.phone || '—',
              NIC: g.nic || '—',
              Gender: g.gender,
            }))
            const genderBreakdown = ['Male', 'Female', 'Other'].map((gender) => ({
              label: gender,
              value: filtered.filter((g) => g.gender === gender).length,
            }))
            exportPDF('Guests Report', rows, 'guests-report.pdf', {
              summary: [
                { label: 'Guests', value: filtered.length },
                { label: 'Male', value: filtered.filter((g) => g.gender === 'Male').length },
                { label: 'Female', value: filtered.filter((g) => g.gender === 'Female').length },
              ],
              charts: [{ type: 'pie', title: 'Gender mix', data: genderBreakdown.filter((item) => item.value > 0) }],
            })
          }} disabled={filtered.length === 0}>Generate Report</button>
        </div>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th><th>Email</th><th>Phone</th><th>NIC</th><th>Gender</th><th className="text-right"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={6} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filtered.length === 0 && (
              <tr><td colSpan={6} className="text-center py-6 text-navy-700">No guests found.</td></tr>
            )}
            {filtered.map((g) => (
              <tr key={g.id}>
                <td className="font-medium">{g.full_name}</td>
                <td>{g.email || '—'}</td>
                <td>{g.phone || '—'}</td>
                <td className="font-mono text-xs">{g.nic || '—'}</td>
                <td>{g.gender}</td>
                <td className="text-right">
                  <div className="flex justify-end gap-2">
                    <button className="btn btn-sm btn-primary" onClick={() => openEdit(g)}>Edit</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(g)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} title={editingId ? 'Edit Guest' : 'Add Guest'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">{error}</div>}
          <div>
            <label className="label">Full name</label>
            <input required className="input" value={form.full_name} onChange={(e) => setForm({ ...form, full_name: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Email</label>
              <input type="email" className="input" value={form.email || ''} onChange={(e) => setForm({ ...form, email: e.target.value })} />
            </div>
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone || ''} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">NIC</label>
              <input className="input" value={form.nic || ''} onChange={(e) => setForm({ ...form, nic: e.target.value })} />
            </div>
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                <option>Male</option><option>Female</option><option>Other</option>
              </select>
            </div>
          </div>
          <div>
            <label className="label">Address</label>
            <input className="input" value={form.address || ''} onChange={(e) => setForm({ ...form, address: e.target.value })} />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary">{editingId ? 'Save changes' : 'Add guest'}</button>
          </div>
        </form>
      </Modal>
    </Layout>
  )
}

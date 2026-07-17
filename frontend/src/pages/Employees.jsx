import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportCSV, exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { logActivity } from '../lib/activityLog'

const JOB_ROLES = ['Reception', 'Housekeeping', 'Chef', 'Safari Guide', 'Manager', 'Maintenance']
const STATUSES = ['Active', 'On Leave', 'Terminated']
const EMPTY = { full_name: '', email: '', phone: '', job_role: 'Reception', salary: '', hire_date: '', status: 'Active' }

export default function Employees() {
  const [employees, setEmployees] = useState([])
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('All')
  const [statusFilter, setStatusFilter] = useState('All')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)

  async function load() {
    setLoading(true)
    const { data } = await supabase.from('employees').select('*').order('created_at', { ascending: false })
    setEmployees(data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() { setForm(EMPTY); setEditingId(null); setModalOpen(true) }
  function openEdit(e) { setForm(e); setEditingId(e.id); setModalOpen(true) }

  async function handleSubmit(e) {
    e.preventDefault()
    const payload = { ...form, salary: Number(form.salary) || 0 }
    if (editingId) {
      await supabase.from('employees').update(payload).eq('id', editingId)
      await logActivity('Updated employee', form.full_name)
    } else {
      await supabase.from('employees').insert(payload)
      await logActivity('Added employee', form.full_name)
    }
    setModalOpen(false)
    load()
  }

  async function handleDelete(emp) {
    if (!confirm(`Remove employee "${emp.full_name}"?`)) return
    await supabase.from('employees').delete().eq('id', emp.id)
    await logActivity('Deleted employee', emp.full_name)
    load()
  }

  const filtered = employees.filter((e) => {
    const haystack = `${e.full_name} ${e.job_role} ${e.email}`.toLowerCase()
    const matchesSearch = haystack.includes(search.toLowerCase())
    const matchesRole = roleFilter === 'All' || e.job_role === roleFilter
    const matchesStatus = statusFilter === 'All' || e.status === statusFilter
    return matchesSearch && matchesRole && matchesStatus
  })

  const statusBadge = (s) => ({
    Active: 'badge-available',
    'On Leave': 'badge-maintenance',
    Terminated: 'badge-occupied',
  }[s])

  return (
    <Layout
      title="Employees"
      subtitle="Staff records, roles, and salary"
      actions={<button className="btn btn-primary" onClick={openCreate}>+ Add Employee</button>}
    >
      <Toolbar search={search} onSearch={setSearch} placeholder="Search by name, role, email…">
        <select className="input max-w-[160px]" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
          <option value="All">All roles</option>
          {JOB_ROLES.map((role) => <option key={role} value={role}>{role}</option>)}
        </select>
        <select className="input max-w-[160px]" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="All">All statuses</option>
          {STATUSES.map((status) => <option key={status} value={status}>{status}</option>)}
        </select>
        <div className="ml-auto flex gap-2">
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((e) => ({
              Name: e.full_name,
              Role: e.job_role,
              Phone: e.phone || '—',
              'Salary (LKR)': e.salary,
              Hired: e.hire_date,
              Status: e.status,
            }))
            exportCSV(rows, 'employees-report.csv')
          }} disabled={filtered.length === 0}>Export CSV</button>
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((e) => ({
              Name: e.full_name,
              Role: e.job_role,
              Phone: e.phone || '—',
              'Salary (LKR)': e.salary,
              Hired: e.hire_date,
              Status: e.status,
            }))
            exportPDF('Employees Report', rows, 'employees-report.pdf')
          }} disabled={filtered.length === 0}>Export PDF</button>
        </div>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th><th>Role</th><th>Phone</th><th>Salary (LKR)</th><th>Hired</th><th>Status</th><th className="text-right"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={7} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filtered.length === 0 && (
              <tr><td colSpan={7} className="text-center py-6 text-navy-700">No employees found.</td></tr>
            )}
            {filtered.map((emp) => (
              <tr key={emp.id}>
                <td className="font-medium">{emp.full_name}</td>
                <td>{emp.job_role}</td>
                <td>{emp.phone || '—'}</td>
                <td className="font-mono">{Number(emp.salary).toLocaleString()}</td>
                <td>{emp.hire_date}</td>
                <td><span className={`badge ${statusBadge(emp.status)}`}>{emp.status}</span></td>
                <td className="text-right">
                  <div className="flex justify-end gap-2">
                    <button className="btn btn-sm btn-secondary" onClick={() => openEdit(emp)}>Edit</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(emp)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} title={editingId ? 'Edit Employee' : 'Add Employee'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-4">
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
              <label className="label">Job role</label>
              <select className="input" value={form.job_role} onChange={(e) => setForm({ ...form, job_role: e.target.value })}>
                {JOB_ROLES.map((r) => <option key={r}>{r}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Monthly salary (LKR)</label>
              <input type="number" min="0" className="input" value={form.salary} onChange={(e) => setForm({ ...form, salary: e.target.value })} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Hire date</label>
              <input type="date" className="input" value={form.hire_date || ''} onChange={(e) => setForm({ ...form, hire_date: e.target.value })} />
            </div>
            <div>
              <label className="label">Status</label>
              <select className="input" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                {STATUSES.map((s) => <option key={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary">{editingId ? 'Save changes' : 'Add employee'}</button>
          </div>
        </form>
      </Modal>
    </Layout>
  )
}

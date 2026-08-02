import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportCSV, exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { buildChangeSummary, logActivity } from '../lib/activityLog'
import { validateFullName, validateEmail, validatePhoneNumber, validateSalary } from '../lib/validation'

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
  const [error, setError] = useState('')

  async function load() {
    setLoading(true)
    const { data } = await supabase.from('employees').select('*').order('created_at', { ascending: false })
    setEmployees(data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() { setForm(EMPTY); setEditingId(null); setError(''); setModalOpen(true) }
  function openEdit(e) { setForm(e); setEditingId(e.id); setError(''); setModalOpen(true) }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    // Validate full name
    const nameValidation = validateFullName(form.full_name)
    if (!nameValidation.valid) {
      setError(nameValidation.error)
      return
    }

    // Validate email
    const emailValidation = validateEmail(form.email)
    if (!emailValidation.valid) {
      setError(emailValidation.error)
      return
    }

    // Validate phone
    const phoneValidation = validatePhoneNumber(form.phone)
    if (!phoneValidation.valid) {
      setError(phoneValidation.error)
      return
    }

    // Validate salary
    const salaryValidation = validateSalary(form.salary)
    if (!salaryValidation.valid) {
      setError(salaryValidation.error)
      return
    }

    const payload = { ...form, full_name: nameValidation.value, email: emailValidation.value, phone: phoneValidation.value, salary: salaryValidation.value }
    if (editingId) {
      const existingEmployee = employees.find((employee) => employee.id === editingId)
      await supabase.from('employees').update(payload).eq('id', editingId)
      await logActivity(
        'Updated employee',
        buildChangeSummary(payload.full_name, existingEmployee || {}, payload, [
          { key: 'job_role', label: 'Role' },
          { key: 'status', label: 'Status' },
          { key: 'salary', label: 'Salary' },
        ])
      )
    } else {
      await supabase.from('employees').insert(payload)
      await logActivity('Added employee', `${payload.full_name} • Role: ${payload.job_role}`)
    }
    setModalOpen(false)
    load()
  }

  async function handleDelete(emp) {
    if (!confirm(`Remove employee "${emp.full_name}"?`)) return
    await supabase.from('employees').delete().eq('id', emp.id)
    await logActivity('Deleted employee', `${emp.full_name} • Role: ${emp.job_role || '—'}`)
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
      title="Manage Employees"
      subtitle="Staff records, roles, and salary information."
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
            const statusBreakdown = ['Active', 'On Leave', 'Inactive'].map((status) => ({
              label: status,
              value: filtered.filter((e) => e.status === status).length,
            }))
            const roleBreakdown = filtered.reduce((acc, employee) => {
              const role = employee.job_role || 'Unassigned'
              acc[role] = (acc[role] || 0) + 1
              return acc
            }, {})
            exportPDF('Employees Report', rows, 'employees-report.pdf', {
              summary: [
                { label: 'Employees', value: filtered.length },
                { label: 'Active', value: filtered.filter((employee) => employee.status === 'Active').length },
                { label: 'Avg salary', value: `LKR ${Math.round(filtered.reduce((sum, employee) => sum + Number(employee.salary || 0), 0) / Math.max(filtered.length, 1)).toLocaleString()}` },
              ],
              charts: [
                { type: 'pie', title: 'Status mix', data: statusBreakdown.filter((item) => item.value > 0) },
                { type: 'bar', title: 'Role distribution', data: Object.entries(roleBreakdown).map(([label, value]) => ({ label, value })) },
              ],
            })
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

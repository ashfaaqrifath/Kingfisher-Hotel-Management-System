import { useEffect, useMemo, useState } from 'react'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import Layout from '../components/Layout'
import Toolbar from '../components/Toolbar'
import { supabase } from '../lib/supabaseClient'

export default function ActivityLog() {
  const [logs, setLogs] = useState([])
  const [profiles, setProfiles] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [userFilter, setUserFilter] = useState('All')

  useEffect(() => {
    async function load() {
      setLoading(true)
      const [logsRes, profilesRes] = await Promise.all([
        supabase
          .from('activity_logs')
          .select('*, profiles(full_name)')
          .order('created_at', { ascending: false })
          .limit(200),
        supabase.from('profiles').select('id, full_name')
      ])

      setLogs(logsRes.data || [])
      setProfiles(profilesRes.data || [])
      setLoading(false)
    }
    load()
  }, [])

  const userOptions = useMemo(() => ['All', ...profiles.map((p) => p.id)], [profiles])

  const filteredLogs = logs.filter((l) => {
    const haystack = `${l.profiles?.full_name || ''} ${l.action} ${l.details}`.toLowerCase()
    const matchesSearch = haystack.includes(search.toLowerCase())
    const matchesUser = userFilter === 'All' || l.user_id === userFilter
    return matchesSearch && matchesUser
  })

  function exportPDF(rows) {
    const doc = new jsPDF()
    doc.setFontSize(14)
    doc.text('Kingfisher Beach Resort — Activity Log', 14, 16)
    doc.setFontSize(10)
    doc.text(`Generated ${new Date().toLocaleString()}`, 14, 22)

    const tableBody = rows.map((r) => [
      new Date(r.created_at).toLocaleString(),
      r.profiles?.full_name || 'Unknown',
      r.action,
      r.details || '',
    ])

    autoTable(doc, {
      startY: 28,
      head: [['Timestamp', 'User', 'Action', 'Details']],
      body: tableBody,
      styles: { fontSize: 8 },
      headStyles: { fillColor: [15, 43, 70] },
      columnStyles: { 3: { cellWidth: 'wrap' } },
    })

    doc.save('activity-log.pdf')
  }

  return (
    <Layout title="Activity Log" subtitle="Audit trail of all system actions">
      <Toolbar search={search} onSearch={setSearch} placeholder="Search user, action, or details…">
        <select className="input max-w-[260px]" value={userFilter} onChange={(e) => setUserFilter(e.target.value)}>
          <option value="All">All users</option>
          {profiles.map((p) => (
            <option key={p.id} value={p.id}>{p.full_name}</option>
          ))}
        </select>
        <div className="ml-auto flex gap-2">
          <button className="btn btn-secondary" onClick={() => exportPDF(filteredLogs)} disabled={filteredLogs.length === 0}>Export PDF</button>
        </div>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr><th>Timestamp</th><th>User</th><th>Action</th><th>Details</th></tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={4} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filteredLogs.length === 0 && (
              <tr><td colSpan={4} className="text-center py-6 text-navy-700">No matching activity recorded.</td></tr>
            )}
            {filteredLogs.map((l) => (
              <tr key={l.id}>
                <td className="font-mono text-xs">{new Date(l.created_at).toLocaleString()}</td>
                <td>{l.profiles?.full_name || 'Unknown'}</td>
                <td className="font-medium">{l.action}</td>
                <td className="text-navy-700">{l.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Layout>
  )
}

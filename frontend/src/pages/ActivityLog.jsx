import { useEffect, useMemo, useState } from 'react'
import Layout from '../components/Layout'
import Toolbar from '../components/Toolbar'
import { useAuth } from '../context/AuthContext'
import { exportPDF as exportReportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'

export default function ActivityLog() {
  const { isOwner } = useAuth()
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

  async function handleDelete(log) {
    if (!isOwner) return

    if (!confirm(`Delete this activity log entry?`)) return

    const { error } = await supabase.from('activity_logs').delete().eq('id', log.id)
    if (error) {
      alert(error.message)
      return
    }

    setLogs((current) => current.filter((item) => item.id !== log.id))
  }

  function exportPDF(rows) {
    const actionBreakdown = rows.reduce((acc, row) => {
      const action = row.action || 'Other'
      acc[action] = (acc[action] || 0) + 1
      return acc
    }, {})

    const reportRows = rows.map((r) => ({
      Timestamp: new Date(r.created_at).toLocaleString(),
      User: r.profiles?.full_name || 'Unknown',
      Action: r.action,
      Details: r.details || '',
    }))

    exportReportPDF('Activity Log', reportRows, 'activity-log.pdf', {
      summary: [
        { label: 'Events', value: rows.length },
        { label: 'Users', value: new Set(rows.map((row) => row.user_id)).size },
        { label: 'Most common action', value: Object.entries(actionBreakdown).sort((a, b) => b[1] - a[1])[0]?.[0] || '—' },
      ],
    })
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
            <tr><th>Timestamp</th><th>User</th><th>Action</th><th>Details</th>{isOwner ? <th className="text-right">Delete</th> : null}</tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={isOwner ? 5 : 4} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filteredLogs.length === 0 && (
              <tr><td colSpan={isOwner ? 5 : 4} className="text-center py-6 text-navy-700">No matching activity recorded.</td></tr>
            )}
            {filteredLogs.map((l) => (
              <tr key={l.id}>
                <td className="font-mono text-xs">{new Date(l.created_at).toLocaleString()}</td>
                <td>{l.profiles?.full_name || 'Unknown'}</td>
                <td className="font-medium">{l.action}</td>
                <td className="text-navy-700">{l.details}</td>
                {isOwner ? (
                  <td className="text-right">
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(l)}>Delete</button>
                  </td>
                ) : null}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Layout>
  )
}

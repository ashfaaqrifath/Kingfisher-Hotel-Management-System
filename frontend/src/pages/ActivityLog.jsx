import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Toolbar from '../components/Toolbar'
import { supabase } from '../lib/supabaseClient'

export default function ActivityLog() {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [actionFilter, setActionFilter] = useState('All')

  useEffect(() => {
    async function load() {
      const { data } = await supabase
        .from('activity_logs')
        .select('*, profiles(full_name)')
        .order('created_at', { ascending: false })
        .limit(200)
      setLogs(data || [])
      setLoading(false)
    }
    load()
  }, [])

  const actionOptions = ['All', ...new Set(logs.map((l) => l.action).filter(Boolean))]
  const filteredLogs = logs.filter((l) => {
    const haystack = `${l.profiles?.full_name || ''} ${l.action} ${l.details}`.toLowerCase()
    const matchesSearch = haystack.includes(search.toLowerCase())
    const matchesAction = actionFilter === 'All' || l.action === actionFilter
    return matchesSearch && matchesAction
  })

  return (
    <Layout title="Activity Log" subtitle="Audit trail of all system actions (admin only)">
      <Toolbar search={search} onSearch={setSearch} placeholder="Search user, action, or details…">
        <select className="input max-w-[180px]" value={actionFilter} onChange={(e) => setActionFilter(e.target.value)}>
          {actionOptions.map((action) => (
            <option key={action} value={action}>{action === 'All' ? 'All actions' : action}</option>
          ))}
        </select>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr><th>When</th><th>User</th><th>Action</th><th>Details</th></tr>
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

import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import { supabase } from '../lib/supabaseClient'

export default function ActivityLog() {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)

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

  return (
    <Layout title="Activity Log" subtitle="Audit trail of all system actions (admin only)">
      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr><th>When</th><th>User</th><th>Action</th><th>Details</th></tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={4} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && logs.length === 0 && (
              <tr><td colSpan={4} className="text-center py-6 text-navy-700">No activity recorded yet.</td></tr>
            )}
            {logs.map((l) => (
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

import { useEffect, useMemo, useState } from 'react'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import Layout from '../components/Layout'
import { supabase } from '../lib/supabaseClient'

function toCSV(rows, headers) {
  const escape = (v) => `"${String(v ?? '').replace(/"/g, '""')}"`
  const lines = [headers.map(escape).join(',')]
  rows.forEach((r) => lines.push(headers.map((h) => escape(r[h])).join(',')))
  return lines.join('\n')
}

function downloadFile(content, filename, mime) {
  const blob = new Blob([content], { type: mime })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

export default function Reports() {
  const [tab, setTab] = useState('bookings')
  const [bookings, setBookings] = useState([])
  const [inventory, setInventory] = useState([])
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')

  useEffect(() => {
    async function load() {
      const [b, i] = await Promise.all([
        supabase.from('bookings').select('*, guests(full_name), rooms(room_number)').order('check_in'),
        supabase.from('inventory_items').select('*').order('item_name'),
      ])
      setBookings(b.data || [])
      setInventory(i.data || [])
    }
    load()
  }, [])

  const filteredBookings = useMemo(() => {
    return bookings.filter((b) => {
      if (from && b.check_in < from) return false
      if (to && b.check_in > to) return false
      return true
    })
  }, [bookings, from, to])

  const bookingRows = filteredBookings.map((b) => ({
    Guest: b.guests?.full_name || '—',
    Room: b.rooms?.room_number || '—',
    'Check-in': b.check_in,
    'Check-out': b.check_out,
    Status: b.status,
    'Total (LKR)': b.total_amount,
  }))

  const inventoryRows = inventory.map((i) => ({
    Item: i.item_name,
    Category: i.category,
    Quantity: `${i.quantity} ${i.unit}`,
    'Low Stock Threshold': i.low_stock_threshold,
    'Unit Price (LKR)': i.unit_price,
  }))

  const rows = tab === 'bookings' ? bookingRows : inventoryRows
  const totalRevenue = filteredBookings.reduce((s, b) => s + Number(b.total_amount || 0), 0)

  function exportCSV() {
    const headers = Object.keys(rows[0] || {})
    downloadFile(toCSV(rows, headers), `${tab}-report.csv`, 'text/csv')
  }

  function exportPDF() {
    const doc = new jsPDF()
    doc.setFontSize(14)
    doc.text('Kingfisher Beach Resort', 14, 16)
    doc.setFontSize(10)
    doc.text(`${tab === 'bookings' ? 'Booking' : 'Inventory'} Report — generated ${new Date().toLocaleDateString()}`, 14, 22)
    autoTable(doc, {
      startY: 28,
      head: [Object.keys(rows[0] || {})],
      body: rows.map((r) => Object.values(r)),
      styles: { fontSize: 8 },
      headStyles: { fillColor: [15, 43, 70] },
    })
    doc.save(`${tab}-report.pdf`)
  }

  return (
    <Layout title="Reports" subtitle="Generate and export operational reports">
      <div className="flex gap-2 mb-5">
        <button className={`btn ${tab === 'bookings' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setTab('bookings')}>Bookings</button>
        <button className={`btn ${tab === 'inventory' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setTab('inventory')}>Inventory</button>
      </div>

      {tab === 'bookings' && (
        <div className="flex flex-wrap items-end gap-3 mb-5">
          <div>
            <label className="label">From (check-in)</label>
            <input type="date" className="input" value={from} onChange={(e) => setFrom(e.target.value)} />
          </div>
          <div>
            <label className="label">To (check-in)</label>
            <input type="date" className="input" value={to} onChange={(e) => setTo(e.target.value)} />
          </div>
          <div className="card !p-3 ml-auto">
            <p className="text-xs text-navy-700 uppercase">Total revenue in range</p>
            <p className="font-mono font-semibold text-lg">LKR {totalRevenue.toLocaleString()}</p>
          </div>
        </div>
      )}

      <div className="flex gap-2 mb-4">
        <button className="btn btn-secondary" onClick={exportCSV} disabled={rows.length === 0}>Export CSV</button>
        <button className="btn btn-secondary" onClick={exportPDF} disabled={rows.length === 0}>Export PDF</button>
      </div>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr>{Object.keys(rows[0] || {}).map((h) => <th key={h}>{h}</th>)}</tr>
          </thead>
          <tbody>
            {rows.length === 0 && (
              <tr><td className="text-center py-6 text-navy-700">No data for this selection.</td></tr>
            )}
            {rows.map((r, idx) => (
              <tr key={idx}>
                {Object.values(r).map((v, i) => <td key={i}>{String(v)}</td>)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Layout>
  )
}

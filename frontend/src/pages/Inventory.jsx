import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { exportCSV, exportPDF } from '../lib/reportUtils'
import { supabase } from '../lib/supabaseClient'
import { buildChangeSummary, logActivity } from '../lib/activityLog'

const CATEGORIES = ['Linen', 'Toiletries', 'Food & Beverage', 'Kitchen', 'Maintenance', 'Office']
const EMPTY = { item_name: '', category: 'Linen', quantity: '', unit: 'pcs', low_stock_threshold: 10, unit_price: '' }

export default function Inventory() {
  const [items, setItems] = useState([])
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('All')
  const [lowOnly, setLowOnly] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)

  async function load() {
    setLoading(true)
    const { data } = await supabase.from('inventory_items').select('*').order('item_name')
    setItems(data || [])
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  function openCreate() { setForm(EMPTY); setEditingId(null); setModalOpen(true) }
  function openEdit(i) { setForm(i); setEditingId(i.id); setModalOpen(true) }

  async function handleSubmit(e) {
    e.preventDefault()
    const payload = {
      ...form,
      quantity: Number(form.quantity) || 0,
      low_stock_threshold: Number(form.low_stock_threshold) || 0,
      unit_price: Number(form.unit_price) || 0,
      updated_at: new Date().toISOString(),
    }
    if (editingId) {
      const existingItem = items.find((item) => item.id === editingId)
      await supabase.from('inventory_items').update(payload).eq('id', editingId)
      await logActivity(
        'Updated inventory item',
        buildChangeSummary(payload.item_name, existingItem || {}, payload, [
          { key: 'category', label: 'Category' },
          { key: 'quantity', label: 'Quantity' },
          { key: 'unit_price', label: 'Unit Price' },
        ])
      )
    } else {
      await supabase.from('inventory_items').insert(payload)
      await logActivity('Added inventory item', `${payload.item_name} • ${payload.category} • Qty: ${payload.quantity}`)
    }
    setModalOpen(false)
    load()
  }

  async function handleDelete(item) {
    if (!confirm(`Remove "${item.item_name}" from inventory?`)) return
    await supabase.from('inventory_items').delete().eq('id', item.id)
    await logActivity('Deleted inventory item', `${item.item_name} • ${item.category || '—'} • Qty: ${item.quantity || 0}`)
    load()
  }

  const filtered = items
    .filter((i) => `${i.item_name} ${i.category}`.toLowerCase().includes(search.toLowerCase()))
    .filter((i) => categoryFilter === 'All' || i.category === categoryFilter)
    .filter((i) => !lowOnly || i.quantity <= i.low_stock_threshold)

  return (
    <Layout
      title="Manage Inventory"
      subtitle="Supplies, stock levels, and low-stock alerts"
      actions={<button className="btn btn-primary" onClick={openCreate}>+ Add Item</button>}
    >
      <Toolbar search={search} onSearch={setSearch} placeholder="Search item or category…">
        <select className="input max-w-[160px]" value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
          <option value="All">All categories</option>
          {CATEGORIES.map((category) => <option key={category} value={category}>{category}</option>)}
        </select>
        <label className="flex items-center gap-2 text-sm text-navy-700">
          <input type="checkbox" checked={lowOnly} onChange={(e) => setLowOnly(e.target.checked)} />
          Low stock only
        </label>
        <div className="ml-auto flex gap-2">
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((i) => ({
              Item: i.item_name,
              Category: i.category,
              Quantity: `${i.quantity} ${i.unit}`,
              'Unit Price (LKR)': i.unit_price,
              Status: i.quantity <= i.low_stock_threshold ? 'Low stock' : 'In stock',
            }))
            exportCSV(rows, 'inventory-report.csv')
          }} disabled={filtered.length === 0}>Export CSV</button>
          <button className="btn btn-secondary" onClick={() => {
            const rows = filtered.map((i) => ({
              Item: i.item_name,
              Category: i.category,
              Quantity: `${i.quantity} ${i.unit}`,
              'Unit Price (LKR)': i.unit_price,
              Status: i.quantity <= i.low_stock_threshold ? 'Low stock' : 'In stock',
            }))
            const statusBreakdown = [
              { label: 'Low stock', value: filtered.filter((i) => i.quantity <= i.low_stock_threshold).length },
              { label: 'Healthy', value: filtered.filter((i) => i.quantity > i.low_stock_threshold).length },
            ]
            exportPDF('Inventory Report', rows, 'inventory-report.pdf', {
              summary: [
                { label: 'Items', value: filtered.length },
                { label: 'Low stock', value: statusBreakdown[0].value },
                { label: 'Total value', value: `LKR ${filtered.reduce((sum, item) => sum + Number(item.unit_price || 0) * Number(item.quantity || 0), 0).toLocaleString()}` },
              ],
              charts: [{ type: 'pie', title: 'Stock health', data: statusBreakdown.filter((item) => item.value > 0) }],
            })
          }} disabled={filtered.length === 0}>Export PDF</button>
        </div>
      </Toolbar>

      <div className="card overflow-x-auto p-0">
        <table className="data-table">
          <thead>
            <tr>
              <th>Item</th><th>Category</th><th>Quantity</th><th>Unit price (LKR)</th><th>Status</th><th className="text-right"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={6} className="text-center py-6 text-navy-700">Loading…</td></tr>}
            {!loading && filtered.length === 0 && (
              <tr><td colSpan={6} className="text-center py-6 text-navy-700">No items found.</td></tr>
            )}
            {filtered.map((i) => {
              const low = i.quantity <= i.low_stock_threshold
              return (
                <tr key={i.id}>
                  <td className="font-medium">{i.item_name}</td>
                  <td>{i.category}</td>
                  <td className="font-mono">{i.quantity} {i.unit}</td>
                  <td className="font-mono">{Number(i.unit_price).toLocaleString()}</td>
                  <td>
                    <span className={`badge ${low ? 'badge-occupied' : 'badge-available'}`}>
                      {low ? 'Low stock' : 'In stock'}
                    </span>
                  </td>
                  <td className="text-right">
                    <div className="flex justify-end gap-2">
                      <button className="btn btn-sm btn-secondary" onClick={() => openEdit(i)}>Edit</button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleDelete(i)}>Delete</button>
                    </div>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} title={editingId ? 'Edit Item' : 'Add Item'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Item name</label>
            <input required className="input" value={form.item_name} onChange={(e) => setForm({ ...form, item_name: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Category</label>
              <select className="input" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
                {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Unit (pcs, kg, reams…)</label>
              <input className="input" value={form.unit} onChange={(e) => setForm({ ...form, unit: e.target.value })} />
            </div>
          </div>
          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="label">Quantity</label>
              <input type="number" min="0" className="input" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} />
            </div>
            <div>
              <label className="label">Low-stock alert at</label>
              <input type="number" min="0" className="input" value={form.low_stock_threshold} onChange={(e) => setForm({ ...form, low_stock_threshold: e.target.value })} />
            </div>
            <div>
              <label className="label">Unit price (LKR)</label>
              <input type="number" min="0" className="input" value={form.unit_price} onChange={(e) => setForm({ ...form, unit_price: e.target.value })} />
            </div>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary">{editingId ? 'Save changes' : 'Add item'}</button>
          </div>
        </form>
      </Modal>
    </Layout>
  )
}

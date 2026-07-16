import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

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

export function exportCSV(rows, filename = 'report.csv') {
  if (!rows || rows.length === 0) return
  const headers = Object.keys(rows[0] || {})
  const csv = toCSV(rows, headers)
  downloadFile(csv, filename, 'text/csv')
}

export function exportPDF(title, rows, filename = 'report.pdf') {
  if (!rows || rows.length === 0) return
  const doc = new jsPDF()
  doc.setFontSize(14)
  doc.text('Kingfisher Beach Resort', 14, 16)
  doc.setFontSize(10)
  doc.text(`${title} — generated ${new Date().toLocaleDateString()}`, 14, 22)
  autoTable(doc, {
    startY: 28,
    head: [Object.keys(rows[0] || {})],
    body: rows.map((r) => Object.values(r)),
    styles: { fontSize: 8 },
    headStyles: { fillColor: [15, 43, 70] },
  })
  doc.save(filename)
}

import { supabase } from './supabaseClient'

function normalizeActivityValue(value) {
  if (value === null || value === undefined || value === '') return '—'
  return String(value).trim()
}

export function buildChangeSummary(entityName, before = {}, after = {}, fieldMap = []) {
  const changes = fieldMap
    .filter(({ key }) => normalizeActivityValue(before[key]) !== normalizeActivityValue(after[key]))
    .map(({ key, label }) => `${label}: ${normalizeActivityValue(after[key])}`)

  const summary = [entityName, ...changes.slice(0, 2)]
  return summary.filter(Boolean).join(' • ')
}

export async function logActivity(action, details = '') {
  const { data: { user } } = await supabase.auth.getUser()
  if (!user) return
  await supabase.from('activity_logs').insert({ user_id: user.id, action, details })
}

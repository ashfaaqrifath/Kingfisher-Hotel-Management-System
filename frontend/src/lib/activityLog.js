import { supabase } from './supabaseClient'

export async function logActivity(action, details = '') {
  const { data: { user } } = await supabase.auth.getUser()
  if (!user) return
  await supabase.from('activity_logs').insert({ user_id: user.id, action, details })
}

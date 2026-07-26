import { createClient } from '@supabase/supabase-js'

const SUPABASE_URL = import.meta.env.VITE_SUPABASE_URL
const SUPABASE_ANON_KEY = import.meta.env.VITE_SUPABASE_ANON_KEY

export const ADMIN_EMAIL = import.meta.env.VITE_TEST_ADMIN_EMAIL
export const ADMIN_PASSWORD = import.meta.env.VITE_TEST_ADMIN_PASSWORD
export const STAFF_EMAIL = import.meta.env.VITE_TEST_STAFF_EMAIL
export const STAFF_PASSWORD = import.meta.env.VITE_TEST_STAFF_PASSWORD

export const TEST_PREFIX = 'TEST_'

export function freshClient() {
  return createClient(SUPABASE_URL, SUPABASE_ANON_KEY, {
    auth: { persistSession: false, autoRefreshToken: false, detectSessionInUrl: false },
  })
}

export async function adminClient() {
  const client = freshClient()
  const { error } = await client.auth.signInWithPassword({ email: ADMIN_EMAIL, password: ADMIN_PASSWORD })
  if (error) throw new Error(`Admin login failed in test helper: ${error.message}`)
  return client
}

export async function staffClient() {
  const client = freshClient()
  const { error } = await client.auth.signInWithPassword({ email: STAFF_EMAIL, password: STAFF_PASSWORD })
  if (error) throw new Error(`Staff login failed in test helper: ${error.message}`)
  return client
}

export function makeCleanup() {
  const rows = []
  return {
    track(table, id) {
      rows.push({ table, id })
    },
    async run(client) {
      for (const { table, id } of rows.reverse()) {
        await client.from(table).delete().eq('id', id)
      }
    },
  }
}

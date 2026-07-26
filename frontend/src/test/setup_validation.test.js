import { describe, it, expect } from 'vitest'
import { execSync } from 'node:child_process'
import { existsSync } from 'node:fs'
import { resolve } from 'node:path'
import { adminClient } from './helpers'

// TC-S1-01..05

const REPO_ROOT = resolve(__dirname, '../../..')
const FRONTEND_ROOT = resolve(__dirname, '../..')

describe('Setup & environment validation', () => {
  it('TC-S1-01: documented core modules exist as pages in the codebase', () => {
    const requiredPages = [
      'Login.jsx', 'Dashboard.jsx', 'Guests.jsx', 'Employees.jsx',
      'Rooms.jsx', 'Bookings.jsx', 'Inventory.jsx', 'ActivityLog.jsx', 'Users.jsx',
    ]
    const missing = requiredPages.filter((p) => !existsSync(resolve(FRONTEND_ROOT, 'src/pages', p)))
    expect(missing).toEqual([])
  })

  it('TC-S1-02: the GitHub repo remote is configured and reachable', () => {
    const remote = execSync('git remote get-url origin', { cwd: REPO_ROOT }).toString().trim()
    expect(remote).toContain('github.com')

    // ls-remote confirms the remote is actually reachable over the network,
    // not just configured locally.
    const lsRemote = execSync(`git ls-remote --heads "${remote}"`, { cwd: REPO_ROOT }).toString()
    expect(lsRemote.length).toBeGreaterThan(0)

    const branch = execSync('git rev-parse --abbrev-ref HEAD', { cwd: REPO_ROOT }).toString().trim()
    expect(branch.length).toBeGreaterThan(0)
    // Note: this repo has a single working branch (master) with no
    // documented feature/develop branching strategy beyond that.
  })

  it('TC-S1-03: dependencies are installed and the app builds without errors', () => {
    expect(existsSync(resolve(FRONTEND_ROOT, 'node_modules/.bin/vite'))).toBe(true)

    const output = execSync('npx vite build --mode test', { cwd: FRONTEND_ROOT }).toString()
    expect(output).toContain('built in')
  }, 60000)

  it('TC-S1-04: supabaseClient.js establishes a working connection', async () => {
    const client = await adminClient()
    const { error } = await client.from('rooms').select('id').limit(1)
    expect(error).toBeNull()
  })

  it('TC-S1-05: all required tables exist in the connected Supabase project', async () => {
    const client = await adminClient()
    const tables = ['guests', 'employees', 'rooms', 'bookings', 'inventory_items', 'profiles', 'activity_logs']
    const results = {}
    for (const table of tables) {
      const { error } = await client.from(table).select('id').limit(1)
      results[table] = error ? error.message : 'ok'
    }
    for (const table of tables) {
      expect(results[table], `table "${table}": ${results[table]}`).toBe('ok')
    }
  })
})

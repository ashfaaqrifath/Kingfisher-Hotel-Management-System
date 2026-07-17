import { useEffect, useMemo, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { useAuth } from '../context/AuthContext'
import { logActivity } from '../lib/activityLog'
import { supabase } from '../lib/supabaseClient'

const EMPTY = { full_name: '', email: '', password: '', role: 'staff' }
const ROLE_OPTIONS = ['owner', 'admin', 'staff']

export default function Users() {
    const { user } = useAuth()
    const [profiles, setProfiles] = useState([])
    const [search, setSearch] = useState('')
    const [roleFilter, setRoleFilter] = useState('All')
    const [modalOpen, setModalOpen] = useState(false)
    const [form, setForm] = useState(EMPTY)
    const [editingId, setEditingId] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    async function load() {
        setLoading(true)
        const { data, error } = await supabase
            .from('profiles')
            .select('id, full_name, email, role, created_at')
            .order('created_at', { ascending: false })

        if (!error) setProfiles(data || [])
        setLoading(false)
    }

    useEffect(() => {
        void load()
    }, [])

    const ownerCount = useMemo(() => profiles.filter((profile) => profile.role === 'owner').length, [profiles])

    function openCreate() {
        setError('')
        setForm(EMPTY)
        setEditingId(null)
        setModalOpen(true)
    }

    function openEdit(profile) {
        setError('')
        setForm({
            auth_user_id: profile.id,
            full_name: profile.full_name || '',
            email: profile.email || '',
            role: profile.role || 'staff',
        })
        setEditingId(profile.id)
        setModalOpen(true)
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setError('')

        const fullName = form.full_name.trim()
        const email = form.email.trim()
        const role = form.role

        if (!fullName) {
            setError('Please enter the person\'s full name.')
            return
        }
        if (!email) {
            setError('Please enter an email address.')
            return
        }
        if (!editingId && !form.password.trim()) {
            setError('Please provide a password for the new user.')
            return
        }

        const existingProfile = profiles.find((profile) => profile.id === editingId)
        const assigningOwner = role === 'owner' && (!existingProfile || existingProfile.role !== 'owner')
        if (assigningOwner && ownerCount > 0) {
            setError('Only one owner account is allowed. Change the current owner back first if you need to assign this role.')
            return
        }

        try {
            if (editingId) {
                const { error } = await supabase
                    .from('profiles')
                    .update({ full_name: fullName, email, role })
                    .eq('id', editingId)

                if (error) throw error
                await logActivity('Updated user profile', `${fullName} (${role})`)
            } else {
                const { data: authData, error: authError } = await supabase.auth.signUp({
                    email,
                    password: form.password,
                    options: {
                        data: {
                            full_name: fullName,
                        },
                    },
                })

                if (authError) throw authError
                if (!authData?.user?.id) throw new Error('The account could not be created in Supabase Auth.')

                const { error: profileError } = await supabase.from('profiles').upsert({
                    id: authData.user.id,
                    full_name: fullName,
                    email,
                    role,
                }, { onConflict: 'id' })

                if (profileError) throw profileError
                await logActivity('Created user profile', `${fullName} (${role})`)
            }

            setModalOpen(false)
            await load()
        } catch (err) {
            setError(err.message || 'Unable to save this profile right now.')
        }
    }

    async function handleDelete(profile) {
        if (profile.id === user?.id) {
            alert('You cannot delete your own account from this page.')
            return
        }
        if (profile.role === 'owner') {
            alert('The owner account cannot be deleted from here.')
            return
        }
        if (!confirm(`Delete profile for ${profile.full_name}?`)) return

        const { error } = await supabase.from('profiles').delete().eq('id', profile.id)
        if (error) {
            alert(error.message)
            return
        }

        await logActivity('Deleted user profile', profile.full_name)
        await load()
    }

    const filtered = profiles.filter((profile) => {
        const haystack = `${profile.full_name || ''} ${profile.email || ''} ${profile.role || ''}`.toLowerCase()
        const matchesSearch = haystack.includes(search.toLowerCase())
        const matchesRole = roleFilter === 'All' || profile.role === roleFilter
        return matchesSearch && matchesRole
    })

    return (
        <Layout
            title="Users"
            subtitle="Manage hotel staff accounts, roles, and profile access"
            actions={
                <button className="btn btn-primary" onClick={openCreate}>
                    + Add User
                </button>
            }
        >
            <Toolbar search={search} onSearch={setSearch} placeholder="Search by name, email, role…">
                <select className="input max-w-[150px]" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
                    <option value="All">All roles</option>
                    {ROLE_OPTIONS.map((role) => (
                        <option key={role} value={role}>
                            {role}
                        </option>
                    ))}
                </select>
            </Toolbar>

            <div className="mb-4 rounded-xl border border-sand-300 bg-white px-4 py-3 text-sm text-navy-700">
                <p className="font-medium text-navy-950">Owner-only workflow</p>
                <p className="mt-1">
                    Create the first owner account once in Supabase Auth.
                </p>
            </div>

            <div className="card overflow-x-auto p-0">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Supabase user ID</th>
                            <th className="text-right"></th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading && (
                            <tr>
                                <td colSpan={5} className="text-center py-6 text-navy-700">
                                    Loading…
                                </td>
                            </tr>
                        )}
                        {!loading && filtered.length === 0 && (
                            <tr>
                                <td colSpan={5} className="text-center py-6 text-navy-700">
                                    No profiles found.
                                </td>
                            </tr>
                        )}
                        {filtered.map((profile) => (
                            <tr key={profile.id}>
                                <td className="font-medium">{profile.full_name}</td>
                                <td>{profile.email || '—'}</td>
                                <td className="capitalize">{profile.role}</td>
                                <td className="font-mono text-xs">{profile.id}</td>
                                <td className="text-right">
                                    <div className="flex justify-end gap-2">
                                        <button className="btn btn-sm btn-secondary" onClick={() => openEdit(profile)}>
                                            Edit
                                        </button>
                                        <button className="btn btn-sm btn-danger" onClick={() => handleDelete(profile)}>
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <Modal open={modalOpen} title={editingId ? 'Edit user profile' : 'Add user profile'} onClose={() => setModalOpen(false)}>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="rounded border border-rust/30 bg-rust/10 px-3 py-2 text-sm text-rust">
                            {error}
                        </div>
                    )}

                    {!editingId && (
                        <div className="grid gap-3 md:grid-cols-2">
                            <div>
                                <label className="label">Create Password</label>
                                <input
                                    required
                                    type="password"
                                    className="input"
                                    value={form.password}
                                    onChange={(e) => setForm({ ...form, password: e.target.value })}
                                    placeholder="Create a secure password"
                                />
                            </div>
                            
                        </div>
                    )}

                    <div>
                        <label className="label">Full name</label>
                        <input
                            required
                            className="input"
                            value={form.full_name}
                            onChange={(e) => setForm({ ...form, full_name: e.target.value })}
                        />
                    </div>

                    <div>
                        <label className="label">Email</label>
                        <input
                            type="email"
                            required
                            className="input"
                            value={form.email}
                            onChange={(e) => setForm({ ...form, email: e.target.value })}
                        />
                    </div>

                    <div>
                        <label className="label">Role</label>
                        <select className="input" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
                            {ROLE_OPTIONS.map((role) => (
                                <option key={role} value={role}>
                                    {role}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="flex justify-end gap-2 pt-2">
                        <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>
                            Cancel
                        </button>
                        <button type="submit" className="btn btn-primary">
                            {editingId ? 'Save changes' : 'Create profile'}
                        </button>
                    </div>
                </form>
            </Modal>
        </Layout>
    )
}

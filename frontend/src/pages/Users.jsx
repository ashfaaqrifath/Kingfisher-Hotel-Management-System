import { useEffect, useMemo, useState } from 'react'
import Layout from '../components/Layout'
import Modal from '../components/Modal'
import Toolbar from '../components/Toolbar'
import { useAuth } from '../context/AuthContext'
import { logActivity } from '../lib/activityLog'
import { supabase } from '../lib/supabaseClient'
import { validateFullName, validateEmail } from '../lib/validation'

const EMPTY = { full_name: '', email: '', password: '', role: 'staff' }
const ROLE_OPTIONS = ['owner', 'admin', 'staff']

export default function Users() {
    const { user, isOwner, isAdmin } = useAuth()
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
        if (!isAdmin) return
        setError('')
        setForm({ full_name: '', email: '', password: '', role: 'staff' })
        setEditingId(null)
        setModalOpen(true)
    }

    function openEdit(profile) {
        if (!isOwner) return
        setError('')
        setForm({
            full_name: profile.full_name || '',
            email: profile.email || '',
            password: '',
            role: profile.role || 'staff',
        })
        setEditingId(profile.id)
        setModalOpen(true)
    }

    async function handleSubmit(e) {
        e.preventDefault()
        if (editingId && !isOwner) {
            setError('Only the owner can edit user profiles.')
            return
        }
        if (!editingId && !isAdmin) {
            setError('Only admins and the owner can add user profiles.')
            return
        }

        // Validate full name
        const nameValidation = validateFullName(form.full_name)
        if (!nameValidation.valid) {
            setError(nameValidation.error)
            return
        }

        // Validate email
        const emailValidation = validateEmail(form.email)
        if (!emailValidation.valid) {
            setError(emailValidation.error)
            return
        }

        const fullName = nameValidation.value
        const email = emailValidation.value
        const role = form.role

        if (!editingId && !form.password.trim()) {
            setError('Please provide a password for the new user.')
            return
        }

        if (!editingId && role === 'owner' && !isOwner) {
            setError('Only the owner can create owner accounts.')
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
        if (!isOwner) {
            alert('Only the owner can delete user profiles.')
            return
        }
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
            title="System Users"
            subtitle={isOwner ? 'Manage hotel staff accounts, roles, and profile access' : 'View user profiles and roles'}
            actions={isAdmin ? <button className="btn btn-primary" onClick={openCreate}>+ Add User</button> : null}
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
                <p className="font-medium text-navy-950">{isOwner ? 'Owner workflow' : isAdmin ? 'Admin workflow' : 'View-only access'}</p>
                <p className="mt-1">
                    {isOwner
                        ? 'Owners can create, edit, and manage user roles from this page. The first owner account is created once in Supabase Auth.'
                        : isAdmin
                            ? 'Admins can add new user profiles from this page, but only the owner can edit existing profiles.'
                            : 'Admins can view the profiles table here, but only the owner can make changes.'}
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
                                <td colSpan={5} className="text-center py-6 text-navy-700">Loading…</td>
                            </tr>
                        )}
                        {!loading && filtered.length === 0 && (
                            <tr>
                                <td colSpan={5} className="text-center py-6 text-navy-700">No profiles found.</td>
                            </tr>
                        )}
                        {filtered.map((profile) => (
                            <tr key={profile.id}>
                                <td className="font-medium">{profile.full_name}</td>
                                <td>{profile.email || '—'}</td>
                                <td className="capitalize">{profile.role}</td>
                                <td className="font-mono text-xs">{profile.id}</td>
                                <td className="text-right">
                                    {isOwner ? (
                                        <div className="flex justify-end gap-2">
                                            <button className="btn btn-sm btn-secondary" onClick={() => openEdit(profile)}>
                                                Edit
                                            </button>
                                            <button className="btn btn-sm btn-danger" onClick={() => handleDelete(profile)}>
                                                Delete
                                            </button>
                                        </div>
                                    ) : (
                                        <span className="text-xs text-navy-700">Read only</span>
                                    )}
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

                    {!editingId && (
                        <div>
                            <label className="label">Create password</label>
                            <input
                                required
                                type="text"
                                className="input"
                                value={form.password}
                                onChange={(e) => setForm({ ...form, password: e.target.value })}
                                placeholder="Create a secure password"
                            />
                        </div>
                    )}

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

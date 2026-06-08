import { useCallback, useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useAlert } from '../context/AlertContext'
import { tenantsApi } from '../api/tenants'
import { TenantDto } from '../models/dto/TenantDto'
import { PermissionRole } from '../models/dto/PermissionsDto'

function MiscPage() {
    const { permissions } = useAuth()
    const { success, error } = useAlert()

    const [tenants, setTenants] = useState<TenantDto[]>([])
    const [loading, setLoading] = useState(true)

    const [createName, setCreateName] = useState('')
    const [createCode, setCreateCode] = useState('')
    const [creating, setCreating] = useState(false)

    const [joinId, setJoinId] = useState('')
    const [joining, setJoining] = useState(false)

    const loadTenants = useCallback(async () => {
        try {
            setTenants(await tenantsApi.getLinked())
        } catch {
            error('Failed to load tenants')
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { loadTenants() }, [loadTenants])

    const handleCreate = async (e: { preventDefault(): void }) => {
        e.preventDefault()
        setCreating(true)
        try {
            await tenantsApi.create(createName.trim(), createCode.trim())
            success('Tenant created')
            setCreateName('')
            setCreateCode('')
            await loadTenants()
        } catch {
            error('Failed to create tenant')
        } finally {
            setCreating(false)
        }
    }

    const handleJoin = async (e: { preventDefault(): void }) => {
        e.preventDefault()
        setJoining(true)
        try {
            await tenantsApi.join(parseInt(joinId))
            success('Join request sent')
            setJoinId('')
            await loadTenants()
        } catch {
            error('Failed to join tenant')
        } finally {
            setJoining(false)
        }
    }

    const getRole = (tenantId: number): PermissionRole | undefined =>
        permissions?.find(p => p.tenantId === tenantId)?.role

    function roleBadge(role: PermissionRole | undefined) {
        switch (role) {
            case PermissionRole.Admin: return <span className="badge badge--purple">Admin</span>
            case PermissionRole.User:  return <span className="badge badge--green">Member</span>
            default:                   return <span className="badge badge--yellow">Awaiting Approval</span>
        }
    }

    return (
        <div className="page">
            <h2 className="page-title">Tenants</h2>
            <div className="page-layout">
                <div className="card">
                    <p className="card-title">Your Tenants</p>
                    {loading ? (
                        <p className="empty-state">Loading...</p>
                    ) : tenants.length === 0 ? (
                        <p className="empty-state">You're not linked to any tenants yet.</p>
                    ) : (
                        tenants.map(t => (
                            <div key={t.id} className="tenant-row">
                                <div className="tenant-row-info">
                                    <span className="tenant-row-name">{t.name}</span>
                                    <span className="tenant-row-code">{t.code}</span>
                                </div>
                                <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                                    {roleBadge(getRole(t.id))}
                                </div>
                            </div>
                        ))
                    )}
                </div>

                <div className="sidebar">
                    <div className="card">
                        <p className="card-title">Create Tenant</p>
                        <form className="form" onSubmit={handleCreate}>
                            <div className="form-field">
                                <label className="form-label">Name</label>
                                <input
                                    className="form-input"
                                    value={createName}
                                    onChange={e => setCreateName(e.target.value)}
                                    placeholder="My Organization"
                                />
                            </div>
                            <div className="form-field">
                                <label className="form-label">Code</label>
                                <input
                                    className="form-input"
                                    value={createCode}
                                    onChange={e => setCreateCode(e.target.value)}
                                    placeholder="MY_ORG"
                                />
                            </div>
                            <button
                                type="submit"
                                className="btn btn--primary"
                                disabled={creating || !createName.trim() || !createCode.trim()}
                            >
                                {creating ? 'Creating...' : 'Create'}
                            </button>
                        </form>
                    </div>

                    <div className="card">
                        <p className="card-title">Join Tenant</p>
                        <form className="form" onSubmit={handleJoin}>
                            <div className="form-field">
                                <label className="form-label">Tenant ID</label>
                                <input
                                    className="form-input"
                                    type="number"
                                    min="1"
                                    value={joinId}
                                    onChange={e => setJoinId(e.target.value)}
                                    placeholder="1"
                                />
                            </div>
                            <button
                                type="submit"
                                className="btn btn--secondary"
                                disabled={joining || !joinId}
                            >
                                {joining ? 'Joining...' : 'Request to Join'}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default MiscPage

import { useCallback, useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useAlert } from '../context/AlertContext'
import { tenantsApi } from '../api/tenants'
import { TenantMemberDto } from '../models/dto/TenantDto'
import { PermissionRole } from '../models/dto/PermissionsDto'

const ROLE_UNACCEPTED = 'ROLE_UNACCEPTED'

function roleBadge(role: string) {
    if (role === PermissionRole.Admin) return <span className="badge badge--purple">Admin</span>
    if (role === ROLE_UNACCEPTED) return <span className="badge badge--yellow">Pending</span>
    return <span className="badge badge--gray">User</span>
}

function AdminPage() {
    const { chosenTenantId, permissions, hasRole } = useAuth()
    const { success, error } = useAlert()

    const [members, setMembers] = useState<TenantMemberDto[]>([])
    const [loading, setLoading] = useState(false)
    const [acting, setActing] = useState<number | null>(null)

    const tenantName = permissions?.find(p => p.tenantId === chosenTenantId)?.tenantName
    const isAdmin = chosenTenantId !== null && hasRole(chosenTenantId, PermissionRole.Admin)

    const loadMembers = useCallback(async () => {
        if (!chosenTenantId || !isAdmin) return
        setLoading(true)
        try {
            setMembers(await tenantsApi.getMembers(chosenTenantId))
        } catch {
            error('Failed to load members')
        } finally {
            setLoading(false)
        }
    }, [chosenTenantId, isAdmin])

    useEffect(() => { loadMembers() }, [loadMembers])

    const handleAccept = async (userId: number) => {
        if (!chosenTenantId) return
        setActing(userId)
        try {
            await tenantsApi.acceptUser(chosenTenantId, userId)
            success('User accepted')
            await loadMembers()
        } catch {
            error('Failed to accept user')
        } finally {
            setActing(null)
        }
    }

    const handleReject = async (userId: number) => {
        if (!chosenTenantId) return
        setActing(userId)
        try {
            await tenantsApi.rejectUser(chosenTenantId, userId)
            success('User removed')
            await loadMembers()
        } catch {
            error('Failed to reject user')
        } finally {
            setActing(null)
        }
    }

    if (!chosenTenantId) {
        return (
            <div className="page">
                <p className="empty-state">Select a tenant from the Profile menu to manage it.</p>
            </div>
        )
    }

    if (!isAdmin) {
        return (
            <div className="page">
                <p className="empty-state">You need admin access in the selected tenant to view this page.</p>
            </div>
        )
    }

    const pending = members.filter(m => m.role === ROLE_UNACCEPTED)
    const accepted = members.filter(m => m.role !== ROLE_UNACCEPTED)

    return (
        <div className="page">
            <h2 className="page-title">{tenantName} — Admin</h2>

            {pending.length > 0 && (
                <div className="card" style={{ marginBottom: '20px' }}>
                    <p className="card-title">Pending Approval</p>
                    <table className="members-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {pending.map(m => (
                                <tr key={m.userId}>
                                    <td>{m.username}</td>
                                    <td>{m.email}</td>
                                    <td>
                                        <div className="actions">
                                            <button
                                                className="btn btn--success"
                                                disabled={acting === m.userId}
                                                onClick={() => handleAccept(m.userId)}
                                            >
                                                Accept
                                            </button>
                                            <button
                                                className="btn btn--danger"
                                                disabled={acting === m.userId}
                                                onClick={() => handleReject(m.userId)}
                                            >
                                                Reject
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            <div className="card">
                <p className="card-title">Members</p>
                {loading ? (
                    <p className="empty-state">Loading...</p>
                ) : accepted.length === 0 ? (
                    <p className="empty-state">No members yet.</p>
                ) : (
                    <table className="members-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            {accepted.map(m => (
                                <tr key={m.userId}>
                                    <td>{m.username}</td>
                                    <td>{m.email}</td>
                                    <td>{roleBadge(m.role)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    )
}

export default AdminPage

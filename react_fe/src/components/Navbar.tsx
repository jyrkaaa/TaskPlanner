import { ReactNode, useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { PermissionRole } from "../models/dto/PermissionsDto";

interface NavItem {
    label: string;
    path?: string;
    action?: () => void;
    requiredRole?: string;
    requiresTenant?: boolean;
}

function Navbar({ children }: { children: ReactNode }) {
    const navigate = useNavigate();
    const { isAuthenticated, permissions, logout, chosenTenantId, chooseTenant } = useAuth();
    const [profileOpen, setProfileOpen] = useState(false);
    const [tenantModalOpen, setTenantModalOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    const navItems: NavItem[] = [
        { label: 'Issues', path: '/issues', action: () => { navigate("/issues")}, requiresTenant: true },
        { label: 'Tasks', path: '/tasks', action: () => { navigate("/tasks")}, requiresTenant: true },
        { label: 'Admin', path: '/admin', action: () => { navigate("/admin")}, requiredRole: PermissionRole.Admin },
        { label : 'Misc', path: '/misc', action: () => { navigate("/misc")}}
    ];

    const canShow = (item: NavItem): boolean => {
        if (!item.requiredRole && !item.requiresTenant) return true;
        if (item.requiredRole && !(permissions?.some(p => (p.role as unknown as string) === item.requiredRole) ?? false)) return false;
        if (item.requiresTenant && !chosenTenantId) return false;
        return true;
    };

    useEffect(() => {
        if (!profileOpen) return;
        function handleClickOutside(e: MouseEvent) {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
                setProfileOpen(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [profileOpen]);

    return (
        <>
            {isAuthenticated && (
                <nav className="navbar">
                    <div className="navbar-left">
                        <h2 className="navbar-brand">Task Planner</h2>
                        <div className="navbar-tabs">
                            {navItems.filter(canShow).map(item =>
                                item.path ? (
                                    <Link key={item.label} to={item.path}>{item.label}</Link>
                                ) : (
                                    <button key={item.label} onClick={item.action}>{item.label}</button>
                                )
                            )}
                        </div>
                    </div>
                    <div className="navbar-right">
                        {chosenTenantId && (
                            <span className="navbar-tenant">
                                {permissions?.find(p => p.tenantId === chosenTenantId)?.tenantName}
                            </span>
                        )}
                        <div className="profile-dropdown" ref={dropdownRef}>
                            <button className="navbar-logout" onClick={() => setProfileOpen(o => !o)}>
                                Profile
                            </button>
                            {profileOpen && (
                                <div className="profile-menu">
                                    <button
                                        className="profile-menu-item"
                                        onClick={() => { setTenantModalOpen(true); setProfileOpen(false); }}
                                    >
                                        Tenant
                                    </button>
                                </div>
                            )}
                        </div>
                        <button className="navbar-logout" onClick={logout}>Logout</button>
                    </div>
                </nav>
            )}
            {children}
            {tenantModalOpen && (
                <div className="modal-overlay" onClick={() => setTenantModalOpen(false)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h2 className="modal-title">Choose Tenant</h2>
                        <div className="tenant-list">
                            {permissions?.map(p => (
                                <button
                                    key={p.tenantId}
                                    className={`tenant-item${p.tenantId === chosenTenantId ? ' tenant-item--active' : ''}`}
                                    onClick={() => { chooseTenant(p.tenantId); setTenantModalOpen(false); }}
                                >
                                    <span className="tenant-name">{p.tenantName}</span>
                                    <span className="tenant-role">{p.role as unknown as string}</span>
                                </button>
                            ))}
                        </div>
                        <button className="modal-close" onClick={() => setTenantModalOpen(false)}>Close</button>
                    </div>
                </div>
            )}
        </>
    );
}

export default Navbar;

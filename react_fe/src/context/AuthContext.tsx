import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { api, clearTokens, setTokens } from '../api/client'
import { PermissionsDto } from '../models/dto/PermissionsDto'
import { clearPermissions, loadPermissions, savePermissions } from '../utils/permissionsStorage'
import { AuthResponse } from '../models/dto/AuthRespose'
import { AuthRequest, RegisterRequest } from '../models/dto/AuthRequest'
import { LocalStorageNames } from '../constants/LocalStorageNames'

interface AuthContextValue {
  permissions: PermissionsDto[] | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (authRequest: AuthRequest) => Promise<void>;
  logout: () => void;
  register: (registerRequest: RegisterRequest) => Promise<void>;
  hasRole: (tenantId: number, role: string) => boolean;
  getTenantRole: (tenantId: number) => string | null;
  chosenTenantId: number | null;
  chooseTenant: (tenantId: number) => void;
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [permissions, setPermissions] = useState<PermissionsDto[] | null>(null)
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true);
  const [chosenTenantId, setChosenTenantId] = useState<number | null>(() => {
    const stored = localStorage.getItem(LocalStorageNames.CHOSEN_TENANT_ID);
    return stored ? parseInt(stored, 10) : null;
  });

  useEffect(() => {
    async function init() {
      const cached = await loadPermissions()
      if (cached) {
        setPermissions(cached)
        setIsAuthenticated(true)
        setLoading(false)
        return
      }

      if (!localStorage.getItem('refreshToken')) {
        setLoading(false)
        return
      }

      try {
        const perms = await api.get<PermissionsDto[]>('/auth/permissions')
        setPermissions(perms)
        setIsAuthenticated(true)
        await savePermissions(perms)
      } catch {
        clearTokens()
        clearPermissions()
      } finally {
        setLoading(false)
      }
    }

    init()
  }, [])

  const login = useCallback(async (authRequest: AuthRequest) => {
    const response = await api.post<AuthResponse>('/auth/login', authRequest);
    setTokens({ access: response.token, refresh: response.refreshToken })
    const perms = await api.get<PermissionsDto[]>('/auth/permissions')
    setPermissions(perms)
    setIsAuthenticated(true)
    await savePermissions(perms)
  }, [])

  const register = useCallback(async (registerRequest: RegisterRequest) => {
    const response = await api.post<AuthResponse>('/auth/register', registerRequest);
    setTokens({ access: response.token, refresh: response.refreshToken })
    const perms = await api.get<PermissionsDto[]>('/auth/permissions')
    setPermissions(perms)
    setIsAuthenticated(true)
    await savePermissions(perms)
  }, [])

  const logout = useCallback(() => {
    clearTokens()
    clearPermissions()
    localStorage.removeItem(LocalStorageNames.CHOSEN_TENANT_ID)
    setPermissions(null)
    setIsAuthenticated(false)
    setChosenTenantId(null)
  }, [])

  const hasRole = useCallback(
    (tenantId: number, role: string) =>
      permissions?.some((p) => p.tenantId === tenantId && (p.role as unknown as string) === role) ?? false,
    [permissions]
  )

  const getTenantRole = useCallback(
    (tenantId: number) => (permissions?.find((p) => p.tenantId === tenantId)?.role as unknown as string) ?? null,
    [permissions]
  );

  const chooseTenant = useCallback((tenantId: number) => {
    if (permissions?.some(p => p.tenantId === tenantId)) {
      localStorage.setItem(LocalStorageNames.CHOSEN_TENANT_ID, tenantId.toString())
      setChosenTenantId(tenantId)
    }
  }, [permissions]);

  return (
    <AuthContext.Provider value={{ permissions, isAuthenticated, loading, login, logout, register,  hasRole, getTenantRole, chosenTenantId, chooseTenant }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

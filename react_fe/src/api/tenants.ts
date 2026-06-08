import { api } from './client'
import { TenantDto, TenantMemberDto } from '../models/dto/TenantDto'

export const tenantsApi = {
    getLinked: () => api.get<TenantDto[]>('/tenants'),
    create: (name: string, code: string) => api.post<void>('/tenants/create', { name, code }),
    join: (tenantId: number) => api.post<void>(`/tenants/join/${tenantId}`),
    getMembers: (tenantId: number) => api.get<TenantMemberDto[]>(`/tenants/${tenantId}/members`),
    acceptUser: (tenantId: number, userId: number) => api.post<void>(`/tenants/${tenantId}/accept/${userId}`),
    rejectUser: (tenantId: number, userId: number) => api.post<void>(`/tenants/${tenantId}/reject/${userId}`),
}

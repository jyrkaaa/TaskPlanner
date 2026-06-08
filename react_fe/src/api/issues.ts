import { api } from './client'
import { TenantMemberDto } from '../models/dto/TenantDto'
import { IssueDto } from '../models/dto/IssueDto'
import { PaginationBaseRequest } from '../models/PaginationBaseRequest'
import { PaginationBaseResponse } from '../models/PaginationBaseResponse'

export const issuesApi = {
    getIssues: (tenantId: number, pagination: PaginationBaseRequest) => api.post<PaginationBaseResponse<IssueDto>>(`${tenantId}/issues/search`, pagination),
    create: (name: string, code: string) => api.post<void>('/tenants/create', { name, code }),
    join: (tenantId: number) => api.post<void>(`/tenants/join/${tenantId}`),
    getMembers: (tenantId: number) => api.get<TenantMemberDto[]>(`/tenants/${tenantId}/members`),
    acceptUser: (tenantId: number, userId: number) => api.post<void>(`/tenants/${tenantId}/accept/${userId}`),
    rejectUser: (tenantId: number, userId: number) => api.post<void>(`/tenants/${tenantId}/reject/${userId}`),
}

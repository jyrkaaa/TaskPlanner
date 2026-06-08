export interface PermissionsDto {
    tenantId: number,
    tenantName: string,
    role: PermissionRole,
}

export class PermissionRole {
    public static readonly Admin = "ROLE_ADMIN"
    public static readonly User = "ROLE_USER"
}
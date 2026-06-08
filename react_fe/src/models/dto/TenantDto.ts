export interface TenantDto {
    id: number;
    name: string;
    code: string;
    isConfirmed: boolean;
}

export interface TenantMemberDto {
    userId: number;
    username: string;
    email: string;
    role: string;
}

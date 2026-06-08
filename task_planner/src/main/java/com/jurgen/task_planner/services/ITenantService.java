package com.jurgen.task_planner.services;

import java.util.List;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TenantDto;
import com.jurgen.task_planner.models.dtos.TenantMemberDto;
import com.jurgen.task_planner.models.requests.CreateTenantRequest;

public interface ITenantService {
    TenantDto getTenantById(int id) throws HttpErrorException;
    List<TenantDto> getAllLinkedTenants(int userId);
    void createTenantAndJoinAsAdmin(CreateTenantRequest request, int creatorUserId) throws HttpErrorException;
    boolean updateTenant(TenantDto tenant, int userId) throws HttpErrorException;
    void joinTenant(int tenantId, int userId) throws HttpErrorException;
    void acceptUser(int tenantId, int targetUserId) throws HttpErrorException;
    void rejectUser(int tenantId, int targetUserId) throws HttpErrorException;
    List<TenantMemberDto> getTenantMembers(int tenantId);
}

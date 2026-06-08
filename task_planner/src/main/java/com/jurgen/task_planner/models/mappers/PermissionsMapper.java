package com.jurgen.task_planner.models.mappers;

import java.util.List;

import com.jurgen.task_planner.models.Constants;
import com.jurgen.task_planner.models.dtos.PermissionsDto;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;
import com.jurgen.task_planner.models.entities.auth.TenantUsersEntity;
import com.jurgen.task_planner.models.entities.auth.UserPrincipal;

public final class PermissionsMapper {
    public static List<PermissionsDto> toDto(UserPrincipal userDetails) {
        List<TenantUsersEntity> tenants = userDetails.getUserEntity().getTenantUsers();
        if (tenants.isEmpty()) return List.of();
        return tenants.stream()
                .filter(te -> !Constants.Roles.UNACCEPTED.equals(te.getRole().getName()))
                .map(te -> {
                    TenantEntity t = te.getTenant();
                    return new PermissionsDto(t.getId(), t.getName(), te.getRole().getName());
                })
                .toList();
    }
}

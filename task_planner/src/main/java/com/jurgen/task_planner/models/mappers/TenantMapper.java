package com.jurgen.task_planner.models.mappers;

import com.jurgen.task_planner.models.dtos.TenantDto;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;

public final class TenantMapper {

    public static TenantDto toDto(TenantEntity entity) {
        if (entity == null) return null;
        return new TenantDto(
            entity.getId(),
            entity.getName(),
            entity.getCode(),
            entity.isConfirmed()
        );
    }
}

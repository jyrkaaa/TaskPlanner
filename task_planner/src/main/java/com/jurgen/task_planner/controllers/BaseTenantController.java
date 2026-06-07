package com.jurgen.task_planner.controllers;

import org.springframework.http.HttpStatus;

import com.jurgen.task_planner.models.auth.SecurityUtils;
import com.jurgen.task_planner.models.dtos.HttpErrorException;

public abstract class BaseTenantController {
    protected int resolveTenant(int tenantId) throws HttpErrorException {
        if (!SecurityUtils.isMemberOfTenant(tenantId))
            throw new HttpErrorException(HttpStatus.FORBIDDEN);
        return tenantId;
    }
    
}

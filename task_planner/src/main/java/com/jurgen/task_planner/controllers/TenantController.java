package com.jurgen.task_planner.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.jurgen.task_planner.models.auth.SecurityUtils;
import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TenantDto;
import com.jurgen.task_planner.models.dtos.TenantMemberDto;
import com.jurgen.task_planner.models.requests.CreateTenantRequest;
import com.jurgen.task_planner.services.ITenantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json", path = "api/tenants")
public class TenantController {

    private final ITenantService tenantService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TenantDto>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createTenant(@RequestBody CreateTenantRequest request) throws HttpErrorException {
        tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/join/{tenantId}")
    public ResponseEntity<Void> joinTenant(@PathVariable int tenantId) throws HttpErrorException {
        tenantService.joinTenant(tenantId, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tenantId}/accept/{userId}")
    public ResponseEntity<Void> acceptUser(@PathVariable int tenantId,
                                           @PathVariable int userId) throws HttpErrorException {
        if (!SecurityUtils.isAdminInTenant(tenantId)) throw new HttpErrorException("Only tenant admins can accept users", HttpStatus.FORBIDDEN);

        tenantService.acceptUser(tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tenantId}/reject/{userId}")
    public ResponseEntity<Void> rejectUser(@PathVariable int tenantId,
                                           @PathVariable int userId) throws HttpErrorException {
        if (!SecurityUtils.isAdminInTenant(tenantId)) throw new HttpErrorException("Only tenant admins can reject users", HttpStatus.FORBIDDEN);
        tenantService.rejectUser(tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{tenantId}/members")
    public ResponseEntity<List<TenantMemberDto>> getTenantMembers(@PathVariable int tenantId) throws HttpErrorException {
        if (!SecurityUtils.isMemberOfTenant(tenantId)) throw new HttpErrorException("Only tenant members can view members", HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(tenantService.getTenantMembers(tenantId));
    }
}

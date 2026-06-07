package com.jurgen.task_planner.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.IssueDto;
import com.jurgen.task_planner.models.requests.CreateIssueRequest;
import com.jurgen.task_planner.models.requests.UpdateIssueRequest;
import com.jurgen.task_planner.services.IIssueService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(produces = "application/json", path = "api/{tenantId}/issues")
public class IssueController extends BaseTenantController {

    private final IIssueService issueService;

    @GetMapping
    public ResponseEntity<List<IssueDto>> getIssues(@PathVariable int tenantId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(issueService.getIssuesForTenant(tenantId));
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<IssueDto> getIssue(@PathVariable int tenantId,
                                             @PathVariable int issueId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(issueService.getIssue(tenantId, issueId));
    }

    @PostMapping
    public ResponseEntity<IssueDto> createIssue(@PathVariable int tenantId,
                                                @RequestBody CreateIssueRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(tenantId, request));
    }

    @PutMapping("/{issueId}")
    public ResponseEntity<IssueDto> updateIssue(@PathVariable int tenantId,
                                                @PathVariable int issueId,
                                                @RequestBody UpdateIssueRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(issueService.updateIssue(tenantId, issueId, request));
    }

    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable int tenantId,
                                            @PathVariable int issueId) throws HttpErrorException {
        resolveTenant(tenantId);
        issueService.deleteIssue(tenantId, issueId);
        return ResponseEntity.noContent().build();
    }
}

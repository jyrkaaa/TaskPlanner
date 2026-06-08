package com.jurgen.task_planner.services;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.IssueDto;
import com.jurgen.task_planner.models.dtos.PagedResponse;
import com.jurgen.task_planner.models.requests.CreateIssueRequest;
import com.jurgen.task_planner.models.requests.PaginationRequest;
import com.jurgen.task_planner.models.requests.UpdateIssueRequest;

public interface IIssueService {
    PagedResponse<IssueDto> getIssuesForTenant(int tenantId, PaginationRequest request);
    IssueDto getIssue(int tenantId, int issueId) throws HttpErrorException;
    IssueDto createIssue(int tenantId, CreateIssueRequest request) throws HttpErrorException;
    IssueDto updateIssue(int tenantId, int issueId, UpdateIssueRequest request) throws HttpErrorException;
    void deleteIssue(int tenantId, int issueId) throws HttpErrorException;
}

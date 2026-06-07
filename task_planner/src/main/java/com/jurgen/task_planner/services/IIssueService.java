package com.jurgen.task_planner.services;

import java.util.List;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.IssueDto;
import com.jurgen.task_planner.models.requests.CreateIssueRequest;
import com.jurgen.task_planner.models.requests.UpdateIssueRequest;

public interface IIssueService {
    List<IssueDto> getIssuesForTenant(int tenantId);
    IssueDto getIssue(int tenantId, int issueId) throws HttpErrorException;
    IssueDto createIssue(int tenantId, CreateIssueRequest request) throws HttpErrorException;
    IssueDto updateIssue(int tenantId, int issueId, UpdateIssueRequest request) throws HttpErrorException;
    void deleteIssue(int tenantId, int issueId) throws HttpErrorException;
}

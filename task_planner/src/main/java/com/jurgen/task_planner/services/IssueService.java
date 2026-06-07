package com.jurgen.task_planner.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.IssueDto;
import com.jurgen.task_planner.models.entities.IssueEntity;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;
import com.jurgen.task_planner.models.requests.CreateIssueRequest;
import com.jurgen.task_planner.models.requests.UpdateIssueRequest;
import com.jurgen.task_planner.repositories.IssueRepository;
import com.jurgen.task_planner.repositories.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService implements IIssueService {

    private final IssueRepository   _issueRepository;
    private final TenantRepository  _tenantRepository;

    @Override
    public List<IssueDto> getIssuesForTenant(int tenantId) {
        return _issueRepository.findAllByTenantId(tenantId).stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public IssueDto getIssue(int tenantId, int issueId) throws HttpErrorException {
        return toDto(requireIssue(tenantId, issueId));
    }

    @Override
    @Transactional
    public IssueDto createIssue(int tenantId, CreateIssueRequest request) throws HttpErrorException {
        if (request.code() == null || request.code().isBlank()) {
            throw new HttpErrorException("Code is required", HttpStatus.BAD_REQUEST);
        }
        if (_issueRepository.existsByTenantIdAndCode(tenantId, request.code())) {
            throw new HttpErrorException("Issue code already exists in this tenant", HttpStatus.CONFLICT);
        }
        TenantEntity tenant = _tenantRepository.findById(tenantId)
            .orElseThrow(() -> new HttpErrorException("Tenant not found", HttpStatus.NOT_FOUND));
        return toDto(_issueRepository.save(IssueEntity.create(tenant, request.code())));
    }

    @Override
    @Transactional
    public IssueDto updateIssue(int tenantId, int issueId, UpdateIssueRequest request) throws HttpErrorException {
        if (request.code() == null || request.code().isBlank()) {
            throw new HttpErrorException("Code is required", HttpStatus.BAD_REQUEST);
        }
        IssueEntity issue = requireIssue(tenantId, issueId);
        if (!issue.getCode().equals(request.code()) && _issueRepository.existsByTenantIdAndCode(tenantId, request.code())) {
            throw new HttpErrorException("Issue code already exists in this tenant", HttpStatus.CONFLICT);
        }
        issue.setCode(request.code());
        return toDto(_issueRepository.save(issue));
    }

    @Override
    @Transactional
    public void deleteIssue(int tenantId, int issueId) throws HttpErrorException {
        _issueRepository.delete(requireIssue(tenantId, issueId));
    }

    private IssueEntity requireIssue(int tenantId, int issueId) throws HttpErrorException {
        return _issueRepository.findByIdAndTenantId(issueId, tenantId)
            .orElseThrow(() -> new HttpErrorException("Issue not found", HttpStatus.NOT_FOUND));
    }

    private IssueDto toDto(IssueEntity e) {
        return new IssueDto(e.getId(), e.getTenant().getId(), e.getCode(), e.getCreatedAt(), e.getUpdatedAt());
    }
}

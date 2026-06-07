package com.jurgen.task_planner.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TaskAssigneeDto;
import com.jurgen.task_planner.models.dtos.TaskDto;
import com.jurgen.task_planner.models.dtos.TaskResponsibilityDto;
import com.jurgen.task_planner.models.entities.IssueEntity;
import com.jurgen.task_planner.models.entities.TaskEntity;
import com.jurgen.task_planner.models.entities.TaskResponsibilityEntity;
import com.jurgen.task_planner.models.entities.TaskUsersEntity;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;
import com.jurgen.task_planner.models.entities.auth.UserEntity;
import com.jurgen.task_planner.models.requests.AssignTaskUserRequest;
import com.jurgen.task_planner.models.requests.CreateTaskRequest;
import com.jurgen.task_planner.models.requests.CreateTaskResponsibilityRequest;
import com.jurgen.task_planner.models.requests.UpdateTaskRequest;
import com.jurgen.task_planner.repositories.IssueRepository;
import com.jurgen.task_planner.repositories.TaskRepository;
import com.jurgen.task_planner.repositories.TaskResponsibilityRepository;
import com.jurgen.task_planner.repositories.TenantRepository;
import com.jurgen.task_planner.repositories.TenantUsersRepository;
import com.jurgen.task_planner.repositories.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository             _taskRepository;
    private final IssueRepository            _issueRepository;
    private final TaskResponsibilityRepository _responsibilityRepository;
    private final TenantRepository           _tenantRepository;
    private final TenantUsersRepository      _tenantUsersRepository;
    private final UserJpaRepository          _userRepository;

    @Override
    public List<TaskDto> getTasksForIssue(int tenantId, int issueId) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        return _taskRepository.findAllByIssueId(issueId).stream().map(this::toDto).toList();
    }

    @Override
    public TaskDto getTask(int tenantId, int issueId, int taskId) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        return toDto(requireTask(issueId, taskId));
    }

    @Override
    @Transactional
    public TaskDto createTask(int tenantId, int issueId, CreateTaskRequest request) throws HttpErrorException {
        IssueEntity issue = requireIssue(tenantId, issueId);
        return toDto(_taskRepository.save(TaskEntity.create(issue, request.code(), request.description())));
    }

    @Override
    @Transactional
    public TaskDto updateTask(int tenantId, int issueId, int taskId, UpdateTaskRequest request) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        TaskEntity task = requireTask(issueId, taskId);
        task.setCode(request.code());
        task.setDescription(request.description());
        return toDto(_taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(int tenantId, int issueId, int taskId) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        _taskRepository.delete(requireTask(issueId, taskId));
    }

    @Override
    @Transactional
    public void assignUser(int tenantId, int issueId, int taskId, AssignTaskUserRequest request) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        TaskEntity task = requireTask(issueId, taskId);

        // user must be an accepted member of the tenant
        _tenantUsersRepository.findByTenantIdAndUserId(tenantId, request.userId())
            .filter(tu -> tu.isAccepted())
            .orElseThrow(() -> new HttpErrorException("User is not an accepted member of this tenant", HttpStatus.BAD_REQUEST));

        UserEntity user = _userRepository.findById(request.userId())
            .orElseThrow(() -> new HttpErrorException("User not found", HttpStatus.NOT_FOUND));

        TaskResponsibilityEntity responsibility = _responsibilityRepository.findById(request.responsibilityId())
            .filter(r -> r.getTenant().getId().equals(tenantId))
            .orElseThrow(() -> new HttpErrorException("Responsibility not found in this tenant", HttpStatus.NOT_FOUND));

        boolean alreadyAssigned = task.getTaskUsers().stream()
            .anyMatch(tu -> tu.getUser().getId() == request.userId());
        if (alreadyAssigned) {
            throw new HttpErrorException("User already assigned to this task", HttpStatus.CONFLICT);
        }

        task.getTaskUsers().add(TaskUsersEntity.create(task, user, responsibility));
        _taskRepository.save(task);
    }

    @Override
    @Transactional
    public void unassignUser(int tenantId, int issueId, int taskId, int userId) throws HttpErrorException {
        requireIssue(tenantId, issueId);
        TaskEntity task = requireTask(issueId, taskId);
        boolean removed = task.getTaskUsers().removeIf(tu -> tu.getUser().getId() == userId);
        if (!removed) {
            throw new HttpErrorException("User is not assigned to this task", HttpStatus.NOT_FOUND);
        }
        _taskRepository.save(task);
    }

    @Override
    public List<TaskResponsibilityDto> getResponsibilities(int tenantId) {
        return _responsibilityRepository.findAllByTenantId(tenantId).stream()
            .map(r -> new TaskResponsibilityDto(r.getId(), r.getName()))
            .toList();
    }

    @Override
    @Transactional
    public TaskResponsibilityDto createResponsibility(int tenantId, CreateTaskResponsibilityRequest request) throws HttpErrorException {
        if (request.name() == null || request.name().isBlank()) {
            throw new HttpErrorException("Name is required", HttpStatus.BAD_REQUEST);
        }
        TenantEntity tenant = _tenantRepository.findById(tenantId)
            .orElseThrow(() -> new HttpErrorException("Tenant not found", HttpStatus.NOT_FOUND));
        TaskResponsibilityEntity saved = _responsibilityRepository.save(
            TaskResponsibilityEntity.create(tenant, request.name())
        );
        return new TaskResponsibilityDto(saved.getId(), saved.getName());
    }

    @Override
    @Transactional
    public void deleteResponsibility(int tenantId, int responsibilityId) throws HttpErrorException {
        TaskResponsibilityEntity responsibility = _responsibilityRepository.findById(responsibilityId)
            .filter(r -> r.getTenant().getId() == tenantId)
            .orElseThrow(() -> new HttpErrorException("Responsibility not found in this tenant", HttpStatus.NOT_FOUND));
        _responsibilityRepository.delete(responsibility);
    }

    private IssueEntity requireIssue(int tenantId, int issueId) throws HttpErrorException {
        return _issueRepository.findByIdAndTenantId(issueId, tenantId)
            .orElseThrow(() -> new HttpErrorException("Issue not found", HttpStatus.NOT_FOUND));
    }

    private TaskEntity requireTask(int issueId, int taskId) throws HttpErrorException {
        return _taskRepository.findByIdAndIssueId(taskId, issueId)
            .orElseThrow(() -> new HttpErrorException("Task not found", HttpStatus.NOT_FOUND));
    }

    private TaskDto toDto(TaskEntity t) {
        List<TaskAssigneeDto> assignees = t.getTaskUsers().stream()
            .map(tu -> new TaskAssigneeDto(
                tu.getUser().getId(),
                tu.getUser().getUsername(),
                tu.getResponsibility().getId(),
                tu.getResponsibility().getName()
            ))
            .toList();
        return new TaskDto(t.getId(), t.getIssue().getId(), t.getCode(), t.getDescription(), assignees, t.getCreatedAt(), t.getUpdatedAt());
    }
}

package com.jurgen.task_planner.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TaskDto;
import com.jurgen.task_planner.models.entities.IssueEntity;
import com.jurgen.task_planner.models.entities.TaskEntity;
import com.jurgen.task_planner.models.entities.TaskStatusEntity;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;
import com.jurgen.task_planner.models.requests.CreateTaskRequest;
import com.jurgen.task_planner.models.requests.UpdateTaskRequest;
import com.jurgen.task_planner.repositories.IssueRepository;
import com.jurgen.task_planner.repositories.TaskRepository;
import com.jurgen.task_planner.repositories.TaskResponsibilityRepository;
import com.jurgen.task_planner.repositories.TaskStatusRepository;
import com.jurgen.task_planner.repositories.TenantRepository;
import com.jurgen.task_planner.repositories.TenantUsersRepository;
import com.jurgen.task_planner.repositories.UserJpaRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository              _taskRepository;
    @Mock private IssueRepository             _issueRepository;
    @Mock private TaskResponsibilityRepository _responsibilityRepository;
    @Mock private TaskStatusRepository        _taskStatusRepository;
    @Mock private TenantRepository            _tenantRepository;
    @Mock private TenantUsersRepository       _tenantUsersRepository;
    @Mock private UserJpaRepository           _userRepository;

    @InjectMocks
    private TaskService _taskService;

    //#region get Task

    @Test
    void getTask_whenBothExist_returnsDto() throws HttpErrorException {
        IssueEntity      issue  = makeIssue(10, 1);
        TaskStatusEntity status = makeStatus(1, 1);
        TaskEntity       task   = makeTask(42, issue, status);

        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findByIdAndIssueId(42, 10)).thenReturn(Optional.of(task));

        TaskDto dto = _taskService.getTask(1, 10, 42);

        assertThat(dto.id()).isEqualTo(42);
        assertThat(dto.issueId()).isEqualTo(10);
        assertThat(dto.code()).isEqualTo("TASK-1");
        assertThat(dto.description()).isEqualTo("Do something");
        assertThat(dto.assignees()).isEmpty();
        verify(_issueRepository, times(1)).findByIdAndTenantId(10, 1);
        verify(_taskRepository, times(1)).findByIdAndIssueId(42, 10);
    }

    @Test
    void getTask_whenIssueNotFound_throws404() {
        when(_issueRepository.findByIdAndTenantId(99, 1)).thenReturn(Optional.empty());

        HttpErrorException ex = assertThrows(
            HttpErrorException.class,
            () -> _taskService.getTask(1, 99, 1)
        );
        assertThat(ex.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
        verify(_issueRepository, times(1)).findByIdAndTenantId(99, 1);
        verify(_taskRepository, times(0)).findByIdAndIssueId(anyInt(), anyInt());
    }

    @Test
    void getTask_whenTaskNotFound_throws404() {
        IssueEntity issue = makeIssue(10, 1);
        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findByIdAndIssueId(99, 10)).thenReturn(Optional.empty());

        HttpErrorException ex = assertThrows(
            HttpErrorException.class,
            () -> _taskService.getTask(1, 10, 99)
        );
        assertThat(ex.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
        verify(_issueRepository, times(1)).findByIdAndTenantId(10, 1);
        verify(_taskRepository, times(1)).findByIdAndIssueId(99, 10);
    }

    //#endregion

    //#region create Task

    @Test
    void createTask_savesAndReturnsDto() throws HttpErrorException {
        IssueEntity      issue  = makeIssue(10, 1);
        TaskStatusEntity status = makeStatus(1, 1);
        TaskEntity       saved  = makeTask(5, issue, status);

        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(_taskRepository.save(any(TaskEntity.class))).thenReturn(saved);

        TaskDto dto = _taskService.createTask(1, 10, new CreateTaskRequest("TASK-1", "Do something", 1));

        verify(_taskRepository, times(1)).save(any(TaskEntity.class));
        assertThat(dto.id()).isEqualTo(5);
        assertThat(dto.code()).isEqualTo("TASK-1");
        assertThat(dto.statusId()).isEqualTo(1);
    }

    @Test
    void createTask_withUnknownStatus_throws404() {
        IssueEntity issue = makeIssue(10, 1);
        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskStatusRepository.findById(99)).thenReturn(Optional.empty());

        HttpErrorException ex = assertThrows(
            HttpErrorException.class,
            () -> _taskService.createTask(1, 10, new CreateTaskRequest("TASK-1", "Do something", 99))
        );
        assertThat(ex.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //#endregion

    //#region update Task

    @Test
    void updateTask_noIssueExists_throws404() {
        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.empty());

        HttpErrorException ex = assertThrows(
            HttpErrorException.class,
            () -> _taskService.updateTask(1, 10, 42, new UpdateTaskRequest("T-UPD", "Updated desc", null))
        );

        assertThat(ex.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
        verify(_issueRepository, times(1)).findByIdAndTenantId(10, 1);
        verify(_taskRepository, times(0)).findByIdAndIssueId(anyInt(), anyInt());
        verify(_taskRepository, times(0)).save(any());
    }

    @Test
    void updateTask_noTaskExists_throws404() {
        IssueEntity issue = makeIssue(10, 1);
        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findByIdAndIssueId(42, 10)).thenReturn(Optional.empty());
        
        HttpErrorException ex = assertThrows(
            HttpErrorException.class,
            () -> _taskService.updateTask(1, 10, 42, new UpdateTaskRequest("T-UPD", "Updated desc", null))
        );

        assertThat(ex.statusCode).isEqualTo(HttpStatus.NOT_FOUND);
        verify(_issueRepository, times(1)).findByIdAndTenantId(10, 1);
        verify(_taskRepository, times(1)).findByIdAndIssueId(42, 10);
        verify(_taskRepository, times(0)).save(any());
    }

    @Test
    void updateTask_updatesFieldsAndSaves() throws HttpErrorException {
        IssueEntity      issue  = makeIssue(10, 1);
        TaskStatusEntity status = makeStatus(1, 1);
        TaskEntity       task   = makeTask(42, issue, status);
        int statusId = 1;

        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findByIdAndIssueId(42, 10)).thenReturn(Optional.of(task));
        when(_taskStatusRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(_taskRepository.save(task)).thenReturn(task);

        TaskDto dto = _taskService.updateTask(1, 10, 42, new UpdateTaskRequest("T-UPD", "Updated desc", statusId));

        assertThat(dto.code()).isEqualTo("T-UPD");
        assertThat(dto.description()).isEqualTo("Updated desc");
        verify(_taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_withNewStatus_updatesStatus() throws HttpErrorException {
        IssueEntity      issue      = makeIssue(10, 1);
        TaskStatusEntity oldStatus  = makeStatus(1, 1);
        TaskStatusEntity newStatus  = makeStatus(2, 1);
        TaskEntity       task       = makeTask(42, issue, oldStatus);

        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findByIdAndIssueId(42, 10)).thenReturn(Optional.of(task));
        when(_taskStatusRepository.findById(2)).thenReturn(Optional.of(newStatus));
        when(_taskRepository.save(task)).thenReturn(task);

        TaskDto dto = _taskService.updateTask(1, 10, 42, new UpdateTaskRequest("TASK-1", "Do something", 2));

        assertThat(dto.statusId()).isEqualTo(2);
        verify(_taskRepository, times(1)).save(task);
    }

    //#endregion

    //#region getTasksForIssue

    @Test
    void getTasksForIssue_whenNoTasks_returnsEmptyList() throws HttpErrorException {
        IssueEntity issue = makeIssue(10, 1);
        when(_issueRepository.findByIdAndTenantId(10, 1)).thenReturn(Optional.of(issue));
        when(_taskRepository.findAllByIssueId(10)).thenReturn(List.of());

        assertThat(_taskService.getTasksForIssue(1, 10)).isEmpty();
    }

    //#endregion

    //#region Helpers

    private IssueEntity makeIssue(int id, int tenantId) {
        TenantEntity tenant = new TenantEntity("Test Tenant", "TEST");
        ReflectionTestUtils.setField(tenant, "id", tenantId);
        IssueEntity issue = IssueEntity.create(tenant, "ISS-1");
        ReflectionTestUtils.setField(issue, "id", id);
        return issue;
    }

    private TaskStatusEntity makeStatus(int id, int tenantId) {
        TenantEntity tenant = new TenantEntity("Test Tenant", "TEST");
        ReflectionTestUtils.setField(tenant, "id", tenantId);
        TaskStatusEntity status = TaskStatusEntity.create(tenant, "In Progress", 1);
        ReflectionTestUtils.setField(status, "id", id);
        return status;
    }

    private TaskEntity makeTask(int id, IssueEntity issue, TaskStatusEntity status) {
        TaskEntity task = TaskEntity.create(issue, status, "TASK-1", "Do something");
        ReflectionTestUtils.setField(task, "id", id);
        return task;
    }

    //#endregion
}

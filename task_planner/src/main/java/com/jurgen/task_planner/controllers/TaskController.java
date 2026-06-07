package com.jurgen.task_planner.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TaskDto;
import com.jurgen.task_planner.models.dtos.TaskResponsibilityDto;
import com.jurgen.task_planner.models.dtos.TaskStatusDto;
import com.jurgen.task_planner.models.requests.AssignTaskUserRequest;
import com.jurgen.task_planner.models.requests.CreateTaskRequest;
import com.jurgen.task_planner.models.requests.CreateTaskResponsibilityRequest;
import com.jurgen.task_planner.models.requests.CreateTaskStatusRequest;
import com.jurgen.task_planner.models.requests.UpdateTaskRequest;
import com.jurgen.task_planner.models.requests.UpdateTaskStatusRequest;
import com.jurgen.task_planner.services.ITaskService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(produces = "application/json", path = "api/{tenantId}")
public class TaskController extends BaseTenantController {
    private final ITaskService taskService;
    // ── Tasks ────────────────────────────────────────────────────────────────

    @GetMapping("/issues/{issueId}/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(@PathVariable int tenantId,
                                                  @PathVariable int issueId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.getTasksForIssue(tenantId, issueId));
    }

    @GetMapping("/issues/{issueId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable int tenantId,
                                           @PathVariable int issueId,
                                           @PathVariable int taskId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.getTask(tenantId, issueId, taskId));
    }

    @PostMapping("/issues/{issueId}/tasks")
    public ResponseEntity<TaskDto> createTask(@PathVariable int tenantId,
                                              @PathVariable int issueId,
                                              @RequestBody CreateTaskRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(tenantId, issueId, request));
    }

    @PutMapping("/issues/{issueId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable int tenantId,
                                              @PathVariable int issueId,
                                              @PathVariable int taskId,
                                              @RequestBody UpdateTaskRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.updateTask(tenantId, issueId, taskId, request));
    }

    @DeleteMapping("/issues/{issueId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable int tenantId,
                                           @PathVariable int issueId,
                                           @PathVariable int taskId) throws HttpErrorException {
        resolveTenant(tenantId);
        taskService.deleteTask(tenantId, issueId, taskId);
        return ResponseEntity.noContent().build();
    }

    // ── Task user assignment ─────────────────────────────────────────────────

    @PostMapping("/issues/{issueId}/tasks/{taskId}/assign")
    public ResponseEntity<Void> assignUser(@PathVariable int tenantId,
                                           @PathVariable int issueId,
                                           @PathVariable int taskId,
                                           @RequestBody AssignTaskUserRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        taskService.assignUser(tenantId, issueId, taskId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/issues/{issueId}/tasks/{taskId}/users/{userId}")
    public ResponseEntity<Void> unassignUser(@PathVariable int tenantId,
                                             @PathVariable int issueId,
                                             @PathVariable int taskId,
                                             @PathVariable int userId) throws HttpErrorException {
        resolveTenant(tenantId);
        taskService.unassignUser(tenantId, issueId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    // ── Task responsibilities ────────────────────────────────────────────────

    @GetMapping("/task-responsibilities")
    public ResponseEntity<List<TaskResponsibilityDto>> getResponsibilities(@PathVariable int tenantId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.getResponsibilities(tenantId));
    }

    @PostMapping("/task-responsibilities")
    public ResponseEntity<TaskResponsibilityDto> createResponsibility(@PathVariable int tenantId,
                                                                      @RequestBody CreateTaskResponsibilityRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createResponsibility(tenantId, request));
    }

    @DeleteMapping("/task-responsibilities/{responsibilityId}")
    public ResponseEntity<Void> deleteResponsibility(@PathVariable int tenantId,
                                                     @PathVariable int responsibilityId) throws HttpErrorException {
        taskService.deleteResponsibility(tenantId, responsibilityId);
        return ResponseEntity.noContent().build();
    }

    // ── Task statuses ────────────────────────────────────────────────────────

    @GetMapping("/task-statuses")
    public ResponseEntity<List<TaskStatusDto>> getStatuses(@PathVariable int tenantId) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.getStatuses(tenantId));
    }

    @PostMapping("/task-statuses")
    public ResponseEntity<TaskStatusDto> createStatus(@PathVariable int tenantId,
                                                      @RequestBody CreateTaskStatusRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createStatus(tenantId, request));
    }

    @PutMapping("/task-statuses/{statusId}")
    public ResponseEntity<TaskStatusDto> updateStatus(@PathVariable int tenantId,
                                                      @PathVariable int statusId,
                                                      @RequestBody UpdateTaskStatusRequest request) throws HttpErrorException {
        resolveTenant(tenantId);
        return ResponseEntity.ok(taskService.updateStatus(tenantId, statusId, request));
    }

    @DeleteMapping("/task-statuses/{statusId}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int tenantId,
                                             @PathVariable int statusId) throws HttpErrorException {
        resolveTenant(tenantId);
        taskService.deleteStatus(tenantId, statusId);
        return ResponseEntity.noContent().build();
    }
}

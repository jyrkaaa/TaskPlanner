package com.jurgen.task_planner.services;

import java.util.List;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TaskDto;
import com.jurgen.task_planner.models.dtos.TaskResponsibilityDto;
import com.jurgen.task_planner.models.requests.AssignTaskUserRequest;
import com.jurgen.task_planner.models.requests.CreateTaskRequest;
import com.jurgen.task_planner.models.requests.CreateTaskResponsibilityRequest;
import com.jurgen.task_planner.models.requests.UpdateTaskRequest;

public interface ITaskService {
    List<TaskDto> getTasksForIssue(int tenantId, int issueId) throws HttpErrorException;
    TaskDto getTask(int tenantId, int issueId, int taskId) throws HttpErrorException;
    TaskDto createTask(int tenantId, int issueId, CreateTaskRequest request) throws HttpErrorException;
    TaskDto updateTask(int tenantId, int issueId, int taskId, UpdateTaskRequest request) throws HttpErrorException;
    void deleteTask(int tenantId, int issueId, int taskId) throws HttpErrorException;
    void assignUser(int tenantId, int issueId, int taskId, AssignTaskUserRequest request) throws HttpErrorException;
    void unassignUser(int tenantId, int issueId, int taskId, int userId) throws HttpErrorException;

    List<TaskResponsibilityDto> getResponsibilities(int tenantId);
    TaskResponsibilityDto createResponsibility(int tenantId, CreateTaskResponsibilityRequest request) throws HttpErrorException;
    void deleteResponsibility(int tenantId, int responsibilityId) throws HttpErrorException;
}

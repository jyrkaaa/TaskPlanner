package com.jurgen.task_planner.models.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record TaskDto(
    long id,
    long issueId,
    int statusId,
    String statusName,
    String code,
    String description,
    List<TaskAssigneeDto> assignees,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

package com.jurgen.task_planner.models.dtos;

public record TaskAssigneeDto(
    int userId,
    String username,
    long responsibilityId,
    String responsibilityName
) {}

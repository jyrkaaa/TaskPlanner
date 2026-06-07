package com.jurgen.task_planner.models.requests;

public record UpdateTaskRequest(String code, String description, Integer statusId) {}

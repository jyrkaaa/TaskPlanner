package com.jurgen.task_planner.models.dtos;

public record TenantMemberDto(
    int userId,
    String username,
    String email,
    String role
) {}

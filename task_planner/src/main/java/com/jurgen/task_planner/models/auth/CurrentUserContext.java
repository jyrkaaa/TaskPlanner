package com.jurgen.task_planner.models.auth;

import java.util.Map;

public record CurrentUserContext(
    int userId,
    String email,
    Map<Integer, String> tenantRoles
) {}

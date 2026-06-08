package com.jurgen.task_planner.models.dtos;

import java.time.OffsetDateTime;

public record IssueDto(
    int id,
    int tenantId,
    String code,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

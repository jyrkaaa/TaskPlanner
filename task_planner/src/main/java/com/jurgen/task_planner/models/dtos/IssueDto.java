package com.jurgen.task_planner.models.dtos;

import java.time.OffsetDateTime;

public record IssueDto(
    long id,
    int tenantId,
    String code,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

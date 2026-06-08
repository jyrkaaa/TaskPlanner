package com.jurgen.task_planner.models.requests;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PaginationRequest(
    int page,
    int size
) {
    public PaginationRequest {
        if (page < 1) page = 1;
        if (size < 1) size = 20;
        if (size > 100) size = 100;
    }

    public Pageable toPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}

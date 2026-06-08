package com.jurgen.task_planner.models.dtos;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PagedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <E, T> PagedResponse<T> from(Page<E> page, Function<E, T> mapper) {
        List<E> content = page.getContent();
        return new PagedResponse<>(
            content.stream().map(mapper).toList(),
            page.getNumber() + 1, // start from 0, better understanding with starting from 1
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}

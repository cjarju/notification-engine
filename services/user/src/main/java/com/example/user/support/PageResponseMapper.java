package com.example.user.support;

import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.example.user.dto.PageResponse;

public final class PageResponseMapper {

    private PageResponseMapper() {}

    public static <S, T> PageResponse<T> map(
            Page<S> page,
            Function<S, T> mapper) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(mapper)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}

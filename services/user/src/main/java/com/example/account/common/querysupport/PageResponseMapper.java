package com.example.account.common.querysupport;

import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.example.account.common.dto.PageResponse;

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
                page.isLast(),
                PageLinksFactory.from(page)

        );
    }
}

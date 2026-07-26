package com.example.account.common.querysupport;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.account.common.dto.PageLinks;

public final class PageLinksFactory {

    private PageLinksFactory() {}

    public static PageLinks from(Page<?> page) {

        var builder = ServletUriComponentsBuilder.fromCurrentRequest();

        int current = page.getNumber();
        int size = page.getSize();

        int lastPage = Math.max(page.getTotalPages() - 1, 0);

        String self = buildLink(builder, current, size);
        String first = buildLink(builder, 0, size);

        String prev = page.hasPrevious()
                ? buildLink(builder, current - 1, size)
                : null;

        String next = page.hasNext()
                ? buildLink(builder, current + 1, size)
                : null;

        String last = buildLink(builder, lastPage, size);

        return new PageLinks(
                self,
                first,
                prev,
                next,
                last
        );
    }

    private static String buildLink(
            ServletUriComponentsBuilder builder,
            int page,
            int size) {

        return builder
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
    }
}

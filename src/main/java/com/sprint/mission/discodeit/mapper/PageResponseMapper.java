package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper<T> {
    public PageResponse<T> fromSlice(Slice<T> slice) {
        return PageResponse.<T>builder()
                .number(slice.getNumber())
                .content(slice.getContent())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .totalElements((long) slice.getContent().size())
                .build();
    }

    public PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .number(page.getNumber())
                .content(page.getContent())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
    }
}

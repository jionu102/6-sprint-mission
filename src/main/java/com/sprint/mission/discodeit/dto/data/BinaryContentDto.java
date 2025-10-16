package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class BinaryContentDto {
    private UUID id;
    private String fileName;
    private Long size;
    private String contentType;
}

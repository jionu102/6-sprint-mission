package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Setter
public class MessageDto {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UserDto author;
    private List<BinaryContentDto> attachments;
}

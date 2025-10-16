package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@Setter
public class ReadStatusDto {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
}

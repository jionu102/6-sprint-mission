package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@Setter
public class UserStatusDto {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
}

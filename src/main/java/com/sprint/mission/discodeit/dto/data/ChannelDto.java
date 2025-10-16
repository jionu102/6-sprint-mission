package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ChannelDto {
    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private List<UserDto> participants;
    private Instant lastMessageAt;
}

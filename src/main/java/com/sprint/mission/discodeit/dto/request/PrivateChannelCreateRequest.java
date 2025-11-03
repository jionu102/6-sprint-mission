package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @Size(min = 2, message = "2명 이상의 사용자가 존재해야합니다.")
    List<UUID> participantIds
) {

}
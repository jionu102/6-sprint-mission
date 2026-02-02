package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

public record UserStatusUpdatedEvent(
        UUID userId
) {

}

package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

public record LogoutEvent(
        UUID userId
) {
}

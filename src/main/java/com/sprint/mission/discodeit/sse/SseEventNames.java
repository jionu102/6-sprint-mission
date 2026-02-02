package com.sprint.mission.discodeit.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventNames {
    //Notification
    NOTIFICATIONS_CREATED("notifications.created"),

    //BinaryContent
    BINARY_CONTENT_UPDATED("binaryContent.updated"),

    //Channel
    CHANNELS_CREATED("channels.created"),
    CHANNELS_UPDATED("channels.updated"),
    CHANNELS_DELETED("channels.deleted"),

    //User
    USERS_CREATED("users.created"),
    USERS_UPDATED("users.updated"),
    USERS_DELETED("users.deleted")
    ;

    private final String name;
}

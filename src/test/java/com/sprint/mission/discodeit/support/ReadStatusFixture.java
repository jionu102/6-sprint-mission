package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

public class ReadStatusFixture {
    public static ReadStatus createReadStatus(User user, Channel channel, Instant lastReadAt, UUID uuid) {
        ReadStatus readStatus = new ReadStatus(user, channel,  lastReadAt);
        setReadStatusId(readStatus, uuid);
        return readStatus;
    }

    private static void setReadStatusId(ReadStatus readStatus, UUID uuid) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(readStatus, uuid);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

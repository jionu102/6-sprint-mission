package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.base.BaseEntity;

import java.lang.reflect.Field;
import java.util.UUID;

public class ChannelFixture {
    public static Channel createChannel(ChannelType type, String name, String description, UUID uuid) {
        Channel channel = new Channel(type, name, description);
        setChannelId(channel, uuid);
        return channel;
    }

    private static void setChannelId(Channel channel, UUID uuid) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id"); //getDeclaredField()는 상속받은 필드는 찾지 않지만 private, public, protected 필드를 모두 탐색할 수 있음
            field.setAccessible(true);
            field.set(channel, uuid);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw  new RuntimeException(e);
        }
    }
}

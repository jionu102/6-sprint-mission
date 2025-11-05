package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageFixture {
    public static Message createMessage(String content, Channel channel, User author, List<BinaryContent> attachments, UUID uuid) {
        Message message = new Message(content, channel, author, attachments);
        setMessageId(message, uuid);
        setMessageCreatedAt(message, Instant.now());
        return message;
    }

    private static void setMessageId(Message message, UUID uuid) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(message, uuid);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMessageCreatedAt(Message message, Instant createdAt) {
        try {
            Field field = BaseEntity.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(message, createdAt);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

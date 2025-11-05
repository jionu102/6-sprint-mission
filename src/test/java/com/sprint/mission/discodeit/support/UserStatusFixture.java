package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

public class UserStatusFixture {
    public static UserStatus createUserStatus(User user, Instant lastActiveAt, UUID uuid) {
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        setUserStatusId(userStatus, uuid);
        return userStatus;
    }

    private static void setUserStatusId(UserStatus userStatus, UUID uuid) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(userStatus, uuid);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new  RuntimeException(e);
        }
    }
}

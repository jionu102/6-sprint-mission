package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;

import java.lang.reflect.Field;
import java.util.UUID;

public class UserFixture {
    public static User createUser(String username, String email, String password, BinaryContent profile, UUID id) {
        User user = new User(username, email, password, profile);
        setUserId(user, id);
        return user;
    }

    public static void setUserStatus(User user, UserStatus userStatus) {
        try {
            Field field = User.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(user, userStatus);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUserId(User user, UUID uuid) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, uuid);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.LogoutEvent;
import com.sprint.mission.discodeit.event.message.UserStatusUpdatedEvent;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @EventListener
    public void handleUserStatusUpdatedEvent(UserStatusUpdatedEvent userStatusUpdatedEvent) {
        userService.broadcastUserStatus(userStatusUpdatedEvent.userId());
    }

    @EventListener
    public void handleLogoutEvent(LogoutEvent logoutEvent) {
        userService.logout(logoutEvent.userId());
    }
}

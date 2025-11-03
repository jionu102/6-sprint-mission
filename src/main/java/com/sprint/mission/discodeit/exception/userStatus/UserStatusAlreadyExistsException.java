package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {

    public  UserStatusAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

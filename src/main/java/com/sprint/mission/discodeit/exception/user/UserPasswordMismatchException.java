package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserPasswordMismatchException extends UserException {

    public UserPasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}

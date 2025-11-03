package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelUpdateException extends ChannelException {

    public ChannelUpdateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
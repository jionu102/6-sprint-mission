package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    //자세하게 가져가고 GlobalExceptionHandler 간소화
    //UserException
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USERNAME_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 username 입니다."),
    USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "일치하지 않는 비밀번호 입니다."),

    //ChannelException
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Private 채널은 수정할 수 없습니다."),

    //BinaryContentException
    BINARYCONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    //MessageException
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),

    //ReadStatusException
    READSTATUS_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 읽음 이력 입니다."),
    READSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 이력을 찾을 수 없습니다."),

    //UserStatusException
    USERSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 상태를 찾을 수 없습니다."),
    USERSTATUS_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 사용자 상태 입니다.");

    private final HttpStatus status;
    private final String message;
}
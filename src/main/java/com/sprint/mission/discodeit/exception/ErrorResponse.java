package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private Instant timestamp;
    private Map<String, Object> details;
    private String code;
    private String message;
    private String exceptionType;
    private int status;
}

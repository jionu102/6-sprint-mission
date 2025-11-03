package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
        @NotBlank(message = "메시지는 공백일 수 없습니다.")
        @Size(min = 1, message = "1글자 이상 입력해주세요.")
        String newContent
) {

}

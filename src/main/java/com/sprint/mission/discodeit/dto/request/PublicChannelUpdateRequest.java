package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "채널 이름은 필수 입력입니다.")
    @Size(min = 1, max = 10, message = "채널 이름은 1~10자를 입력해주세요.")
    String newName,
    String newDescription
) {

}

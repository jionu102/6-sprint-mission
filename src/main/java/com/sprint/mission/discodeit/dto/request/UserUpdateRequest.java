package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 5, message = "username은 5자 이상 입력해주세요.")
        @NotBlank(message = "username은 필수 입력입니다.")
        String newUsername,
        @Email(message = "잘못된 이메일 형식 입니다.")
        @Size(min = 13, message = "이메일은 13자 이상 입력해주세요")
        @NotBlank(message = "이메일은 필수 입력입니다.")
        String newEmail,
        @Size(min = 10, max = 16, message = "비밀번호는 10~16자를 입력해주세요.")
        @NotBlank(message = "비밀번호는 필수 입력입니다.")
        String newPassword
) {

}

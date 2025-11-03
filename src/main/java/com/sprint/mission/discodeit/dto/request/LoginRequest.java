package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(min = 5)
    String username,
    @Size(min = 10, max = 16)
    @NotBlank
    String password
) {

}

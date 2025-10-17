package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    public UserStatusDto toDto(UserStatus userStatus);
}

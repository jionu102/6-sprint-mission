package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected SseEmitterRepository emitterRepository;

  @Mapping(target = "online", expression = "java(emitterRepository.hasActiveSseEmitterByUserId(user.getId()))")
  public abstract UserDto toDto(User user);
}

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

    default ChannelDto toDto(
            Channel channel,
            Instant lastMessageAt,
            List<UserDto> participants
    ) {
        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .participants(participants)
                .lastMessageAt(lastMessageAt)
                .build();
    }
}

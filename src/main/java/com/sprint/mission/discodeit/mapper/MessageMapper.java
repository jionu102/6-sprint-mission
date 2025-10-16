package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        return  MessageDto.builder()
                .author(userMapper.toDto(message.getAuthor()))
                .content(message.getContent())
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .attachments(message.getAttachments().stream().map(binaryContentMapper::toDto).toList())
                .build();
    }
}

package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final PageResponseMapper<MessageDto>  pageResponseMapper;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
                          List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        UUID authorId = messageCreateRequest.authorId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    return binaryContentRepository.save(binaryContent);
                })
                .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public MessageDto find(UUID messageId) {
        return messageMapper.toDto(messageRepository.findById(messageId)
                .orElseThrow(
                        () -> new NoSuchElementException("Message with id " + messageId + " not found")));
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, LocalDateTime cursor, Pageable pageable) {
        Slice<MessageDto> messageSlice;
        if (cursor == null) {
            messageSlice = messageRepository.findAllByChannelId(channelId, pageable).map(messageMapper::toDto);
        }else {
            messageSlice = messageRepository.findAllByChannelId(channelId, cursor, pageable)
                    .map(messageMapper::toDto);
        }

        return pageResponseMapper.fromSlice(messageSlice);
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachments().stream().map(BinaryContent::getId)
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}

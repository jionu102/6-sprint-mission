package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel createdChannel = channelRepository.save(channel);
        return channelMapper.toDto(createdChannel, getLastMessageAt(createdChannel.getId()), null);
    }

    @Transactional
    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));
                    return new ReadStatus(user, createdChannel, Instant.now());
                })
                .forEach(readStatusRepository::save);

        return channelMapper.toDto(createdChannel, getLastMessageAt(createdChannel.getId()), getParticipants(createdChannel.getId()));
    }

    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channel -> channelMapper.toDto(channel, getLastMessageAt(channel.getId()), getParticipants(channelId)))
                .orElseThrow(
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(channel -> channelMapper.toDto(channel, getLastMessageAt(channel.getId()), getParticipants(channel.getId())))
                .toList();
    }

    @Transactional
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(newName, newDescription);

        Channel updatedChannel = channelRepository.save(channel);
        return channelMapper.toDto(updatedChannel, getLastMessageAt(updatedChannel.getId()), getParticipants(updatedChannel.getId()));
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    public Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);
    }

    public List<UserDto> getParticipants(UUID channelId) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        if (optionalChannel.isPresent()
                && optionalChannel.get().getType().equals(ChannelType.PRIVATE)) {
            return readStatusRepository.findAllByChannelId(channelId)
                    .stream().map(readStatus -> userMapper.toDto(readStatus.getUser()))
                    .toList();
        }
        return null;
    }
}

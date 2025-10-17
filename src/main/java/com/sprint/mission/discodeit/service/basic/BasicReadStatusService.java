package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.info("🔴 create() 호출: userId={}, channelId={}", request.userId(), request.channelId());
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));

        UUID channelId = request.channelId();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        if (readStatusRepository.findAllByUserId(userId).stream()
                .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
            throw new IllegalArgumentException(
                    "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        return readStatusMapper.toDto(readStatusRepository.findById(readStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found")));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream().map(readStatusMapper::toDto).toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt);

        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}

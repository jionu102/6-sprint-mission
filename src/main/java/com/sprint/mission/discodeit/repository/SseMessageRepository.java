package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.sse.SseMessage;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> broadcastQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    private final Map<UUID, ConcurrentLinkedDeque<UUID>> userQueues = new ConcurrentHashMap<>();

    private static final int USER_QUEUE_MAX_SIZE = 100;
    private static final int BROADCAST_QUEUE_MAX_SIZE = 100;

    public void saveAll(Collection<UUID> userIds, UUID eventId, String eventName, Object data) {
        SseMessage sseMessage = new SseMessage(eventId, eventName, data, Instant.now());
        messages.put(eventId, sseMessage);

        userIds.forEach(userId -> {
                    userQueues.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>()).addFirst(eventId);

                    while (userQueues.get(userId).size() > USER_QUEUE_MAX_SIZE) {
                        UUID oldId = userQueues.get(userId).pollLast();
                        messages.remove(oldId);
                    }
                }
        );
    }

    public void saveBroadcast(UUID eventId, String eventName, Object data) {
        SseMessage sseMessage = new SseMessage(eventId, eventName, data, Instant.now());
        messages.put(eventId, sseMessage);
        broadcastQueue.addFirst(eventId);

        while (broadcastQueue.size() > BROADCAST_QUEUE_MAX_SIZE) {
            UUID oldId = broadcastQueue.pollLast();
            messages.remove(oldId);
        }
    }

    public List<SseMessage> findAfter(UUID userId, UUID lastEventId) {
        if (lastEventId == null) {
            return Collections.emptyList();
        }

        return Stream.concat(
                        userQueues.getOrDefault(userId, new ConcurrentLinkedDeque<>()).stream(),
                        broadcastQueue.stream()
                )
                .map(messages::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SseMessage::createdAt))
                .dropWhile(msg -> !msg.id().equals(lastEventId))
                .skip(1)
                .toList();
    }
}

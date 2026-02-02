package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.event.message.UserStatusUpdatedEvent;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository sseMessageRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static long TIME_OUT = 60 * 60 * 1000L;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(TIME_OUT);

        emitterRepository.save(receiverId, emitter);

        emitter.onCompletion(() ->
            disconnect(receiverId, emitter)
        );
        emitter.onTimeout(() -> {
                    disconnect(receiverId, emitter);
                    log.error("[SSE] Emitter Time Out, userId: {}", receiverId.toString());
                }
        );
        emitter.onError(e -> {
                    disconnect(receiverId, emitter);
                    log.error("[SSE] Emitter Error, userId: {}, error: {}", receiverId.toString(), e.getMessage());
                }
        );

        eventPublisher.publishEvent(new UserStatusUpdatedEvent(receiverId));

        ping(emitter);

        if (lastEventId != null) {
            sseMessageRepository.findAfter(receiverId, lastEventId)
                    .forEach(msg -> sendToEmitter(emitter, msg.id(), msg.eventName(), msg.data()));
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        sseMessageRepository.saveAll(receiverIds, eventId, eventName, data);

        receiverIds.stream()
                .flatMap(id -> emitterRepository.findByUserId(id).stream())
                .forEach(emitter -> sendToEmitter(emitter, eventId, eventName, data));
    }

    public void broadcast(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        sseMessageRepository.saveBroadcast(eventId, eventName, data);
        Collection<List<SseEmitter>> emitters = emitterRepository.findAll();
        emitters.stream().flatMap(Collection::stream)
                .forEach(emitter -> sendToEmitter(emitter, eventId, eventName, data));
    }

    public void disconnectAll(UUID receiverId) {
        emitterRepository.removeAll(receiverId);
        eventPublisher.publishEvent(new UserStatusUpdatedEvent(receiverId));
    }

    @Scheduled(fixedDelay = 30000L)
    public void heartBeat() {
        emitterRepository.findAll().stream()
                .flatMap(Collection::stream)
                .forEach(this::ping);
    }

    private void ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("ping").data(""));
        } catch (IOException e) {
            emitter.complete();
        }
    }

    private void sendToEmitter(SseEmitter emitter, UUID id, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().id(id.toString()).name(eventName).data(data));
        } catch (IOException e) {
            emitter.complete();
        }
    }

    private void disconnect(UUID receiverId, SseEmitter emitter) {
        emitterRepository.remove(receiverId, emitter);
        if (emitterRepository.findByUserId(receiverId).isEmpty()) {
            eventPublisher.publishEvent(new UserStatusUpdatedEvent(receiverId));
        }
    }
}

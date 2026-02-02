package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void save(UUID userId, SseEmitter emitter) {
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> findByUserId(UUID userId) {
        return emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
    }

    public boolean hasActiveSseEmitterByUserId(UUID userId) {
        return emitters.containsKey(userId);
    }

    public Collection<List<SseEmitter>> findAll() {
        return emitters.values();
    }

    public void remove(UUID userId, SseEmitter emitter) {
        List<SseEmitter> sseEmitters = emitters.get(userId);
        if (sseEmitters != null) {
            sseEmitters.remove(emitter);
            if (sseEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    public void removeAll(UUID userId) {
        emitters.remove(userId);
    }
}

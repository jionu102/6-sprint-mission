package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @Async(value = "eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        log.info("Message received: {}", event);
        String message = event.getData().content();
        UUID channelId = event.getData().channelId();
        String destination = "/sub/channels." + channelId.toString() + ".messages";
        messagingTemplate.convertAndSend(destination, message);
    }
}

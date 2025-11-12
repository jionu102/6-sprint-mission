package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    private UUID channelId;

    @BeforeEach
    public void setup() {
        Channel channel = new Channel(ChannelType.PUBLIC, "testChannel", "testChannelDescription");
        BinaryContent profile = new BinaryContent("buzz.jpg", 1024L, "image/jpeg");
        User author = new User("jionu102", "jionu102@naver.com", "123412341234", profile);
        UserStatus userStatus = new UserStatus(author, Instant.now());
        Message message = new Message("content", channel, author, List.of());

        channelRepository.save(channel);
        channelId = channel.getId();
        userRepository.save(author);
        messageRepository.save(message);
    }

    @Test
    @DisplayName("findAllByChannelIdWithAuthor 성공")
    void  findAllByChannelIdWithAuthor_success() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        Slice<Message> messageSlice = messageRepository.findAllByChannelIdWithAuthor(channelId, Instant.MAX, pageable);

        //then
        assertThat(messageSlice.getNumber()).isEqualTo(0);
        assertThat(messageSlice.getSize()).isEqualTo(10); //Slice의 size는 요청받은 Size
        assertThat(messageSlice.hasNext()).isFalse();
        assertThat(messageSlice.getContent()).isNotEmpty()
                .allSatisfy(message -> {
                    assertThat(message.getId()).isNotNull();
                    //Channel은 Lazy이지만 channel프록시의 channel id = 영속성에서 관리하는 channel의 id와 같으면 주입해줌
                    assertThat(message.getChannel()).isNotNull();

                    assertThat(message.getAuthor()).isNotNull();
                    assertThat(message.getAuthor().getId()).isNotNull();
                    assertThat(message.getAuthor().getProfile()).isNotNull();
                    assertThat(message.getAuthor().getProfile().getId()).isNotNull();
                    assertThat(message.getAuthor().getStatus()).isNotNull();
                    assertThat(message.getAuthor().getStatus().getId()).isNotNull();
                });
    }


}
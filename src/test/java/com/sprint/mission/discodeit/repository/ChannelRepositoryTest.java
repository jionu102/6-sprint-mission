package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
public class ChannelRepositoryTest {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserRepository userRepository;

    private final String CHANNEL_NAME = "testChannel";
    private final String CHANNEL_DESCRIPTION = "testChannelDescription";

    @Test
    @DisplayName("findAll 성공")
    void findAll_success() {
        //given
        Channel publicChannel = new Channel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION);
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(publicChannel);
        channelRepository.save(privateChannel);

        //when
        List<Channel> channels = channelRepository.findAll();

        //then
        assertThat(channels).isNotEmpty()
                .allSatisfy(channel -> {
                    assertThat(channel).isNotNull();
                    assertThat(channel.getId()).isNotNull();
                    assertThat(channel.getType()).isNotNull();
                });
    }

    @Test
    @DisplayName("findAllByTypeOrIdIn 성공")
    void findALlByTypeOrIdIn_success() {
        //given
        Channel publicChannel = new Channel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION);
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

        channelRepository.save(publicChannel);
        Channel savedPrivateChannel = channelRepository.save(privateChannel);
        //when
        List<UUID> subscribedChannelIds = List.of(savedPrivateChannel.getId());
        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, subscribedChannelIds);

        //then
        assertThat(channels).hasSize(2)
                .allSatisfy(channel -> {
                    assertThat(channel).isNotNull();
                    assertThat(channel.getId()).isNotNull();
                });
    }
}

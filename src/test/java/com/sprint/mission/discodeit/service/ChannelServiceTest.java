package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapperImpl;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.ReadStatusFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ChannelMapperImpl.class, BasicChannelService.class})
public class ChannelServiceTest {
    private final String CHANNEL_NAME = "testChannelName";
    private final String CHANNEL_DESCRIPTION = "testChannelDescription";
    private final UUID CHANNEL_ID = UUID.randomUUID();

    @MockitoBean
    ChannelRepository channelRepository;
    @MockitoBean
    ReadStatusRepository readStatusRepository;
    @MockitoBean
    MessageRepository messageRepository;
    @MockitoBean
    UserMapper userMapper;
    @MockitoBean
    UserRepository userRepository;
    @Autowired
    ChannelMapper channelMapper;
    @Autowired
    ChannelService channelService;

    @Test
    @DisplayName("Public 채널 생성 성공")
    void create_public_success() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(CHANNEL_NAME, CHANNEL_DESCRIPTION);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        //when
        ChannelDto channelDto = channelService.create(channelCreateRequest);

        //then
        assertThat(channelDto.id()).isEqualTo(CHANNEL_ID);
        assertThat(channelDto.name()).isEqualTo(CHANNEL_NAME);
        assertThat(channelDto.description()).isEqualTo(CHANNEL_DESCRIPTION);
        assertThat(channelDto.type()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("Private 채널 생성 성공")
    void create_private_success() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PRIVATE, null, null, CHANNEL_ID);
        PrivateChannelCreateRequest channelCreateRequest = new PrivateChannelCreateRequest(null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        //when
        ChannelDto channelDto = channelService.create(channelCreateRequest);

        //then
        assertThat(channelDto.id()).isEqualTo(CHANNEL_ID);
        assertThat(channelDto.name()).isEqualTo(null);
        assertThat(channelDto.description()).isEqualTo(null);
        assertThat(channelDto.type()).isEqualTo(ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("채널 수정 성공")
    void update_success() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);
        PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest("newChannelName", "newDescription");

        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));

        //when
        ChannelDto channelDto = channelService.update(CHANNEL_ID, channelUpdateRequest);

        //then
        assertThat(channelDto.id()).isEqualTo(CHANNEL_ID);
        assertThat(channelDto.name()).isEqualTo("newChannelName");
        assertThat(channelDto.description()).isEqualTo("newDescription");
        assertThat(channelDto.type()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("채널 수정 실패 - Private 채널 수정 시도")
    void update_privateChannelCannotUpdate_throwsException() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PRIVATE, null, null, CHANNEL_ID);
        PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest("newChannelName", "newDescription");

        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));

        //when, then
        assertThatThrownBy(() -> channelService.update(CHANNEL_ID, channelUpdateRequest))
                .isInstanceOf(ChannelUpdateException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE);
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() {
        //given
        given(channelRepository.existsById(CHANNEL_ID)).willReturn(true);

        //when
        channelService.delete(CHANNEL_ID);

        //then
        verify(channelRepository, times(1)).deleteById(CHANNEL_ID);

    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void delete_channelNotFound_throwsException() {
        //given
        given(channelRepository.existsById(CHANNEL_ID)).willReturn(false);

        //when, then
        assertThatThrownBy(() -> channelService.delete(CHANNEL_ID))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 ID로 검색 성공")
    void findAllByUserId_success() {
        //given
        UUID userId = UUID.randomUUID();
        List<UUID> subscribedChannelIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        Channel subscribedChannel1 = ChannelFixture.createChannel(ChannelType.PRIVATE, null, null, subscribedChannelIds.get(0));
        Channel subscribedChannel2 = ChannelFixture.createChannel(ChannelType.PRIVATE, null, null, subscribedChannelIds.get(1));

        Channel publicChannel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);

        List<ReadStatus> myReadStatuses = List.of(
                ReadStatusFixture.createReadStatus(null, subscribedChannel1, null, null),
                ReadStatusFixture.createReadStatus(null, subscribedChannel2, null, null)
        );

        given(readStatusRepository.findAllByUserId(userId)).willReturn(myReadStatuses);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, subscribedChannelIds)).willReturn(List.of(publicChannel, subscribedChannel1, subscribedChannel2));

        //when
        List<ChannelDto> foundChannels = channelService.findAllByUserId(userId);

        //then
        assertThat(foundChannels.size()).isEqualTo(3);
        assertThat(foundChannels.stream().map(ChannelDto::id).toList().containsAll(List.of(publicChannel.getId(), subscribedChannel1.getId(), subscribedChannel2.getId()))).isTrue();
    }

    @Test
    @DisplayName("채널 ID로 검색 성공")
    void find_success() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);
        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));

        //when
        ChannelDto channelDto = channelService.find(CHANNEL_ID);

        //then
        assertThat(channelDto.id()).isEqualTo(CHANNEL_ID);
        assertThat(channelDto.name()).isEqualTo(CHANNEL_NAME);
        assertThat(channelDto.description()).isEqualTo(CHANNEL_DESCRIPTION);
        assertThat(channelDto.type()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("채널 ID로 검색 실패 - 존재하지 않는 채널")
    void find_channelNotFound_throwsException() {
        //given
        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.empty());

        //when. then
        assertThatThrownBy(() -> channelService.find(CHANNEL_ID))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_NOT_FOUND);
    }
}

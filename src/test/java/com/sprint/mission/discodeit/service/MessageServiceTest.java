package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import com.sprint.mission.discodeit.support.UserStatusFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BasicMessageService.class,
        BinaryContentMapperImpl.class,
        MessageMapperImpl.class,
        UserStatusMapperImpl.class,
        UserMapperImpl.class,
        PageResponseMapperImpl.class
})
public class MessageServiceTest {
    private final String MESSAGE_CONTENT = "testMessageContent";
    private final UUID MESSAGE_ID = UUID.randomUUID();

    private final UUID CHANNEL_ID = UUID.randomUUID();
    private final String CHANNEL_NAME = "testChannelName";
    private final String CHANNEL_DESCRIPTION = "testChannelDescription";

    private final UUID USER_ID = UUID.randomUUID();
    private final String USERNAME = "jionu102";
    private final String USER_EMAIL = "jionu102@naver.com";
    private final String USER_PASSWORD = "123412341234";

    private final UUID USERSTATUS_ID = UUID.randomUUID();

    @MockitoBean
    private MessageRepository messageRepository;
    @MockitoBean
    private ChannelRepository channelRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private MessageMapper messageMapper;
    @MockitoBean
    private BinaryContentStorage binaryContentStorage;
    @MockitoBean
    private BinaryContentRepository binaryContentRepository;
    @Autowired
    private PageResponseMapper pageResponseMapper;
    @Autowired
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() {
        //given
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);

        User user = UserFixture.createUser(USERNAME, USER_EMAIL, USER_PASSWORD, null, USER_ID);
        UserStatus userStatus = UserStatusFixture.createUserStatus(user, Instant.now(), USERSTATUS_ID);
        UserFixture.setUserStatus(user, userStatus);

        Message message = MessageFixture.createMessage(MESSAGE_CONTENT, channel, user, List.of(), MESSAGE_ID);
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(MESSAGE_CONTENT,CHANNEL_ID, USER_ID);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
        given(messageRepository.save(any(Message.class))).willReturn(message);

        //when
        MessageDto messageDto = messageService.create(messageCreateRequest, List.of());

        //then
        assertThat(messageDto).isNotNull();
        assertThat(messageDto.attachments()).isEmpty();
        assertThat(messageDto.author().id()).isEqualTo(USER_ID);
        assertThat(messageDto.channelId()).isEqualTo(CHANNEL_ID);
        assertThat(messageDto.content()).isEqualTo(MESSAGE_CONTENT);
        assertThat(messageDto.id()).isEqualTo(MESSAGE_ID);
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
    void create_channelNotFound_throwsException() {
        //given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(MESSAGE_CONTENT, CHANNEL_ID, USER_ID);
        given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> messageService.create(messageCreateRequest, List.of()))
                .isInstanceOf(ChannelNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHANNEL_NOT_FOUND);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);
        User user = UserFixture.createUser(USERNAME, USER_EMAIL, USER_PASSWORD, null, USER_ID);
        UserStatus userStatus = UserStatusFixture.createUserStatus(user, Instant.now(), USERSTATUS_ID);
        UserFixture.setUserStatus(user, userStatus);
        Message message = MessageFixture.createMessage(MESSAGE_CONTENT, channel, user,  List.of(), MESSAGE_ID);
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");

        given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.of(message));

        MessageDto messageDto = messageService.update(MESSAGE_ID, messageUpdateRequest);

        assertThat(messageDto).isNotNull();
        assertThat(messageDto.attachments()).isEmpty();
        assertThat(messageDto.author().id()).isEqualTo(USER_ID);
        assertThat(messageDto.channelId()).isEqualTo(CHANNEL_ID);
        assertThat(messageDto.content()).isEqualTo("newContent");
        assertThat(messageDto.id()).isEqualTo(MESSAGE_ID);
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
    void update_messageNotFound_throwsException() {
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");

        given(messageRepository.findById(MESSAGE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.update(MESSAGE_ID, messageUpdateRequest))
                .isInstanceOf(MessageNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_success() {
        //given
        given(messageRepository.existsById(MESSAGE_ID)).willReturn(true);

        //when
        messageService.delete(MESSAGE_ID);

        //then
        verify(messageRepository, times(1)).deleteById(MESSAGE_ID);
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
    void delete_messageNotFound_throwsException() {
        given(messageRepository.existsById(MESSAGE_ID)).willReturn(false);

        assertThatThrownBy(() -> messageService.delete(MESSAGE_ID))
                .isInstanceOf(MessageNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 성공")
    void findAllByChannelId_success() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Channel channel = ChannelFixture.createChannel(ChannelType.PUBLIC, CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_ID);
        User user = UserFixture.createUser(USERNAME, USER_EMAIL, USER_PASSWORD, null, USER_ID);
        UserStatus userStatus = UserStatusFixture.createUserStatus(user,  Instant.now(), USERSTATUS_ID);
        UserFixture.setUserStatus(user, userStatus);
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();
        List<Message> contents = List.of(
                MessageFixture.createMessage("content1", channel, user, List.of(), messageId1),
                MessageFixture.createMessage("content2", channel, user, List.of(), messageId2)
        );
        Instant nextCursor = contents.stream().max(Comparator.comparing(Message::getCreatedAt)).get().getCreatedAt();

        Slice<Message> slice = new SliceImpl<>(contents, pageable, true);

        given(messageRepository.findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class), any(Pageable.class))).willReturn(slice);

        //when
        PageResponse<MessageDto> pageResponse = messageService.findAllByChannelId(CHANNEL_ID, null, pageable);

        //then
        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.content()).hasSize(contents.size());
        assertThat(pageResponse.nextCursor()).isEqualTo(nextCursor);
        assertThat(pageResponse.size()).isEqualTo(10);
        assertThat(pageResponse.hasNext()).isTrue();
    }
}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserMapperImpl;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.support.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final String USER_EMAIL = "jionu102@naver.com";
    private final String USERNAME = "jionu102";
    private final String USER_PASSWORD = "123412341234";
    private final UUID USER_ID = UUID.randomUUID();

    private final String PROFILE_FILE_NAME = "rex.jpg";
    private final Long PROFILE_FILE_SIZE = 1024L;
    private final String PROFILE_CONTENT_TYPE = "image/jpeg";
    private final byte[] PROFILE_FILE_CONTENT = new byte[1024];

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Spy
    private UserMapper userMapper = new UserMapperImpl(){
        final BinaryContentMapper binaryContentMapper = Mappers.getMapper(BinaryContentMapper.class);
        @Override
        public UserDto toDto(User user) {

            return new UserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    binaryContentMapper.toDto(user.getProfile()),
                    user.getStatus() != null ? user.getStatus().isOnline() : null
            );
        }
    };
    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("사용자 생성 성공")
    void create_success() {
        //given
        BinaryContent profile = new BinaryContent(PROFILE_FILE_NAME, PROFILE_FILE_SIZE, PROFILE_CONTENT_TYPE);
        User savedUser = UserFixture.createUser(USERNAME, USER_EMAIL, USER_PASSWORD, profile, USER_ID);

        UserCreateRequest request = new UserCreateRequest(USERNAME, USER_EMAIL, USER_PASSWORD);
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(PROFILE_FILE_NAME, PROFILE_CONTENT_TYPE, PROFILE_FILE_CONTENT);

        BDDMockito.given(userRepository.save(BDDMockito.any(User.class))).willReturn(savedUser);

        //when
        UserDto userDto = userService.create(request, Optional.of(profileRequest));

        //then
        Assertions.assertThat(userDto).isNotNull();
        Assertions.assertThat(userDto.id()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("사용자 생성 실패 - 이메일 중복")
    void create_duplicateEmail_throwsException() {
        //given
        BinaryContent profile = new BinaryContent(PROFILE_FILE_NAME, PROFILE_FILE_SIZE, PROFILE_CONTENT_TYPE);
        UserCreateRequest request = new UserCreateRequest(USERNAME, USER_EMAIL, USER_PASSWORD);
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(PROFILE_FILE_NAME, PROFILE_CONTENT_TYPE, PROFILE_FILE_CONTENT);

        BDDMockito.given(userRepository.existsByEmail(USER_EMAIL)).willReturn(true);

        //when, then
        Assertions.assertThatThrownBy(() -> userService.create(request, Optional.of(profileRequest)))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_EMAIL_DUPLICATE);
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update_success() {
        //given
        BinaryContent profile = new BinaryContent(PROFILE_FILE_NAME, PROFILE_FILE_SIZE, PROFILE_CONTENT_TYPE);
        User findUser = UserFixture.createUser(USERNAME, USER_EMAIL, USER_PASSWORD, profile, USER_ID);

        UserUpdateRequest request = new UserUpdateRequest("jionu121", "jionu121@naver.com", "432143214321");
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("judy.jpg", "image/jpeg", new byte[1024]);

        BDDMockito.given(userRepository.findById(USER_ID)).willReturn(Optional.of(findUser));

        //when
        UserDto updatedUser = userService.update(USER_ID, request, Optional.of(profileRequest));

        //then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.id()).isEqualTo(USER_ID);
        Assertions.assertThat(updatedUser.email()).isEqualTo("jionu121@naver.com");
        Assertions.assertThat(updatedUser.username()).isEqualTo("jionu121");
        Assertions.assertThat(updatedUser.profile().fileName()).isEqualTo("judy.jpg");
    }

    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
    void update_notFound_throwsException() {
        //given
        UserUpdateRequest request = new UserUpdateRequest("jionu121", "jionu121@naver.com", "432143214321");
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("judy.jpg", "image/jpeg", new byte[1024]);

        BDDMockito.given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        //when, then
        Assertions.assertThatThrownBy(() -> userService.update(USER_ID, request, Optional.of(profileRequest)))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() {
        //given
        BDDMockito.given(userRepository.existsById(USER_ID)).willReturn(true);

        //when
        userService.delete(USER_ID);

        //then
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(USER_ID);
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void delete_notFound_throwsException() {
        //given
        BDDMockito.given(userRepository.existsById(USER_ID)).willReturn(false);

        //when, then
        Assertions.assertThatThrownBy(() -> userService.delete(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}

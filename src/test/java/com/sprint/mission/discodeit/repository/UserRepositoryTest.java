package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    private final String USERNAME = "jionu102";
    private final String PASSWORD = "123412341234";
    private final String USER_EMAIL = "jionu102@naver.com";

    private final String FILE_NAME = "buzz.jpg";
    private final Long FILE_SIZE = 1024L;
    private final String CONTENT_TYPE = "image/jpeg";

    //BeforeEach와 Test는 같은 트랜잭션을 공유한다. (em.clear 시 문제 발생)
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        BinaryContent binaryContent = new BinaryContent(FILE_NAME, FILE_SIZE, CONTENT_TYPE);
        User user = new User(USERNAME, USER_EMAIL, PASSWORD, binaryContent);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);
        userStatusRepository.save(userStatus);
    }

    @Test
    @DisplayName("findByUsername 성공")
    void findByUsername_success() {
        //when
        User user = userRepository.findByUsername("jionu102").orElse(null);

        //then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);

        assertThat(user.getProfile()).isNotNull();
        assertThat(user.getProfile().getId()).isNotNull();
        assertThat(user.getProfile().getContentType()).isEqualTo(CONTENT_TYPE);
        assertThat(user.getProfile().getSize()).isEqualTo(FILE_SIZE);
        assertThat(user.getProfile().getContentType()).isEqualTo(CONTENT_TYPE);

        assertThat(user.getStatus()).isNotNull();
        assertThat(user.getStatus().getId()).isNotNull();
        assertThat(user.getStatus().getLastActiveAt()).isNotNull();
    }

    @Test
    @DisplayName("findAllWithProfileAndStatus 성공")
    void findAllWithProfileAndStatus_success() {
        //when
        List<User> users = userRepository.findAllWithProfileAndStatus();

        //then
        assertThat(users)
                .isNotEmpty()
                .allSatisfy(user -> {
                    assertThat(user.getId()).isNotNull();
                    assertThat(user.getProfile()).isNotNull();
                    assertThat(user.getStatus()).isNotNull();
                });
    }
}

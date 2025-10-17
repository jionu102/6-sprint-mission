package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Setter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    private String username;
    private String email;
    private String password;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;     // BinaryContent
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        if (newProfile != null && (this.profile == null || !newProfile.getId().equals(this.profile.getId()))) {
            this.profile = newProfile;
        }
    }
}

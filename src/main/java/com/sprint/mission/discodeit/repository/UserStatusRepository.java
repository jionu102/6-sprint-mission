package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUserId(UUID userId);

  @Modifying
  @Query("DELETE FROM UserStatus us WHERE us.user.id = :userId")
  void deleteByUserId(UUID userId);
}

package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    @Query("SELECT rs FROM ReadStatus rs JOIN FETCH rs.channel WHERE rs.user.id = :userId")
    List<ReadStatus> findAllByUserId(UUID userId);

    @Query("SELECT rs FROM ReadStatus rs JOIN FETCH rs.user WHERE rs.channel.id = :channelId")
    List<ReadStatus> findAllByChannelId(UUID channelId);

    @Modifying
    @Query("DELETE FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    void deleteAllByChannelId(UUID channelId);
}

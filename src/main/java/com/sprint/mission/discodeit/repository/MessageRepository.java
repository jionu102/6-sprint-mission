package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m JOIN FETCH m.author WHERE m.channel.id = :channelId")
    List<Message> findAllByChannelId(UUID channelId);

    @Query("""
            SELECT m FROM Message m 
            JOIN FETCH m.author
            WHERE m.channel.id = :channelId
            """)
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @Query("""
            SELECT m FROM Message m 
            JOIN FETCH m.author 
            WHERE m.channel.id = :channelId 
            AND m.createdAt < :cursor
            """)
    Slice<Message> findAllByChannelId(UUID channelId, LocalDateTime cursor, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    void deleteAllByChannelId(UUID channelId);
}

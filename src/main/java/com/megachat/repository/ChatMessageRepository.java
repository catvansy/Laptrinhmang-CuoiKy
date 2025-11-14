package com.megachat.repository;

import com.megachat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
           "((m.sender.id = :userId AND m.receiver.id = :friendId) " +
           "OR (m.sender.id = :friendId AND m.receiver.id = :userId)) " +
           "AND (:afterId IS NULL OR m.id > :afterId) " +
           "ORDER BY m.id ASC")
    List<ChatMessage> findConversation(
        @Param("userId") Long userId,
        @Param("friendId") Long friendId,
        @Param("afterId") Long afterId
    );
}


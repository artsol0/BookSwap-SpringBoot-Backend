package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.ChatMessage;
import com.artsolo.bookswap.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);
}

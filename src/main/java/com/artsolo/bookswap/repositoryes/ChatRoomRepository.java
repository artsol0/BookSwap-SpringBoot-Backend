package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.ChatRoom;
import com.artsolo.bookswap.models.ChatRoomKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, ChatRoomKey> {
    List<ChatRoom> findAllBySenderId(Long senderId);
}

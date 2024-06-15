package com.artsolo.bookswap.chat.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.firstParticipant.id = :participantId OR c.secondParticipant.id = :participantId")
    List<ChatRoom> findAllByParticipantId(Long participantId);
    @Query("SELECT c FROM ChatRoom c WHERE " +
            "(c.firstParticipant.id = :firstParticipantId AND c.secondParticipant.id = :secondParticipantId) OR " +
            "(c.firstParticipant.id = :secondParticipantId AND c.secondParticipant.id = :firstParticipantId)")
    Optional<ChatRoom> findByParticipantIds(Long firstParticipantId, Long secondParticipantId);
}

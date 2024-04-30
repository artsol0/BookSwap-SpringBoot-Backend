package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @EmbeddedId
    private ChatRoomKey chatRoomKey;

    @ManyToOne
    @MapsId("sender_id")
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @MapsId("receiver_id")
    @JoinColumn(name = "receiver_id")
    private User receiver;

}

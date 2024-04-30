package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;
    private String content;
    private Date timestamp;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "sender_id", referencedColumnName = "sender_id"),
            @JoinColumn(name = "receiver_id", referencedColumnName = "receiver_id")
    })
    private ChatRoom chatRoom;
}

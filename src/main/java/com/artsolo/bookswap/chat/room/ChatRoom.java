package com.artsolo.bookswap.chat.room;

import com.artsolo.bookswap.user.User;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_participant_id")
    private User firstParticipant;

    @ManyToOne
    @JoinColumn(name = "second_participant_id")
    private User secondParticipant;

}

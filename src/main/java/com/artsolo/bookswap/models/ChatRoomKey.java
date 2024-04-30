package com.artsolo.bookswap.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ChatRoomKey {
    private Long sender_id;
    private Long receiver_id;
}

package com.artsolo.bookswap.chat.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String nickname;
    private byte[] photo;
    private String content;
    private Date timestamp;
}

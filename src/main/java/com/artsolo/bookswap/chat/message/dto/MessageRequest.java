package com.artsolo.bookswap.chat.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    @NotBlank(message = "Content is mandatory")
    @NotNull(message = "Content can't be null")
    @Size(max = 500, message = "Content cannot contain more than 500 characters")
    private String content;

    @NotNull(message = "Date can't be null")
    private Date timestamp;

    @NotNull(message = "Receiver can't be null")
    private Long receiver_id;
}

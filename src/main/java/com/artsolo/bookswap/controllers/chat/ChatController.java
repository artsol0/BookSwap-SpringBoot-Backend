package com.artsolo.bookswap.controllers.chat;

import com.artsolo.bookswap.models.ChatMessage;
import com.artsolo.bookswap.services.ChatMessageService;
import com.artsolo.bookswap.services.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageRequest messageRequest) {
        ChatMessage savedMsg = chatMessageService.sendMessage(messageRequest);
        simpMessagingTemplate.convertAndSend(String.valueOf(savedMsg.getChatRoom().getReceiver().getId()), savedMsg.getContent());
    }

    @GetMapping("get/chats/{userId}")
    public ResponseEntity<List<ChatRoomResponse>> getAllChats(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(userId));
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<MessageResponse>> findChatMessages(@PathVariable("senderId") Long senderId,
                                                              @PathVariable("receiverId") Long receiverId
    ) {
        return ResponseEntity.ok(chatMessageService.getChatMessages(senderId, receiverId));
    }

}

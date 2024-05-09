package com.artsolo.bookswap.controllers.chat;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.ChatMessage;
import com.artsolo.bookswap.models.ChatRoom;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.services.ChatMessageService;
import com.artsolo.bookswap.services.ChatRoomService;
import com.artsolo.bookswap.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload @Valid MessageRequest messageRequest) {
        ChatMessage savedMsg = chatMessageService.sendMessage(messageRequest);
        simpMessagingTemplate.convertAndSendToUser(
                userService.getUserById(messageRequest.getReceiver_id()).getNickname(),
                "/queue/messages",
                chatMessageService.getMessageResponse(savedMsg)
        );
    }

    @GetMapping("/get/chats")
    public ResponseEntity<List<ChatRoomResponse>> getAllChats(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok(chatRoomService.getChatRooms(user.getId()));
    }

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<?> findChatMessages(@PathVariable("chatId") Long chatId, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatId);
        if (chatRoomService.isUserChatParticipant(chatRoom, user)) {
            return ResponseEntity.ok(chatMessageService.getChatMessages(chatId));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the chat participant to perform this action")).build());
    }

    @PostMapping("/add/message")
    public ResponseEntity<?> addNewMessage(@RequestBody @Valid MessageRequest messageRequest) {
        ChatMessage savedMsg = chatMessageService.sendMessage(messageRequest);
        simpMessagingTemplate.convertAndSendToUser(
                userService.getUserById(messageRequest.getReceiver_id()).getNickname(),
                "/queue/messages",
                chatMessageService.getMessageResponse(savedMsg)
        );
        return ResponseEntity.ok(SuccessResponse.builder().data("Message has been sent").build());
    }

}

package com.artsolo.bookswap.chat;

import com.artsolo.bookswap.chat.message.dto.MessageRequest;
import com.artsolo.bookswap.chat.room.dto.ChatRoomResponse;
import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.chat.message.ChatMessage;
import com.artsolo.bookswap.chat.room.ChatRoom;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.chat.message.ChatMessageService;
import com.artsolo.bookswap.chat.room.ChatRoomService;
import com.artsolo.bookswap.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload @Valid MessageRequest messageRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        ChatMessage savedMsg = chatMessageService.sendMessage(messageRequest, user);
        simpMessagingTemplate.convertAndSendToUser(
                user.getNickname(),
                "/queue/messages",
                chatMessageService.getMessageResponse(savedMsg)
        );
        simpMessagingTemplate.convertAndSendToUser(
                userService.getUserById(messageRequest.getReceiver_id()).getNickname(),
                "/queue/messages",
                chatMessageService.getMessageResponse(savedMsg)
        );
    }

    @GetMapping("/get/chats")
    public ResponseEntity<SuccessResponse<List<ChatRoomResponse>>> getAllChats(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(chatRoomService.getChatRooms(user.getId())));
    }

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<?> findChatMessages(@PathVariable("chatId") Long chatId, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatId);
        if (chatRoomService.isUserChatParticipant(chatRoom, user)) {
            return ResponseEntity.ok().body(new SuccessResponse<>(chatMessageService.getChatMessages(chatId)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.FORBIDDEN.value(), "You are not the chat participant to perform this action")).build());
    }

    @PostMapping("/add/message")
    public ResponseEntity<SuccessResponse<String>> addNewMessage(@RequestBody @Valid MessageRequest messageRequest,
                                                                 Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        ChatMessage savedMsg = chatMessageService.sendMessage(messageRequest, user);
        simpMessagingTemplate.convertAndSendToUser(
                userService.getUserById(messageRequest.getReceiver_id()).getNickname(),
                "/queue/messages",
                chatMessageService.getMessageResponse(savedMsg)
        );
        return ResponseEntity.ok().body(new SuccessResponse<>("Message has been sent"));
    }

}

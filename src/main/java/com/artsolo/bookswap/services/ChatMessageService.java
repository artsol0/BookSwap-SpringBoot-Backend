package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.chat.MessageRequest;
import com.artsolo.bookswap.controllers.chat.MessageResponse;
import com.artsolo.bookswap.models.ChatMessage;
import com.artsolo.bookswap.repositoryes.ChatMessageRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final TextEncryptor textEncryptor;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService, UserService userService, TextEncryptor textEncryptor) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
        this.userService = userService;
        this.textEncryptor = textEncryptor;
    }

    public ChatMessage sendMessage(MessageRequest messageRequest) {
        Long chatId = chatRoomService.getChatRoomId(
                messageRequest.getSender_id(),
                messageRequest.getReceiver_id(),
                true
        ).orElseThrow(); // TODO: create new exception
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoomService.getChatRoomById(chatId))
                .sender(userService.getUserById(messageRequest.getSender_id()))
                .content(textEncryptor.encrypt(messageRequest.getContent()))
                .timestamp(messageRequest.getTimestamp())
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    public List<MessageResponse> getChatMessages(Long chatId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoomService.getChatRoomById(chatId));
        return  chatMessages.stream().map(this::getMessageResponse).collect(Collectors.toList());
    }

    public MessageResponse getMessageResponse(ChatMessage chatMessage) {
        return MessageResponse.builder()
                .id(chatMessage.getId())
                .chatId(chatMessage.getChatRoom().getId())
                .senderId(chatMessage.getSender().getId())
                .nickname(chatMessage.getSender().getNickname())
                .photo(chatMessage.getSender().getPhoto())
                .content(textEncryptor.decrypt(chatMessage.getContent()))
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}

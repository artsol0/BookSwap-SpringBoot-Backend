package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.chat.MessageRequest;
import com.artsolo.bookswap.controllers.chat.MessageResponse;
import com.artsolo.bookswap.models.ChatMessage;
import com.artsolo.bookswap.models.ChatRoomKey;
import com.artsolo.bookswap.repositoryes.ChatMessageRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final TextEncryptor textEncryptor;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService, TextEncryptor textEncryptor) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
        this.textEncryptor = textEncryptor;
    }

    public ChatMessage sendMessage(MessageRequest messageRequest) {
        ChatRoomKey chatId = chatRoomService.getChatRoomId(
                messageRequest.getSender_id(),
                messageRequest.getReceiver_id(),
                true
        ).orElseThrow(); // TODO: create new exception
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoomService.getChatRoomById(chatId))
                .content(textEncryptor.encrypt(messageRequest.getContent()))
                .timestamp(messageRequest.getTimestamp())
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    public List<MessageResponse> getChatMessages(Long senderId, Long receiverId) {
        var chatId = chatRoomService.getChatRoomId(senderId, receiverId, false);
        if (chatId.isPresent()) {
            var chatRoom = chatRoomService.getChatRoomById(chatId.get());
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom);
            List<MessageResponse> messageResponses = new ArrayList<>();
            for (ChatMessage message : chatMessages) {
                messageResponses.add(getMessageResponse(message));
            }
            return messageResponses;
        }
        return new ArrayList<>();
    }

    public MessageResponse getMessageResponse(ChatMessage chatMessage) {
        return MessageResponse.builder()
                .id(chatMessage.getId())
                .content(textEncryptor.decrypt(chatMessage.getContent()))
                .senderId(chatMessage.getChatRoom().getSender().getId())
                .receiverId(chatMessage.getChatRoom().getReceiver().getId())
                .build();
    }
}

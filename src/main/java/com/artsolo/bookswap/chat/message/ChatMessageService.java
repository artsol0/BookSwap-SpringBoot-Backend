package com.artsolo.bookswap.chat.message;

import com.artsolo.bookswap.chat.message.dto.MessageRequest;
import com.artsolo.bookswap.chat.message.dto.MessageResponse;
import com.artsolo.bookswap.chat.room.ChatRoomService;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final TextEncryptor textEncryptor;

    public ChatMessage sendMessage(MessageRequest messageRequest, User sender) {
        Long chatId = chatRoomService.getChatRoomId(
                sender.getId(),
                messageRequest.getReceiver_id(),
                true
        ).orElseThrow(() -> new NoDataFoundException("Chat room", sender.getId(), messageRequest.getReceiver_id()));
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoomService.getChatRoomById(chatId))
                .sender(sender)
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

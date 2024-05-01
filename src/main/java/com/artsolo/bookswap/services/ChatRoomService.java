package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.chat.ChatRoomResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.ChatRoom;
import com.artsolo.bookswap.models.ChatRoomKey;
import com.artsolo.bookswap.repositoryes.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderId(userId);
        if (!chatRooms.isEmpty()) {
            List<ChatRoomResponse> chatRoomResponses = new ArrayList<>();
            for (ChatRoom chatRoom : chatRooms) {
                chatRoomResponses.add(getChatRoomResponse(chatRoom));
            }
            return chatRoomResponses;
        }
        return new ArrayList<>();
    }

    public ChatRoomResponse getChatRoomResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .receiverId(chatRoom.getReceiver().getId())
                .nickname(chatRoom.getReceiver().getNickname())
                .photo(chatRoom.getReceiver().getPhoto())
                .build();
    }

    public ChatRoom getChatRoomById(ChatRoomKey chatRoomKey) {
        return chatRoomRepository.findById(chatRoomKey)
                .orElseThrow(() -> new NoDataFoundException(
                        "Chat room",
                        chatRoomKey.getSender_id(),
                        chatRoomKey.getReceiver_id())
                );
    }

    public Optional<ChatRoomKey> getChatRoomId(Long senderId, Long receiverId, boolean createNewRoomIfNotExist) {
        return chatRoomRepository.findById(new ChatRoomKey(senderId, receiverId))
                .map(ChatRoom::getChatRoomKey)
                .or(() -> {
                    if (createNewRoomIfNotExist) {
                        ChatRoomKey chatId = createChatId(senderId, receiverId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private ChatRoomKey createChatId(Long senderId, Long receiverId) {
        ChatRoomKey chatRoomKey = new ChatRoomKey(senderId, receiverId);

        ChatRoom senderReceiver = ChatRoom.builder()
                .chatRoomKey(chatRoomKey)
                .sender(userService.getUserById(senderId))
                .receiver(userService.getUserById(receiverId))
                .build();

        ChatRoom receiverSender = ChatRoom.builder()
                .chatRoomKey(chatRoomKey)
                .sender(userService.getUserById(receiverId))
                .receiver(userService.getUserById(senderId))
                .build();

        chatRoomRepository.save(senderReceiver);
        chatRoomRepository.save(receiverSender);

        return chatRoomKey;
    }
}

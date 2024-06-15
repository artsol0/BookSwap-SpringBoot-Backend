package com.artsolo.bookswap.chat.room;

import com.artsolo.bookswap.chat.room.dto.ChatRoomResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByParticipantId(userId);
        return chatRooms.stream()
                .map(chatRoom -> getChatRoomResponse(chatRoom, userId))
                .collect(Collectors.toList());
    }

    public ChatRoomResponse getChatRoomResponse(ChatRoom chatRoom, Long userId) {
        User participant = chatRoom.getFirstParticipant().getId().equals(userId) ?
                chatRoom.getSecondParticipant() : chatRoom.getFirstParticipant();

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .receiverId(participant.getId())
                .chatName(participant.getNickname())
                .photo(participant.getPhoto())
                .build();
    }

    public ChatRoom getChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Chat room", id));
    }

    public Optional<Long> getChatRoomId(Long senderId, Long receiverId, boolean createNewRoomIfNotExist) {
        return chatRoomRepository.findByParticipantIds(senderId, receiverId).map(ChatRoom::getId)
                .or(() -> {
                    if (createNewRoomIfNotExist) {
                        Long chatId = createChatId(senderId, receiverId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private Long createChatId(Long senderId, Long receiverId) {

        ChatRoom chatRoom = ChatRoom.builder()
                .firstParticipant(userService.getUserById(senderId))
                .secondParticipant(userService.getUserById(receiverId))
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }

    public boolean isUserChatParticipant(ChatRoom chatRoom, User user) {
        return (chatRoom.getFirstParticipant().getId().equals(user.getId()) || chatRoom.getSecondParticipant().getId().equals(user.getId()));
    }
}

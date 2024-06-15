package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.chat.message.ChatMessage;
import com.artsolo.bookswap.chat.message.ChatMessageRepository;
import com.artsolo.bookswap.chat.room.ChatRoom;
import com.artsolo.bookswap.chat.room.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

@DataJpaTest
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private final ChatRoom firstChat = new ChatRoom();
    private final ChatRoom secondChat = new ChatRoom();

    private final ChatMessage message1 = ChatMessage.builder().chatRoom(firstChat).build();
    private final ChatMessage message2 = ChatMessage.builder().chatRoom(firstChat).build();
    private final ChatMessage message3 = ChatMessage.builder().chatRoom(firstChat).build();

    private final ChatMessage message4 = ChatMessage.builder().chatRoom(secondChat).build();
    private final ChatMessage message5 = ChatMessage.builder().chatRoom(secondChat).build();

    @BeforeEach
    void setUp() {
        chatRoomRepository.save(firstChat);
        chatRoomRepository.save(secondChat);

        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);
        chatMessageRepository.save(message3);
        chatMessageRepository.save(message4);
        chatMessageRepository.save(message5);
    }

    @AfterEach
    void tearDown() {
        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }

    @Test
    void foundAllFirstChatMessages() {
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom(firstChat);
        assertThat(messages.size()).isEqualTo(3);
        messages.forEach(message -> assertThat(message.getChatRoom().getId()).isEqualTo(firstChat.getId()));
    }

    @Test
    void foundAllSecondChatMessages() {
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom(secondChat);
        assertThat(messages.size()).isEqualTo(2);
        messages.forEach(message -> assertThat(message.getChatRoom().getId()).isEqualTo(secondChat.getId()));
    }
}
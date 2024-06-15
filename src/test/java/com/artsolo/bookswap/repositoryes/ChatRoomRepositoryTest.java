package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.chat.room.ChatRoom;
import com.artsolo.bookswap.chat.room.ChatRoomRepository;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    private final User firstUser = new User();
    private final User secondUser = new User();
    private final User thirdUser = new User();

    private final ChatRoom firstChat = ChatRoom.builder().firstParticipant(firstUser).secondParticipant(secondUser).build();
    private final ChatRoom secondChat = ChatRoom.builder().firstParticipant(firstUser).secondParticipant(thirdUser).build();

    @BeforeEach
    void setUp() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);

        chatRoomRepository.save(firstChat);
        chatRoomRepository.save(secondChat);
    }

    @AfterEach
    void tearDown() {
        chatRoomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void foundAllFirstUserChats() {
        List<ChatRoom> chats = chatRoomRepository.findAllByParticipantId(firstUser.getId());
        assertThat(chats.size()).isEqualTo(2);
        chats.forEach(chat -> assertThat(chat.getFirstParticipant().getId()).isEqualTo(firstUser.getId()));
    }

    @Test
    void foundAllSecondUserChats() {
        List<ChatRoom> chats = chatRoomRepository.findAllByParticipantId(secondUser.getId());
        assertThat(chats.size()).isEqualTo(1);
        chats.forEach(chat -> assertThat(chat.getSecondParticipant().getId()).isEqualTo(secondUser.getId()));
    }

    @Test
    void foundChatRoomByFirstUserAndSecondUser() {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByParticipantIds(secondUser.getId(), firstUser.getId());
        assertThat(chatRoom.isPresent()).isTrue();
    }

    @Test
    void chatRoomNotFoundBySecondUserAndThirdUser() {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByParticipantIds(secondUser.getId(), thirdUser.getId());
        assertThat(chatRoom.isPresent()).isFalse();
    }

}
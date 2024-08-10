package com.capstone_ex.chat_server.DAO.ChatRoom;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.ChatRoomRepository;
import com.capstone_ex.chat_server.Repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomDAOImpl implements ChatRoomDAO {

    private final ChatRoomRepository chatRoomRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public ChatRoomEntity createChatRoom(String chatName, String description, String creatorId) {
        UserInfoEntity creator = userInfoRepository.findByUserId(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator ID is invalid or does not exist."));

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatName(chatName)
                .description(description)
                .creator(creator)  // creator를 설정
                .users(new HashSet<>()) // 초기 users 설정
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoomEntity getChatRoomById(Long chatRoomId) {
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findById(chatRoomId);
        return chatRoom.orElse(null);
    }

    @Override
    public List<ChatRoomEntity> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    @Override
    public void addUserToChatRoom(UserInfoEntity user, Long chatRoomId) {
        ChatRoomEntity chatRoom = getChatRoomById(chatRoomId);
        if (chatRoom != null) {
            chatRoom.getUsers().add(user);
            chatRoomRepository.save(chatRoom);

            // user_chat_room 테이블에 사용자와 채팅방 ID를 저장
            // 이 부분이 사용자와 채팅방 간의 관계를 저장하는 로직이 됩니다.
        }
    }

    @Override
    public void removeUserFromChatRoom(UserInfoEntity user, Long chatRoomId) {
        ChatRoomEntity chatRoom = getChatRoomById(chatRoomId);
        if (chatRoom != null) {
            chatRoom.getUsers().remove(user);
            chatRoomRepository.save(chatRoom);
        }
    }
}

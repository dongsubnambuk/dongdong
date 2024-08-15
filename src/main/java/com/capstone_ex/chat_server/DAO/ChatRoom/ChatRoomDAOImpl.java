package com.capstone_ex.chat_server.DAO.ChatRoom;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.ChatRoomRepository;
import com.capstone_ex.chat_server.Repository.UserInfoRepository;
import com.capstone_ex.chat_server.Service.UserInfo.UserInfoService;
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
    private final UserInfoService userInfoService;

    @Override
    public ChatRoomEntity createChatRoom(String chatName, String creatorId) {
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatName(chatName)
                .creatorId(creatorId)  // creatorId를 String으로 설정
                .users(new HashSet<>()) // 초기 users 설정
                .build();
        UserInfoEntity creator = userInfoRepository.findByUniqueId(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("User ID is invalid or does not exist."));
        chatRoom.getUsers().add(creator);
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
    public void addUserToChatRoom(String uniqueId, Long chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat Room ID is invalid or does not exist."));

        UserInfoEntity user = userInfoRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new IllegalArgumentException("User ID is invalid or does not exist."));

        chatRoom.getUsers().add(user);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public void removeUserFromChatRoom(UserInfoEntity user, Long chatRoomId) {
        ChatRoomEntity chatRoom = getChatRoomById(chatRoomId);
        if (chatRoom != null) {
            chatRoom.getUsers().remove(user);
            chatRoomRepository.save(chatRoom);
        }
    }

    @Override
    public List<ChatRoomEntity> findChatRoomsByUserUniqueId(String uniqueId) {
        UserInfoEntity user = userInfoRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return chatRoomRepository.findByUsersContaining(user);
    }
}

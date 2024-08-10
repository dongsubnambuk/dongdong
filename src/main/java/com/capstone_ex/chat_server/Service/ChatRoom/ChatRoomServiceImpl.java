package com.capstone_ex.chat_server.Service.ChatRoom;

import com.capstone_ex.chat_server.DAO.ChatRoom.ChatRoomDAO;
import com.capstone_ex.chat_server.DAO.User.UserInfoDAO;
import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomDAO chatRoomDAO;
    private final UserInfoDAO userInfoDAO;

    @Override
    public ChatRoomEntity createChatRoom(String chatName, String description, String creatorId) {
        UserInfoEntity creator = userInfoDAO.getUserById(creatorId);

        if (creator == null) {
            throw new IllegalArgumentException("Creator not found");
        }

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatName(chatName)
                .description(description)
                .creator(creator)
                .users(new HashSet<>())  // 이 부분을 추가하여 users를 초기화
                .build();

        chatRoom.getUsers().add(creator);

        return chatRoomRepository.save(chatRoom);
    }


    @Override
    public ChatRoomEntity getChatRoomById(Long chatRoomId) {
        return chatRoomDAO.getChatRoomById(chatRoomId);
    }

    @Override
    public List<ChatRoomDTO> getAllChatRooms() {
        List<ChatRoomEntity> chatRoomEntities = chatRoomDAO.getAllChatRooms();
        return chatRoomEntities.stream()
                .map(ChatRoomDTO::new) // ChatRoomEntity를 ChatRoomDTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomDAO.deleteChatRoom(chatRoomId);
    }

    @Override
    public void addUserToChatRoom(String userId, Long chatRoomId) {
        UserInfoEntity user = userInfoDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User ID is invalid or does not exist.");
        }
        chatRoomDAO.addUserToChatRoom(user, chatRoomId);
    }

    @Override
    public void removeUserFromChatRoom(String userId, Long chatRoomId) {
        UserInfoEntity user = userInfoDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User ID is invalid or does not exist.");
        }
        chatRoomDAO.removeUserFromChatRoom(user, chatRoomId);
    }
}

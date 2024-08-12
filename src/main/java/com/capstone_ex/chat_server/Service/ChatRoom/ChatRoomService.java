package com.capstone_ex.chat_server.Service.ChatRoom;

import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;

import java.util.List;

public interface ChatRoomService {
    ChatRoomEntity createChatRoom(String chatName, String creatorId);

    ChatRoomEntity getChatRoomById(Long chatRoomId);

    List<ChatRoomDTO> getAllChatRooms();

    void deleteChatRoom(Long chatRoomId);

    void addUserToChatRoom(String userId, Long chatRoomId);

    void removeUserFromChatRoom(String userId, Long chatRoomId);
}

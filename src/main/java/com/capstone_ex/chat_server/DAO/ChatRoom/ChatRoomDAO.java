package com.capstone_ex.chat_server.DAO.ChatRoom;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;

import java.util.List;

public interface ChatRoomDAO {
    ChatRoomEntity createChatRoom(String chatName, String creatorId);
    ChatRoomEntity getChatRoomById(Long chatRoomId);
    List<ChatRoomEntity> getAllChatRooms();
    void deleteChatRoom(Long chatRoomId);
    void addUserToChatRoom(String uniqueId, Long chatRoomId);
    void removeUserFromChatRoom(UserInfoEntity user, Long chatRoomId);
}
